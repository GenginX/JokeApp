package com.kaczmar.JokesApp.services;

import com.kaczmar.JokesApp.services.external.ExternalJokeProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Service
public class RandomnessProvider {

    public List<Integer> determineCallingOrder(int size) {
        List<Integer> callingOrderList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            callingOrderList.add(i);
        }
        Collections.shuffle(callingOrderList);
        return callingOrderList;
    }

}
