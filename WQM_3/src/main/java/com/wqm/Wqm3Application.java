package com.wqm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Wqm3Application {

	public static void main(String[] args) {
		SpringApplication.run(Wqm3Application.class, args);
	}

}
