package com.smartbiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SmartBizApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartBizApplication.class, args);
    }
}
