package com.test.rockpaperscissors.service;

import com.test.rockpaperscissors.model.GameResult;
import com.test.rockpaperscissors.model.Gesture;
import com.test.rockpaperscissors.service.ai.GameAi;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class HumanAdaptedGameService implements GameService {
    private final WinnerCalculator winnerCalculator;
    private final GameAi gameAi;

    public HumanAdaptedGameService(WinnerCalculator winnerCalculator, GameAi gameAi) {
        this.winnerCalculator = winnerCalculator;
        this.gameAi = gameAi;
    }

    //todo tests
    @Override
    public Pair<Gesture, GameResult> play(Gesture userInput) {
        Gesture aiInput = gameAi.calculateResult(userInput);
        GameResult result = winnerCalculator.calculateResultForFirst(userInput, aiInput);
        return ImmutablePair.of(aiInput, result);
    }
}
