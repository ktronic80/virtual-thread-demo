package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {


    @GetMapping("/data")
    public String getData() throws InterruptedException  {
        Thread.sleep(5000);
        return "Hello World";
    }

}