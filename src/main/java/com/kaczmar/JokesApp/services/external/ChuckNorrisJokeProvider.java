package com.kaczmar.JokesApp.services.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaczmar.JokesApp.model.Joke;
import com.kaczmar.JokesApp.model.JokeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Service
public class ChuckNorrisJokeProvider implements ExternalJokeProvider {

    private final String baseUrl = "https://api.chucknorris.io/";
    private final String randomJokeUrl = "jokes/random";
    private final String categories = "jokes/categories";
    private final String randomJokeByCategoryUrl = "jokes/random?category=";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ChuckNorrisJokeProvider(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Joke getRandomJoke() throws JokeException {
        String randomCategory = getRandomCategory();
        ResponseEntity<String> randomJoke = restTemplate.getForEntity(baseUrl + randomJokeByCategoryUrl + randomCategory, String.class);
        isStatusCodeOk(randomJoke);

        String randomJokeBody = randomJoke.getBody();

        return new Joke(randomCategory, randomJokeBody);

    }

    @Override
    public Joke getRandomJokeFromCategory(String category) throws JokeException {
        ResponseEntity<String> randomJoke = restTemplate.getForEntity(baseUrl + randomJokeByCategoryUrl + category, String.class);
        isStatusCodeOk(randomJoke);

        String randomJokeBody = randomJoke.getBody();

        return new Joke(category, randomJokeBody);
    }

    @Override
    public Set<String> getAvailableCategories() throws JokeException {
        ResponseEntity<String> categoryList = restTemplate.getForEntity(baseUrl + categories, String.class);
        isStatusCodeOk(categoryList);
        String[] categoriesFromBody = categoryList.getBody().split("\n");
        HashSet<String> categories = new HashSet<>();

        categories.addAll(Arrays.asList(categoriesFromBody));

        return categories;
    }


    private String getRandomCategory() throws JokeException {
        ResponseEntity<String> categoryList = restTemplate.getForEntity(baseUrl + categories, String.class);
        isStatusCodeOk(categoryList);

        String[] body = categoryList.getBody().split("\n");

        int randomNumber = (int) Math.floor(Math.random() * body.length);

        return body[randomNumber];
    }

    private JsonNode getJsonNodeOfBody(String body) throws JokeException {
        final JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(body);
        } catch (JsonProcessingException e) {
            throw new JokeException("response body is not a Json");
        }
        return jsonNode;
    }

    private void isStatusCodeOk(ResponseEntity<String> jokeEntity) throws JokeException {
        if(jokeEntity.getStatusCode() != HttpStatus.OK){
            throw new JokeException("Service is unavailable");
        }
    }
}
