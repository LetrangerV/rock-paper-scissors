package com.test.rockpaperscissors.service.ai;

import com.test.rockpaperscissors.model.CurrentState;
import com.test.rockpaperscissors.model.Gesture;
import com.test.rockpaperscissors.service.WinnerCalculator;

public class MarkovChainBasedAi implements GameAi {
    private final MarkovChain markovChain;
    private CurrentState pairDiff1;
    private CurrentState pairDiff2; //todo concurrent session state management?

    public MarkovChainBasedAi(MarkovChain markovChain) {
        this.markovChain = markovChain;
    }

    @Override //todo tests
    public Gesture calculateResult(Gesture userInput) {
        pairDiff1 = null;
        pairDiff2 = null;
        Gesture aiOutput = null;

        if (userInput == null) { //first round
            markovChain.createProbabilitiesMatrix();
        } else {
            pairDiff2 = pairDiff1;
            pairDiff1 = new CurrentState(userInput, aiOutput);
        }

        if (pairDiff2 != null) {
            markovChain.updateProbabilitiesMatrix(pairDiff2, userInput);
            aiOutput = WinnerCalculator.WIN_MATRIX.get(markovChain.predictPlayerMove(pairDiff1));
        } else {
            aiOutput = Gesture.PAPER; //todo make random predictor here
        }

        return aiOutput;
    }
}
