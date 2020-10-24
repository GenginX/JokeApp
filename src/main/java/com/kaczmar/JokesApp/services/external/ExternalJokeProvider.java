package com.kaczmar.JokesApp.services.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaczmar.JokesApp.model.Joke;
import com.kaczmar.JokesApp.model.JokeException;

import java.util.Set;

public interface ExternalJokeProvider {

    public Joke getRandomJoke() throws JokeException;

    public Joke getRandomJokeFromCategory(String category) throws JokeException;

    public Set<String> getAvailableCategories() throws JokeException;
}
