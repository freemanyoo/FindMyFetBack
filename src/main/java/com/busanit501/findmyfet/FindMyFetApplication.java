package com.busanit501.findmyfet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class FindMyFetApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindMyFetApplication.class, args);
    }

}
