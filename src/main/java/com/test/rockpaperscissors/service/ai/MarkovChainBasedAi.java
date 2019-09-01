package com.test.rockpaperscissors.service.ai;

import com.test.rockpaperscissors.model.CurrentState;
import com.test.rockpaperscissors.model.Gesture;
import com.test.rockpaperscissors.service.WinnerCalculator;
import lombok.NonNull;

public class MarkovChainBasedAi implements GameAi {
    private final MarkovChain markovChain;
    private final RandomAi randomAi;
    private CurrentState previousState; //todo concurrent session state management?

    public MarkovChainBasedAi(MarkovChain markovChain, RandomAi randomAi) {
        this.markovChain = markovChain;
        this.randomAi = randomAi;
    }

    @Override
    public Gesture calculateAiGesture(@NonNull Gesture userInput) {
        if (previousState == null) {
            return playFirstRound(userInput);
        }

        return playNextRounds(userInput);
    }

    private Gesture playFirstRound(@NonNull Gesture userInput) {
        Gesture gesture = randomAi.calculateAiGesture(userInput);
        updateGameState(userInput, gesture);
        return gesture;
    }

    private Gesture playNextRounds(@NonNull Gesture userInput) {
        Gesture aiOutput = WinnerCalculator.WIN_MATRIX.get(markovChain.predictPlayerMove(previousState));
        updateGameState(userInput, aiOutput);
        return aiOutput;
    }

    private void updateGameState(@NonNull Gesture userInput, Gesture aiOutput) {
        previousState = new CurrentState(userInput, aiOutput);
        markovChain.updateProbabilitiesMatrix(previousState, userInput);
    }
}
