package com.test.rockpaperscissors.server.service.ai;

import com.test.rockpaperscissors.server.model.CurrentState;
import com.test.rockpaperscissors.server.model.Gesture;
import com.test.rockpaperscissors.server.model.GestureStatistics;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.test.rockpaperscissors.server.model.Gesture.PAPER;
import static com.test.rockpaperscissors.server.model.Gesture.ROCK;
import static com.test.rockpaperscissors.server.model.Gesture.SCISSORS;

@Getter
public class MarkovChain {
    private Map<CurrentState, Map<Gesture, GestureStatistics>> transitionProbabilities;

    public MarkovChain() {
        transitionProbabilities = new ConcurrentHashMap<>();
        transitionProbabilities.put(new CurrentState(PAPER, PAPER), createGestureStatistics());
        transitionProbabilities.put(new CurrentState(PAPER, ROCK), createGestureStatistics());
        transitionProbabilities.put(new CurrentState(PAPER, SCISSORS), createGestureStatistics());
        transitionProbabilities.put(new CurrentState(ROCK, PAPER), createGestureStatistics());
        transitionProbabilities.put(new CurrentState(ROCK, ROCK), createGestureStatistics());
        transitionProbabilities.put(new CurrentState(ROCK, SCISSORS), createGestureStatistics());
        transitionProbabilities.put(new CurrentState(SCISSORS, PAPER), createGestureStatistics());
        transitionProbabilities.put(new CurrentState(SCISSORS, ROCK), createGestureStatistics());
        transitionProbabilities.put(new CurrentState(SCISSORS, SCISSORS), createGestureStatistics());
    }

    private Map<Gesture, GestureStatistics> createGestureStatistics() {
        final ConcurrentHashMap<Gesture, GestureStatistics> stats = new ConcurrentHashMap<>();
        stats.put(PAPER, new GestureStatistics());
        stats.put(ROCK, new GestureStatistics());
        stats.put(SCISSORS, new GestureStatistics());
        return stats;
    }

}
