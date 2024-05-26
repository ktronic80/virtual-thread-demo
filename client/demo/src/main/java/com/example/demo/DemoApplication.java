package com.example.demo;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync(proxyTargetClass=true)
public class DemoApplication {


	public static void main(String[] args) {
		new SpringApplicationBuilder(DemoApplication.class)
          .web(WebApplicationType.NONE)
          .run(args);
	}


}
