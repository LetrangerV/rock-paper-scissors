package com.test.rockpaperscissors.service.ai;

import com.test.rockpaperscissors.model.CurrentState;
import com.test.rockpaperscissors.model.Gesture;
import com.test.rockpaperscissors.service.WinnerCalculator;

public class MarkovChainBasedAi implements GameAi {
    private final MarkovChain markovChain;
    private final RandomAi randomAi;
    private CurrentState pairDiff1;
    private CurrentState pairDiff2; //todo concurrent session state management?
    private Gesture aiOutput;

    public MarkovChainBasedAi(MarkovChain markovChain, RandomAi randomAi) {
        this.markovChain = markovChain;
        this.randomAi = randomAi;
    }

    @Override //todo tests
    public Gesture calculateResult(Gesture userInput) {
        if (userInput == null) { //first round
//            markovChain.createProbabilitiesMatrix(); //todo actually user input is never null. need to init somewhere else. postconstruct?
        } else {
            pairDiff2 = pairDiff1;
            pairDiff1 = new CurrentState(userInput, aiOutput);
        }

        if (pairDiff2 != null) {
            markovChain.updateProbabilitiesMatrix(pairDiff2, userInput);
            aiOutput = WinnerCalculator.WIN_MATRIX.get(markovChain.predictPlayerMove(pairDiff1));
        } else {
            aiOutput = randomAi.calculateResult(userInput);
        }

        return aiOutput;
    }
}
