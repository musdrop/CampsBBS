package com.bbsserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BbsAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(BbsAdminApplication.class, args);
    }

}
