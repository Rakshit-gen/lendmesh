package com.lendmesh.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LendMeshApplication {

    public static void main(String[] args) {
        SpringApplication.run(LendMeshApplication.class, args);
    }
}
