package com.test.rockpaperscissors.service.ai;

import com.test.rockpaperscissors.model.Context;
import com.test.rockpaperscissors.model.Gesture;
import com.test.rockpaperscissors.model.GestureStatistics;

import java.util.Comparator;
import java.util.Map;

public class MarkovChainBasedPredictor {
    private static final double DOUBLE_COMPARISON_PRECISION = 0.000000001;

    private final RandomAi randomAi;

    public MarkovChainBasedPredictor(RandomAi randomAi) {
        this.randomAi = randomAi;
    }

    public Gesture predictPlayerMove(Context sessionContext) {
        Map<Gesture, GestureStatistics> gestureWithStats = sessionContext.getTransitionProbabilities().get(sessionContext.getPreviousState());
        if (gestureWithStats == null) { //first round with NONE, NONE as previous input
            return randomAi.calculateAiGesture(sessionContext, sessionContext.getPreviousState().getFirst());
        }

        Double maxProbability = gestureWithStats.values().stream().map(GestureStatistics::getProbability).max(Comparator.comparingDouble(x -> x)).orElse(0d);
        Double minProbability = gestureWithStats.values().stream().map(GestureStatistics::getProbability).min(Comparator.comparingDouble(x -> x)).orElse(0d);

        if (compareDoublesWithPrecision(maxProbability, minProbability)) {
            return randomAi.calculateAiGesture(sessionContext, sessionContext.getPreviousState().getFirst());
        }

        for (Map.Entry<Gesture, GestureStatistics> entry : gestureWithStats.entrySet()) {
            if (compareDoublesWithPrecision(maxProbability, entry.getValue().getProbability())) {
                return entry.getKey();
            }
        }

        throw new RuntimeException("Can't find maximum probability for current state");
    }

    private boolean compareDoublesWithPrecision(Double first, Double second) {
        return Math.abs(first - second) < DOUBLE_COMPARISON_PRECISION;
    }
}
