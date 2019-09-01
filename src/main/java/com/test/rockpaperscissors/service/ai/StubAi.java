package com.test.rockpaperscissors.service.ai;

import com.test.rockpaperscissors.model.Gesture;

public class StubAi implements GameAi {
    @Override
    public Gesture calculateAiGesture(Gesture userInput) {
        return Gesture.PAPER;
    }
}
