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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class Sv443JokeProvider implements ExternalJokeProvider {

    private static final String baseUrl = "https://sv443.net/jokeapi/v2/";
    private static final String anyJokePath = "joke/Any";
    private static final String categoryPath = "joke/%s";
    private static final String categoryList = "categories";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private JsonNode jsonNode;

    public Sv443JokeProvider(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Joke getRandomJoke() throws JokeException {
        ResponseEntity<String> randomJokeEntity = restTemplate.getForEntity(baseUrl + anyJokePath, String.class);

        isStatusCodeOk(randomJokeEntity);

        final String body = randomJokeEntity.getBody();
        final JsonNode jsonNode = getJsonNodeOfBody(body);

        String jokeValue = getJokeValue(jsonNode);
        String category = jsonNode.path("category").asText();

        return new Joke(category,jokeValue);
    }

    @Override
    public Joke getRandomJokeFromCategory(String category) throws JokeException {
        ResponseEntity<String> randomJokeEntity = restTemplate.getForEntity(baseUrl + String.format(categoryPath, category), String.class);

        isStatusCodeOk(randomJokeEntity);

        final String body = randomJokeEntity.getBody();
        final JsonNode jsonNode = getJsonNodeOfBody(body);

        String jokeValue = getJokeValue(jsonNode);

        return new Joke(category, jokeValue);
    }

    @Override
    public Set<String> getAvailableCategories() throws JokeException {
        ResponseEntity<String> categoriesEntity = restTemplate.getForEntity(baseUrl + categoryList, String.class);

        isStatusCodeOk(categoriesEntity);

        final String body = categoriesEntity.getBody();
        final JsonNode jsonNode = getJsonNodeOfBody(body);

        JsonNode categories = jsonNode.path("categories");

        Iterator<JsonNode> elements = categories.elements();

        HashSet<String> categoriesResult = new HashSet<>();

        while(elements.hasNext()){
            categoriesResult.add(elements.next().asText());
        }

        return categoriesResult;
    }

    private void isStatusCodeOk(ResponseEntity<String> categoriesEntity) throws JokeException {
        if (categoriesEntity.getStatusCode() != HttpStatus.OK) {
            throw new JokeException("Service is unavailable");
        }
    }

    private String getJokeValue(JsonNode jsonNode) {
        String joke = jsonNode.path("joke").asText();
        if(joke == null || joke.isEmpty()) {
            final String setup = jsonNode.path("setup").asText();
            final String delivery = jsonNode.path("delivery").asText();
            joke = setup + "\n\n" + delivery;
        }
        return joke;
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
}
