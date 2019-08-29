package com.test.rockpaperscissors.service;

import com.test.rockpaperscissors.model.GameResult;
import com.test.rockpaperscissors.model.Gesture;

import java.util.Map;

//todo add checkstyle and PMD
public class WinnerCalculator {
    public static final Map<Gesture, Gesture> WIN_MATRIX = Map.of(
            Gesture.PAPER, Gesture.SCISSORS,
            Gesture.SCISSORS, Gesture.ROCK,
            Gesture.ROCK, Gesture.PAPER
    );

    public GameResult calculateResultForFirst(Gesture first, Gesture second) {
        Gesture winConditionForFirst = WIN_MATRIX.get(second);
        if (winConditionForFirst.equals(first)) {
            return GameResult.WON;
        }

        if (first.equals(second)) {
            return GameResult.TIED;
        }

        return GameResult.LOST;
    }
}
