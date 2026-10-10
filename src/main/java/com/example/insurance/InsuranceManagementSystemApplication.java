package com.example.insurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InsuranceManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(InsuranceManagementSystemApplication.class, args);
	}

}
