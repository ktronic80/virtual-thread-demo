package com.example.demo;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

    private final TaskExecutor threadPoolTaskExecutor;
    private final TaskExecutor virtualThreadExecutor;
    private final DoRequest doRequest;

    public AppRunner(
            DoRequest doRequest,
            @Qualifier("threadPoolTaskExecutor") TaskExecutor threadPoolTaskExecutor,
            @Qualifier("virtualThreadTaskExecutor") TaskExecutor virtualThreadExecutor
    ) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.virtualThreadExecutor = virtualThreadExecutor;
        this.doRequest = doRequest;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Start with number of cores. Time should be the same for virtual and platform threads up to max number of cores.
        // After that, virtual threads should be faster.
        var threadCount = Integer.parseInt(args.getOptionValues("threads").get(0));
        var whatToRun = args.getNonOptionArgs();
		logger.info("leng {}", whatToRun.size());
        whatToRun.stream().forEach(arg -> {
            switch (arg) {
                case "classicFuture" ->
                    runFutureDirectly(threadCount, threadPoolTaskExecutor, arg);
                case "classicSpring" ->
                    runAsyncMethods(threadCount, doRequest::doRequestClassic, arg);
                case "virtualFuture" ->
                    runFutureDirectly(threadCount, virtualThreadExecutor, arg);
                case "virtualSpring" ->
                    runAsyncMethods(threadCount, doRequest::doRequestVirtual, arg);
                default ->  logger.error("Unknown option: {}", arg);
            }

        });
		logger.info("All done");
		System.exit(0);
    }

    private void runAsyncMethods(int count, Supplier<CompletableFuture<String>> asyncAction, String type) {
		logger.info("Running {} with {} threads", type, count);
        final var allRequests = new CompletableFuture[count];
        runTimed(() -> {
            for (int i = 0; i < count; i++) {
                allRequests[i] = asyncAction.get();
            }
            CompletableFuture.allOf(allRequests).join();
			logger.info("--- All requests completed ---");
			return null;
        });
    }

    private void runFutureDirectly(int count, TaskExecutor executor, String type) {
		logger.info("Running {} with {} threads", count, type);
        final var allRequests = new CompletableFuture[count];
        runTimed(() -> {
            for (int i = 0; i < count; i++) {
                allRequests[i] = CompletableFuture.runAsync(new DoRequest(), executor);
            }
            CompletableFuture.allOf(allRequests).join();
			logger.info("--- All requests completed ---");
			return null;
        });
    }

    private void runTimed(Supplier<Void> actionToTime) {
        long start = System.currentTimeMillis();
        actionToTime.get();
        logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
    }
}
