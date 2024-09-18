package com.brettstine.social_game_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SocialGameBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialGameBackendApplication.class, args);
	}

}
