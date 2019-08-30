package com.test.rockpaperscissors.service.ai;

import com.test.rockpaperscissors.model.CurrentState;
import com.test.rockpaperscissors.model.Gesture;
import com.test.rockpaperscissors.model.GestureStatistics;
import lombok.Data;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.test.rockpaperscissors.model.Gesture.*;

@Data
public class MarkovChain {
    public static final double DOUBLE_COMPARISON_PRECISION = 0.000000001;

    private final RandomAi randomAi;

    public MarkovChain(RandomAi randomAi) {
        this.randomAi = randomAi;
    }

    private int order;
    private float decay;
    private Map<CurrentState, Map<Gesture, GestureStatistics>> statsForGesture;

    public Map<CurrentState, Map<Gesture, GestureStatistics>> createProbabilitiesMatrix() {
        statsForGesture = new ConcurrentHashMap<>();
        final Map<CurrentState, Map<Gesture, GestureStatistics>> transitionProbabilities = new ConcurrentHashMap<>();
        transitionProbabilities.put(new CurrentState(PAPER, PAPER), createGestureStatistics());
        transitionProbabilities.put(new CurrentState(PAPER, ROCK), createGestureStatistics());
        transitionProbabilities.put(new CurrentState(PAPER, SCISSORS), createGestureStatistics());
        transitionProbabilities.put(new CurrentState(ROCK, PAPER), createGestureStatistics());
        transitionProbabilities.put(new CurrentState(ROCK, ROCK), createGestureStatistics());
        transitionProbabilities.put(new CurrentState(ROCK, SCISSORS), createGestureStatistics());
        transitionProbabilities.put(new CurrentState(SCISSORS, PAPER), createGestureStatistics());
        transitionProbabilities.put(new CurrentState(SCISSORS, ROCK), createGestureStatistics());
        transitionProbabilities.put(new CurrentState(SCISSORS, SCISSORS), createGestureStatistics());
        return transitionProbabilities;
    }

    private Map<Gesture, GestureStatistics> createGestureStatistics() {
        final ConcurrentHashMap<Gesture, GestureStatistics> stats = new ConcurrentHashMap<>();
        stats.put(PAPER, new GestureStatistics());
        stats.put(ROCK, new GestureStatistics());
        stats.put(SCISSORS, new GestureStatistics());
        return stats;
    }

    //todo tests
    public void updateProbabilitiesMatrix(CurrentState state, Gesture userInput) {
        Map<Gesture, GestureStatistics> gestureWithStats = this.statsForGesture.get(state);
//        GestureStatistics statForGesture = gestureWithStats.get(userInput);
        for (Map.Entry<Gesture, GestureStatistics> entry : gestureWithStats.entrySet()) {
            entry.getValue().setNumberOfObservations(decay * entry.getValue().getNumberOfObservations());
        }

        gestureWithStats.get(userInput).setNumberOfObservations(gestureWithStats.get(userInput).getNumberOfObservations() + 1); //todo thread safety? atomic?

        int total = 0;
        for (Map.Entry<Gesture, GestureStatistics> entry : gestureWithStats.entrySet()) {
            total += entry.getValue().getNumberOfObservations();
        }

        for (Map.Entry<Gesture, GestureStatistics> entry : gestureWithStats.entrySet()) {
            entry.getValue().setProbability(entry.getValue().getNumberOfObservations() / total);
        }
    }

    //todo tests
    public Gesture predictPlayerMove(CurrentState state) {
        Map<Gesture, GestureStatistics> gestureWithStats = getStatsForGesture().get(state);
        Double maxProbability = gestureWithStats.values().stream().map(GestureStatistics::getProbability).max(Comparator.comparingDouble(x -> x)).orElse(0d);
        Double minProbability = gestureWithStats.values().stream().map(GestureStatistics::getProbability).min(Comparator.comparingDouble(x -> x)).orElse(0d);

        if (compareDoublesWithPrecision(maxProbability, minProbability)) {
            return randomAi.calculateResult(state.getFirst());
        }

        for (Map.Entry<Gesture, GestureStatistics> entry : gestureWithStats.entrySet()) {
            if (compareDoublesWithPrecision(maxProbability, entry.getValue().getProbability())) {
                return entry.getKey();
            }
        }

        throw new RuntimeException("Can't find maximum probability for current state"); //todo remove exception since reactive?
    }

    private boolean compareDoublesWithPrecision(Double first, Double second) {
        return Math.abs(first - second) < DOUBLE_COMPARISON_PRECISION;
    }
}
