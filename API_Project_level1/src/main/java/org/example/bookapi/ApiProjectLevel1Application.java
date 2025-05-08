package org.example.bookapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ApiProjectLevel1Application {

    public static void main(String[] args) {
        SpringApplication.run(ApiProjectLevel1Application.class, args);
    }

}
