package com.movieflix;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MovieApiApplication {
	@Bean
	public ModelMapper   modelMapper(){
		return new ModelMapper();
	}
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		// Add any custom configurations here
		return objectMapper;
	}

	public static void main(String[] args) {
		SpringApplication.run(MovieApiApplication.class, args);
	}

}
