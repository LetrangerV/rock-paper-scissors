package com.test.rockpaperscissors.server.service.ai;

import com.test.rockpaperscissors.server.model.Context;
import com.test.rockpaperscissors.server.model.Gesture;
import com.test.rockpaperscissors.server.model.GestureStatistics;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class TransitionMatrixUpdater {
    private final float decay;

    public TransitionMatrixUpdater(float decay) {
        this.decay = decay;
    }

    public synchronized void updateProbabilitiesMatrix(Context sessionContext, Gesture userInput) {
        Map<Gesture, GestureStatistics> gestureWithStats =
                sessionContext.getTransitionProbabilities().get(sessionContext.getPreviousState());
        if (gestureWithStats == null) {
            log.debug("Can't update transition probability matrix because current state is not present there");
            return;
        }
        applyMemoryDecay(gestureWithStats);
        updateNumberOfObservations(userInput, gestureWithStats);
        updateProbabilities(gestureWithStats);
    }

    private void applyMemoryDecay(Map<Gesture, GestureStatistics> gestureWithStats) {
        for (Map.Entry<Gesture, GestureStatistics> entry : gestureWithStats.entrySet()) {
            entry.getValue().setNumberOfObservations(decay * entry.getValue().getNumberOfObservations());
        }
    }

    private void updateNumberOfObservations(Gesture userInput, Map<Gesture, GestureStatistics> gestureWithStats) {
        gestureWithStats.get(userInput).setNumberOfObservations(
                gestureWithStats.get(userInput).getNumberOfObservations() + 1);
    }

    private void updateProbabilities(Map<Gesture, GestureStatistics> gestureWithStats) {
        int total = 0;
        for (Map.Entry<Gesture, GestureStatistics> entry : gestureWithStats.entrySet()) {
            total += entry.getValue().getNumberOfObservations();
        }

        for (Map.Entry<Gesture, GestureStatistics> entry : gestureWithStats.entrySet()) {
            entry.getValue().setProbability(entry.getValue().getNumberOfObservations() / total);
        }
    }
}
