package com.test.rockpaperscissors.service;

import com.test.rockpaperscissors.model.GameResult;
import com.test.rockpaperscissors.model.Gesture;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WinnerCalculatorTest {
    private WinnerCalculator underTest;

    @Before
    public void setUp() {
        underTest = new WinnerCalculator();
    }

    @Test
    public void testCalculateResultForFirstWin() {
        GameResult result = underTest.calculateResultForFirst(Gesture.PAPER, Gesture.ROCK);

        assertEquals(GameResult.WON, result);
    }

    @Test
    public void testCalculateResultForFirstLost() {
        GameResult result = underTest.calculateResultForFirst(Gesture.SCISSORS, Gesture.ROCK);

        assertEquals(GameResult.LOST, result);
    }

    @Test
    public void testCalculateResultForFirstTied() {
        GameResult result = underTest.calculateResultForFirst(Gesture.SCISSORS, Gesture.SCISSORS);

        assertEquals(GameResult.TIED, result);
    }
}