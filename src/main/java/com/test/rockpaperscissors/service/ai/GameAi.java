package com.test.rockpaperscissors.service.ai;

import com.test.rockpaperscissors.model.Context;
import com.test.rockpaperscissors.model.Gesture;

public interface GameAi {
    Gesture calculateAiGesture(Context sessionContext, Gesture userInput);
}
