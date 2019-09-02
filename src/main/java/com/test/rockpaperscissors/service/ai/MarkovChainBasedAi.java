package com.test.rockpaperscissors.service.ai;

import com.test.rockpaperscissors.model.Context;
import com.test.rockpaperscissors.model.CurrentState;
import com.test.rockpaperscissors.model.Gesture;
import com.test.rockpaperscissors.service.WinnerCalculator;
import lombok.NonNull;

public class MarkovChainBasedAi implements GameAi {
    private final MarkovChainBasedPredictor markovChainBasedPredictor;
    private final RandomAi randomAi;
    private final TransitionMatrixUpdater matrixUpdater;

    public MarkovChainBasedAi(MarkovChainBasedPredictor markovChainBasedPredictor, RandomAi randomAi, TransitionMatrixUpdater matrixUpdater) {
        this.markovChainBasedPredictor = markovChainBasedPredictor;
        this.randomAi = randomAi;
        this.matrixUpdater = matrixUpdater;
    }

    @Override
    public Gesture calculateAiGesture(Context sessionContext, @NonNull Gesture userInput) {
        if (sessionContext.getPreviousState() == null) {
            return playFirstRound(sessionContext, userInput);
        }

        return playNextRounds(sessionContext, userInput);
    }

    private Gesture playFirstRound(Context sessionContext, @NonNull Gesture userInput) {
        Gesture gesture = randomAi.calculateAiGesture(sessionContext, userInput);
        updateGameState(sessionContext, userInput, gesture);
        return gesture;
    }

    private Gesture playNextRounds(Context sessionContext, @NonNull Gesture userInput) {
        Gesture aiOutput = WinnerCalculator.WIN_MATRIX.get(markovChainBasedPredictor.predictPlayerMove(sessionContext));
        updateGameState(sessionContext, userInput, aiOutput);
        return aiOutput;
    }

    private void updateGameState(Context sessionContext, @NonNull Gesture userInput, Gesture aiOutput) {
        sessionContext.setPreviousState(new CurrentState(userInput, aiOutput));
        matrixUpdater.updateProbabilitiesMatrix(sessionContext, userInput);
    }
}
