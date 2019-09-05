package com.test.rockpaperscissors.server.service;

import com.test.rockpaperscissors.server.model.Context;
import com.test.rockpaperscissors.server.model.GameResult;
import com.test.rockpaperscissors.server.model.Gesture;
import com.test.rockpaperscissors.server.service.ai.GameAi;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class HumanAdaptedGameService implements GameService {
    private final WinnerCalculator winnerCalculator;
    private final GameAi gameAi;

    public HumanAdaptedGameService(WinnerCalculator winnerCalculator, GameAi gameAi) {
        this.winnerCalculator = winnerCalculator;
        this.gameAi = gameAi;
    }

    @Override
    public synchronized Pair<Gesture, GameResult> play(Context sessionContext, @NonNull Gesture userInput) {
        Gesture aiInput = gameAi.calculateAiGesture(sessionContext, userInput);
        GameResult result = winnerCalculator.calculateResultForFirst(userInput, aiInput);
        return ImmutablePair.of(aiInput, result);
    }
}
