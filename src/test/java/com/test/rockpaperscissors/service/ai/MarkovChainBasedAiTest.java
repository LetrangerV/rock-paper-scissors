package com.test.rockpaperscissors.service.ai;

import com.test.rockpaperscissors.model.CurrentState;
import com.test.rockpaperscissors.model.Gesture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MarkovChainBasedAiTest {
    private MarkovChainBasedAi underTest;
    @Mock
    private MarkovChain markovChain;
    @Mock
    private RandomAi randomAi;

    @Before
    public void setUp() {
        underTest = new MarkovChainBasedAi(markovChain, randomAi);
    }

    @Test
    public void testCalculateResultFirstRound() {
        when(randomAi.calculateAiGesture(Gesture.ROCK)).thenReturn(Gesture.PAPER);
        Gesture result = underTest.calculateAiGesture(Gesture.ROCK);

        verify(randomAi).calculateAiGesture(Gesture.ROCK);
        verify(markovChain).updateProbabilitiesMatrix(any(CurrentState.class), eq(Gesture.ROCK));
        verify(markovChain, never()).predictPlayerMove(any(CurrentState.class));
        Assert.assertEquals(Gesture.PAPER, result);
    }

    @Test
    public void testCalculateResultSecondRound() {
        when(randomAi.calculateAiGesture(Gesture.ROCK)).thenReturn(Gesture.PAPER);
        underTest.calculateAiGesture(Gesture.ROCK);

        when(markovChain.predictPlayerMove(any(CurrentState.class))).thenReturn(Gesture.PAPER);
        Gesture secondResult = underTest.calculateAiGesture(Gesture.ROCK);

        verify(markovChain, times(2)).updateProbabilitiesMatrix(any(CurrentState.class), eq(Gesture.ROCK));
        verify(markovChain).predictPlayerMove(any(CurrentState.class));
        Assert.assertEquals(Gesture.SCISSORS, secondResult); //we predict user's most probable move as PAPER and AI plays SCISSORS to win
    }
}