package com.kaczmar.JokesApp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaczmar.JokesApp.services.external.ChuckNorrisJokeProvider;
import com.kaczmar.JokesApp.services.external.ExternalJokeProvider;
import com.kaczmar.JokesApp.services.external.Sv443JokeProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JokeDispatcherServiceTest {

    private JokeDispatcherService jokeDispatcherService;
    private List<ExternalJokeProvider> externalJokeProviders = new ArrayList<>();


    @AfterEach
    void tearDown(){
        externalJokeProviders.clear();
    }

    @Test
    void shouldCallAllGetAvailableCategoriesMethods() {
        Sv443JokeProvider sv443JokeProvider = Mockito.mock(Sv443JokeProvider.class);
        ChuckNorrisJokeProvider chuckNorrisJokeProvider = Mockito.mock(ChuckNorrisJokeProvider.class);

        externalJokeProviders.add(sv443JokeProvider);
        externalJokeProviders.add(chuckNorrisJokeProvider);

        jokeDispatcherService = new JokeDispatcherService(externalJokeProviders);

        //when
        jokeDispatcherService.provideJokeCategories();

        //then
        Mockito.verify(sv443JokeProvider, Mockito.times(1)).getAvailableCategories();
        Mockito.verify(chuckNorrisJokeProvider, Mockito.times(1)).getAvailableCategories();
    }

    @Test
    void shouldProvideCombinedDistinctElementSet(){
        Sv443JokeProvider sv443JokeProvider = Mockito.mock(Sv443JokeProvider.class);
        ChuckNorrisJokeProvider chuckNorrisJokeProvider = Mockito.mock(ChuckNorrisJokeProvider.class);
        Mockito.when(sv443JokeProvider.getAvailableCategories()).thenReturn(new HashSet<>(Arrays.asList("fashion", "food")));
        Mockito.when(chuckNorrisJokeProvider.getAvailableCategories()).thenReturn(new HashSet<>(Arrays.asList("fashion", "political")));

        externalJokeProviders.add(sv443JokeProvider);
        externalJokeProviders.add(chuckNorrisJokeProvider);

        jokeDispatcherService = new JokeDispatcherService(externalJokeProviders);

        //when
        final Set<String> set = jokeDispatcherService.provideJokeCategories();

        //then
        assertThat(set).size().isEqualTo(3);
        assertThat(set).containsExactlyInAnyOrder("fashion","food","political");

    }
}