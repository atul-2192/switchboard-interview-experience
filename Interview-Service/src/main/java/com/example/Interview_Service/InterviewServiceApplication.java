package com.example.Interview_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class InterviewServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterviewServiceApplication.class, args);
	}

}
