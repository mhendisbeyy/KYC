package com.tr.kyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class KycApplication {

	public static void main(String[] args) {
		SpringApplication.run(KycApplication.class, args);
	}

}
