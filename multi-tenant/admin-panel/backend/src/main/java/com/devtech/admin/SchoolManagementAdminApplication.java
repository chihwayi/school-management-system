package com.devtech.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.devtech.admin.repository")
@EntityScan(basePackages = "com.devtech.admin.entity")
@EnableScheduling
public class SchoolManagementAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolManagementAdminApplication.class, args);
    }
}

