package com.test.rockpaperscissors.service;

import com.test.rockpaperscissors.model.Context;
import com.test.rockpaperscissors.model.GameResult;
import com.test.rockpaperscissors.model.Gesture;
import org.apache.commons.lang3.tuple.Pair;

public interface GameService {
    Pair<Gesture, GameResult> play(Context sessionContext, Gesture userInput);
}
