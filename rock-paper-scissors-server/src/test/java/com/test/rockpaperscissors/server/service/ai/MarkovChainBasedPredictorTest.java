package com.test.rockpaperscissors.server.service.ai;

import com.test.rockpaperscissors.server.dto.UserStats;
import com.test.rockpaperscissors.server.model.Context;
import com.test.rockpaperscissors.server.model.CurrentState;
import com.test.rockpaperscissors.server.model.Gesture;
import com.test.rockpaperscissors.server.model.GestureStatistics;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MarkovChainBasedPredictorTest {
    private MarkovChainBasedPredictor underTest;
    @Mock
    private RandomAi randomAi;

    @Before
    public void setUp() {
        underTest = new MarkovChainBasedPredictor(randomAi);
    }

    @Test
    public void testPredictPlayerMoveFirstTurn() {
        when(randomAi.calculateAiGesture(any(Context.class), any(Gesture.class))).thenReturn(Gesture.ROCK);
        MarkovChain markovChain = new MarkovChain();
        Context context = new Context(markovChain.getTransitionProbabilities(), new CurrentState(Gesture.NONE, Gesture.NONE), new UserStats(0L,0L,0L,0L));
        Gesture result = underTest.predictPlayerMove(context);

        Assert.assertEquals(Gesture.ROCK, result);
        verify(randomAi).calculateAiGesture(any(Context.class), any(Gesture.class));
    }

    @Test
    public void testPredictPlayerMoveEqualProbabilities() {
        when(randomAi.calculateAiGesture(any(Context.class), any(Gesture.class))).thenReturn(Gesture.ROCK);
        MarkovChain markovChain = new MarkovChain();
        Context context = new Context(markovChain.getTransitionProbabilities(), new CurrentState(Gesture.PAPER, Gesture.PAPER), new UserStats(0L,0L,0L,0L));
        Gesture result = underTest.predictPlayerMove(context);

        Assert.assertEquals(Gesture.ROCK, result);
        verify(randomAi).calculateAiGesture(any(Context.class), any(Gesture.class));
    }

    @Test
    public void testPredictPlayerMovePaperIsMoreProbable() {
        MarkovChain markovChain = new MarkovChain();
        Map<Gesture, GestureStatistics> gestureStats = markovChain.getTransitionProbabilities().get(new CurrentState(Gesture.PAPER, Gesture.PAPER));
        gestureStats.get(Gesture.PAPER).setProbability(0.5);
        Context context = new Context(markovChain.getTransitionProbabilities(), new CurrentState(Gesture.PAPER, Gesture.PAPER), new UserStats(0L,0L,0L,0L));

        Gesture result = underTest.predictPlayerMove(context);

        Assert.assertEquals(Gesture.PAPER, result);
        verify(randomAi, never()).calculateAiGesture(any(Context.class), any(Gesture.class));
    }
}