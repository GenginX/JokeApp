package com.kaczmar.JokesApp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaczmar.JokesApp.model.Joke;
import com.kaczmar.JokesApp.model.JokeException;
import com.kaczmar.JokesApp.services.external.ExternalJokeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class JokeDispatcherService {

    @Autowired
    List<ExternalJokeProvider> externalJokeProvider;


    public Joke provideRandomJoke() throws JokeException {
        return externalJokeProvider.get(getRandomNumber()).getRandomJoke();
    }

    public Joke provideRandomJoke(String category) throws JokeException {
        return externalJokeProvider.get(getRandomNumber()).getRandomJokeFromCategory(category);
    }

    public Set<String> provideJokeCategories() throws JokeException {
        return externalJokeProvider.get(getRandomNumber()).getAvailableCategories();
    }

    private int getRandomNumber(){
        int size = externalJokeProvider.size();
        return (int) Math.floor(Math.random() * size);
    }

}
