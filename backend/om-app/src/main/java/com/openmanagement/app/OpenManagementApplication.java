package com.openmanagement.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.openmanagement")
@MapperScan("com.openmanagement.**.mapper")
public class OpenManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenManagementApplication.class, args);
    }
}
