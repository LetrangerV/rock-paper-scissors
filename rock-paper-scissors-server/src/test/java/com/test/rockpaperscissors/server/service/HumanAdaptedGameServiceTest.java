package com.test.rockpaperscissors.server.service;

import com.test.rockpaperscissors.server.model.Context;
import com.test.rockpaperscissors.server.model.GameResult;
import com.test.rockpaperscissors.server.model.Gesture;
import com.test.rockpaperscissors.server.service.ai.GameAi;
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
        Context sessionContext = new Context(null, null, null);
        when(gameAi.calculateAiGesture(sessionContext, Gesture.ROCK)).thenReturn(Gesture.PAPER);
        when(winnerCalculator.calculateResultForFirst(Gesture.ROCK, Gesture.PAPER)).thenReturn(GameResult.LOST);
        Pair<Gesture, GameResult> result = underTest.play(sessionContext, Gesture.ROCK);

        assertEquals(Gesture.PAPER, result.getLeft());
        assertEquals(GameResult.LOST, result.getRight());
    }
}