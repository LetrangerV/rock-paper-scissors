package com.test.rockpaperscissors.server.service;

import com.test.rockpaperscissors.server.model.Context;
import com.test.rockpaperscissors.server.model.GameResult;
import com.test.rockpaperscissors.server.model.Gesture;
import org.apache.commons.lang3.tuple.Pair;

public interface GameService {
    Pair<Gesture, GameResult> play(Context sessionContext, Gesture userInput);
}
