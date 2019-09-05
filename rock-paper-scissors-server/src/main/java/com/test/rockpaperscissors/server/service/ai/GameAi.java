package com.test.rockpaperscissors.server.service.ai;

import com.test.rockpaperscissors.server.model.Context;
import com.test.rockpaperscissors.server.model.Gesture;

public interface GameAi {
    Gesture calculateAiGesture(Context sessionContext, Gesture userInput);
}
