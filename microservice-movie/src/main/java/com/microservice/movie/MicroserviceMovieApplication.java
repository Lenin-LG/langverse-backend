package com.microservice.movie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MicroserviceMovieApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceMovieApplication.class, args);
	}

}
