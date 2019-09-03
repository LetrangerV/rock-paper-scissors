package com.test.rockpaperscissors.service.ai;

import com.test.rockpaperscissors.model.Context;
import com.test.rockpaperscissors.model.Gesture;
import com.test.rockpaperscissors.model.GestureStatistics;

import java.util.Map;

public class TransitionMatrixUpdater {
    private float decay;

    public synchronized void updateProbabilitiesMatrix(Context sessionContext, Gesture userInput) {
        Map<Gesture, GestureStatistics> gestureWithStats = sessionContext.getTransitionProbabilities().get(sessionContext.getPreviousState());
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
}
