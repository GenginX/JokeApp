package com.kaczmar.JokesApp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaczmar.JokesApp.services.external.ChuckNorrisJokeProvider;
import com.kaczmar.JokesApp.services.external.ExternalJokeProvider;
import com.kaczmar.JokesApp.services.external.Sv443JokeProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class JokesAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(JokesAppApplication.class, args);
	}

	@Bean
	public RestTemplate providedRestTemplate(){
		return new RestTemplate();
	}

	@Bean
	public ObjectMapper providedObjectMapper(){
		return new ObjectMapper();
	}

	@Bean List<ExternalJokeProvider> provideExternalJokeImplementations(final RestTemplate restTemplate, final ObjectMapper objectMapper){
		List<ExternalJokeProvider> list = new ArrayList<>();
		list.add(new Sv443JokeProvider(restTemplate, objectMapper));
		list.add(new ChuckNorrisJokeProvider(restTemplate, objectMapper));
		return list;
	}

}
