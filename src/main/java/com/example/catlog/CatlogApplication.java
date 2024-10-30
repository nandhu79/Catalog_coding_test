package com.example.catlog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CatlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatlogApplication.class, args);

		// Call the SecretSharing logic for both input files
		SecretSharing.runSecretSharing("input1.json");
		SecretSharing.runSecretSharing("input2.json");
	}
}