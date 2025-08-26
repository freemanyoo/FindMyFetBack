package com.busanit501.findmyfet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // 추가함_250824
@SpringBootApplication
public class FindMyFetApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindMyFetApplication.class, args);
    }

}
