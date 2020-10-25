package com.kaczmar.JokesApp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaczmar.JokesApp.model.Joke;
import com.kaczmar.JokesApp.model.JokeException;
import com.kaczmar.JokesApp.services.external.ExternalJokeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JokeDispatcherService {

    private final List<ExternalJokeProvider> externalJokeProvider;
    private RandomnessProvider randomnessProvider;

    public JokeDispatcherService(List<ExternalJokeProvider> externalJokeProvider, RandomnessProvider randomnessProvider) {
        this.externalJokeProvider = externalJokeProvider;
        this.randomnessProvider = randomnessProvider;
    }

    public Joke provideRandomJoke() throws JokeException {
        final List<Integer> callingOrder = randomnessProvider.determineCallingOrder(externalJokeProvider.size());
        return getJokeOrCallAnotherServiceInCaseOfError(callingOrder);
    }

    public Joke provideRandomJoke(String category) throws JokeException {
        if(externalJokeProvider.get(1).getAvailableCategories().contains(category)){
            return externalJokeProvider.get(1).getRandomJokeFromCategory(category);
        }else if(externalJokeProvider.get(0).getAvailableCategories().contains(category)){
            return externalJokeProvider.get(0).getRandomJokeFromCategory(category);
        }
        return null;
    }

    public Set<String> provideJokeCategories() throws JokeException {
        Set<String> set = new HashSet<>();
        for (ExternalJokeProvider jokeProvider : externalJokeProvider) {
            Set<String> availableCategories = jokeProvider.getAvailableCategories();
            set.addAll(availableCategories);
        }
        return set;
    }

    private int getRandomNumber(){
        int size = externalJokeProvider.size();
        return (int) Math.floor(Math.random() * size);
    }

    private Joke getJokeOrCallAnotherServiceInCaseOfError(List<Integer> callingOrder) throws JokeException {
        try{
            return externalJokeProvider.get(callingOrder.get(0)).getRandomJoke();
        } catch (JokeException e){
            callingOrder.remove(0);
            if(callingOrder.size() == 0){
                throw new JokeException("All services are unabailable");
            }
            getJokeOrCallAnotherServiceInCaseOfError(callingOrder);
        }
        return null;
    }


}
