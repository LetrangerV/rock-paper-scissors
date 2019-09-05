package com.test.rockpaperscissors.server.service.ai;

import com.test.rockpaperscissors.server.model.Context;
import com.test.rockpaperscissors.server.model.CurrentState;
import com.test.rockpaperscissors.server.model.Gesture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MarkovChainBasedAiTest {
    private MarkovChainBasedAi underTest;
    @Mock
    private MarkovChainBasedPredictor markovChainBasedPredictor;
    @Mock
    private RandomAi randomAi;
    @Mock
    private TransitionMatrixUpdater transitionMatrixUpdater;

    @Before
    public void setUp() {
        underTest = new MarkovChainBasedAi(markovChainBasedPredictor, randomAi, transitionMatrixUpdater);
    }

    @Test
    public void testCalculateResultFirstRound() {
        Context sessionContext = mock(Context.class);
        when(randomAi.calculateAiGesture(sessionContext, Gesture.ROCK)).thenReturn(Gesture.PAPER);
        Gesture result = underTest.calculateAiGesture(sessionContext, Gesture.ROCK);

        verify(randomAi).calculateAiGesture(sessionContext, Gesture.ROCK);
        verify(transitionMatrixUpdater).updateProbabilitiesMatrix(any(Context.class), eq(Gesture.ROCK));
        verify(markovChainBasedPredictor, never()).predictPlayerMove(any(Context.class));
        Assert.assertEquals(Gesture.PAPER, result);
    }

    @Test
    public void testCalculateResultSecondRound() {
        Context sessionContext = mock(Context.class);
        when(sessionContext.getPreviousState()).thenReturn(mock(CurrentState.class));

        when(markovChainBasedPredictor.predictPlayerMove(any(Context.class))).thenReturn(Gesture.PAPER);
        Gesture result = underTest.calculateAiGesture(sessionContext, Gesture.ROCK);

        verify(transitionMatrixUpdater).updateProbabilitiesMatrix(any(Context.class), eq(Gesture.ROCK));
        verify(markovChainBasedPredictor).predictPlayerMove(any(Context.class));
        Assert.assertEquals(Gesture.SCISSORS, result); //we predict user's most probable move as PAPER and AI plays SCISSORS to win
    }
}