package com.test.rockpaperscissors.service.ai;

import com.test.rockpaperscissors.dto.UserStats;
import com.test.rockpaperscissors.model.Context;
import com.test.rockpaperscissors.model.CurrentState;
import com.test.rockpaperscissors.model.Gesture;
import com.test.rockpaperscissors.model.GestureStatistics;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TransitionMatrixUpdaterTest {
    private TransitionMatrixUpdater underTest;

    @Before
    public void setUp() {
        underTest = new TransitionMatrixUpdater(1);
    }

    @Test
    public void testUpdateProbabilitiesMatrixNonePreviousState() {
        MarkovChain markovChain = new MarkovChain();
        Map<CurrentState, Map<Gesture, GestureStatistics>> probabilitiesBefore = markovChain.getTransitionProbabilities();
        Context context = new Context(probabilitiesBefore, new CurrentState(Gesture.NONE, Gesture.NONE), new UserStats(0L, 0L, 0L, 0L));

        underTest.updateProbabilitiesMatrix(context, Gesture.ROCK);

        assertEquals(probabilitiesBefore, new MarkovChain().getTransitionProbabilities());
    }

    @Test
    public void testUpdateProbabilitiesMatrixNumberOfObservations() {
        MarkovChain markovChain = new MarkovChain();
        Map<CurrentState, Map<Gesture, GestureStatistics>> probabilitiesBefore = markovChain.getTransitionProbabilities();
        final CurrentState previousState = new CurrentState(Gesture.ROCK, Gesture.PAPER);
        final Context context = new Context(probabilitiesBefore, previousState, new UserStats(0L, 0L, 0L, 0L));

        underTest.updateProbabilitiesMatrix(context, Gesture.ROCK);

        assertNotEquals(probabilitiesBefore, new MarkovChain().getTransitionProbabilities());
        final Map<Gesture, GestureStatistics> updatedStats = context.getTransitionProbabilities().get(previousState);
        assertTrue(doubleEquals(1d, updatedStats.get(Gesture.ROCK).getNumberOfObservations()));
        assertTrue(doubleEquals(0d, updatedStats.get(Gesture.PAPER).getNumberOfObservations()));
        assertTrue(doubleEquals(0d, updatedStats.get(Gesture.SCISSORS).getNumberOfObservations()));
    }

    @Test
    public void testUpdateProbabilitiesMatrixNumberOfObservationsWithDecay() {
        underTest = new TransitionMatrixUpdater(0.4f);

        MarkovChain markovChain = new MarkovChain();
        Map<CurrentState, Map<Gesture, GestureStatistics>> probabilitiesBefore = markovChain.getTransitionProbabilities();
        final CurrentState previousState = new CurrentState(Gesture.ROCK, Gesture.PAPER);
        final Context context = new Context(probabilitiesBefore, previousState, new UserStats(0L, 0L, 0L, 0L));

        underTest.updateProbabilitiesMatrix(context, Gesture.ROCK);
        underTest.updateProbabilitiesMatrix(context, Gesture.ROCK);

        assertNotEquals(probabilitiesBefore, new MarkovChain().getTransitionProbabilities());
        final Map<Gesture, GestureStatistics> updatedStats = context.getTransitionProbabilities().get(previousState);
        assertTrue(doubleEquals(1.4d, updatedStats.get(Gesture.ROCK).getNumberOfObservations()));
        assertTrue(doubleEquals(0d, updatedStats.get(Gesture.PAPER).getNumberOfObservations()));
        assertTrue(doubleEquals(0d, updatedStats.get(Gesture.SCISSORS).getNumberOfObservations()));
    }

    @Test
    public void testUpdateProbabilitiesMatrixProbability() {
        MarkovChain markovChain = new MarkovChain();
        Map<CurrentState, Map<Gesture, GestureStatistics>> probabilitiesBefore = markovChain.getTransitionProbabilities();
        final CurrentState previousState = new CurrentState(Gesture.ROCK, Gesture.PAPER);
        final Context context = new Context(probabilitiesBefore, previousState, new UserStats(0L, 0L, 0L, 0L));

        underTest.updateProbabilitiesMatrix(context, Gesture.ROCK);

        assertNotEquals(probabilitiesBefore, new MarkovChain().getTransitionProbabilities());
        final Map<Gesture, GestureStatistics> updatedStats = context.getTransitionProbabilities().get(previousState);
        assertTrue(doubleEquals(1d, updatedStats.get(Gesture.ROCK).getProbability()));
        assertTrue(doubleEquals(0d, updatedStats.get(Gesture.PAPER).getProbability()));
        assertTrue(doubleEquals(0d, updatedStats.get(Gesture.SCISSORS).getProbability()));
    }

    private boolean doubleEquals(double first, double second) {
        return Math.abs(first - second) < 0.00000001;
    }
}