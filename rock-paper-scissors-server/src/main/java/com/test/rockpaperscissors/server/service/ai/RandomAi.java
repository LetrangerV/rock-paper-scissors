package com.test.rockpaperscissors.server.service.ai;

import com.test.rockpaperscissors.server.model.Context;
import com.test.rockpaperscissors.server.model.Gesture;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomAi implements GameAi {
    private static final List<Gesture> VALUES =
            Collections.unmodifiableList(List.of(Gesture.PAPER, Gesture.ROCK, Gesture.SCISSORS));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    @Override
    public Gesture calculateAiGesture(Context sessionContext, Gesture userInput) {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
