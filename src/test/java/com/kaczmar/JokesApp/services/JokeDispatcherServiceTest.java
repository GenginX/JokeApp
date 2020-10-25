package com.kaczmar.JokesApp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaczmar.JokesApp.model.Joke;
import com.kaczmar.JokesApp.model.JokeException;
import com.kaczmar.JokesApp.services.external.ChuckNorrisJokeProvider;
import com.kaczmar.JokesApp.services.external.ExternalJokeProvider;
import com.kaczmar.JokesApp.services.external.Sv443JokeProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JokeDispatcherServiceTest {

    private JokeDispatcherService jokeDispatcherService;
    private List<ExternalJokeProvider> externalJokeProviders = new ArrayList<>();


    @AfterEach
    void tearDown() {
        externalJokeProviders.clear();
    }

    @Test
    void shouldCallAllGetAvailableCategoriesMethods() throws JokeException {
        Sv443JokeProvider sv443JokeProvider = Mockito.mock(Sv443JokeProvider.class);
        ChuckNorrisJokeProvider chuckNorrisJokeProvider = Mockito.mock(ChuckNorrisJokeProvider.class);

        externalJokeProviders.add(sv443JokeProvider);
        externalJokeProviders.add(chuckNorrisJokeProvider);

        jokeDispatcherService = new JokeDispatcherService(externalJokeProviders, new RandomnessProvider());

        //when
        jokeDispatcherService.provideJokeCategories();

        //then
        Mockito.verify(sv443JokeProvider, Mockito.times(1)).getAvailableCategories();
        Mockito.verify(chuckNorrisJokeProvider, Mockito.times(1)).getAvailableCategories();
    }

    @Test
    void shouldProvideCombinedDistinctElementSet() throws JokeException {
        Sv443JokeProvider sv443JokeProvider = Mockito.mock(Sv443JokeProvider.class);
        ChuckNorrisJokeProvider chuckNorrisJokeProvider = Mockito.mock(ChuckNorrisJokeProvider.class);
        Mockito.when(sv443JokeProvider.getAvailableCategories()).thenReturn(new HashSet<>(Arrays.asList("fashion", "food")));
        Mockito.when(chuckNorrisJokeProvider.getAvailableCategories()).thenReturn(new HashSet<>(Arrays.asList("fashion", "political")));

        externalJokeProviders.add(sv443JokeProvider);
        externalJokeProviders.add(chuckNorrisJokeProvider);

        jokeDispatcherService = new JokeDispatcherService(externalJokeProviders, new RandomnessProvider());

        //when
        final Set<String> set = jokeDispatcherService.provideJokeCategories();

        //then
        assertThat(set).size().isEqualTo(3);
        assertThat(set).containsExactlyInAnyOrder("fashion", "food", "political");

    }

    @Test
    void shouldProvideRandomJokeWhenAllServicesAreAvailable() throws JokeException {
        //given
        Sv443JokeProvider sv443JokeProvider = Mockito.mock(Sv443JokeProvider.class);
        ChuckNorrisJokeProvider chuckNorrisJokeProvider = Mockito.mock(ChuckNorrisJokeProvider.class);

        Mockito.when(sv443JokeProvider.getRandomJoke()).thenReturn(Mockito.mock(Joke.class));
        Mockito.when(chuckNorrisJokeProvider.getRandomJoke()).thenReturn(Mockito.mock(Joke.class));

        externalJokeProviders.add(sv443JokeProvider);
        externalJokeProviders.add(chuckNorrisJokeProvider);
        jokeDispatcherService = new JokeDispatcherService(externalJokeProviders, new RandomnessProvider());

        //when
        jokeDispatcherService.provideRandomJoke();

        //then
        if (Mockito.mockingDetails(sv443JokeProvider).getInvocations().isEmpty()) {
            Mockito.verify(chuckNorrisJokeProvider, Mockito.times(1)).getRandomJoke();
        } else {
            Mockito.verify(sv443JokeProvider, Mockito.times(1)).getRandomJoke();
        }
    }

    @Test
    void shouldProvideRandomJokeWhenNotAllServicesAreAvailable() throws Throwable {
        //given
        Sv443JokeProvider sv443JokeProvider = Mockito.mock(Sv443JokeProvider.class);
        ChuckNorrisJokeProvider chuckNorrisJokeProvider = Mockito.mock(ChuckNorrisJokeProvider.class);
        RandomnessProvider randomnessProvider = Mockito.mock(RandomnessProvider.class);

        externalJokeProviders.add(sv443JokeProvider);
        externalJokeProviders.add(chuckNorrisJokeProvider);

        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);

        Mockito.when(sv443JokeProvider.getRandomJoke()).thenThrow(new JokeException("Service is unavailable"));
        Mockito.when(chuckNorrisJokeProvider.getRandomJoke()).thenReturn(Mockito.mock(Joke.class));
        Mockito.when(randomnessProvider.determineCallingOrder(Mockito.anyInt())).thenReturn(list);

        jokeDispatcherService = new JokeDispatcherService(externalJokeProviders, randomnessProvider);

        //when
        Executable executable = () -> jokeDispatcherService.provideRandomJoke();
        executable.execute();

        //then
        Mockito.verify(chuckNorrisJokeProvider, Mockito.times(1)).getRandomJoke();
        Mockito.verify(sv443JokeProvider, Mockito.times(1)).getRandomJoke();
        assertDoesNotThrow(executable);
    }
}