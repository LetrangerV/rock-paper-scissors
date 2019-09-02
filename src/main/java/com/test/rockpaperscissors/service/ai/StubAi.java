package com.test.rockpaperscissors.service.ai;

import com.test.rockpaperscissors.model.Context;
import com.test.rockpaperscissors.model.Gesture;

public class StubAi implements GameAi {
    @Override
    public Gesture calculateAiGesture(Context sessionContext, Gesture userInput) {
        return Gesture.PAPER;
    }
}
