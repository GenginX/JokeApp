package com.kaczmar.JokesApp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaczmar.JokesApp.model.Joke;
import com.kaczmar.JokesApp.model.JokeException;
import com.kaczmar.JokesApp.services.JokeDispatcherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class JokeController {

    private final JokeDispatcherService jokeDispatcherService;

    public JokeController(JokeDispatcherService jokeDispatcherService) {
        this.jokeDispatcherService = jokeDispatcherService;
    }

    @GetMapping("/random")
    public ResponseEntity<Joke> getRandomJoke() throws JokeException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(jokeDispatcherService.provideRandomJoke());
    }

    @GetMapping("/categories")
    public ResponseEntity<Set<String>> getJokeCategory() throws JokeException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(jokeDispatcherService.provideJokeCategories());
    }

    @GetMapping("/{category}/random")
    public ResponseEntity<Joke> getRandomJokeFromCategory(@PathVariable("category") String category) throws JokeException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(jokeDispatcherService.provideRandomJoke(category));
    }

}
