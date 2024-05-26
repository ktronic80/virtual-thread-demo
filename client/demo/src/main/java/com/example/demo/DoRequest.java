package com.example.demo;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DoRequest implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(DoRequest.class);
 
    public String doRequest() {
        try {
            logger.debug("Thread: " + Thread.currentThread().toString());
            var url = new URL("http://localhost:8080/data");
            var con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(5000);
            con.setReadTimeout(10000);

            var response = con.getInputStream().readAllBytes();
            con.disconnect();
            var body = new String(response);
            return body;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<String> doRequestClassic() {
        var response = doRequest();
        return CompletableFuture.completedFuture(response);
    }

    @Async("virtualThreadTaskExecutor")
    public CompletableFuture<String> doRequestVirtual() {
        var response = doRequest();
        return CompletableFuture.completedFuture(response);
    }

    @Override
    public void run() {
        doRequest();
    }

}
