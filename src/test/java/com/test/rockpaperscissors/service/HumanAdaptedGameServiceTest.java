package com.test.rockpaperscissors.service;

import com.test.rockpaperscissors.model.GameResult;
import com.test.rockpaperscissors.model.Gesture;
import com.test.rockpaperscissors.service.ai.GameAi;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HumanAdaptedGameServiceTest {
    private HumanAdaptedGameService underTest;
    @Mock
    private WinnerCalculator winnerCalculator;
    @Mock
    private GameAi gameAi;

    @Before
    public void setUp() {
        underTest = new HumanAdaptedGameService(winnerCalculator, gameAi);
    }

    @Test
    public void testPlay() {
        when(gameAi.calculateAiGesture(Gesture.ROCK)).thenReturn(Gesture.PAPER);
        when(winnerCalculator.calculateResultForFirst(Gesture.ROCK, Gesture.PAPER)).thenReturn(GameResult.LOST);
        Pair<Gesture, GameResult> result = underTest.play(Gesture.ROCK);

        assertEquals(Gesture.PAPER, result.getLeft());
        assertEquals(GameResult.LOST, result.getRight());
    }
}