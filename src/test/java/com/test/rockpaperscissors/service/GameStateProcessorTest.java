package com.test.rockpaperscissors.service;

import com.test.rockpaperscissors.dto.GameState;
import com.test.rockpaperscissors.dto.GameStateDto;
import com.test.rockpaperscissors.dto.UserStats;
import com.test.rockpaperscissors.model.Context;
import com.test.rockpaperscissors.model.GameResult;
import com.test.rockpaperscissors.model.Gesture;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameStateProcessorTest {
    private GameStateProcessor underTest;
    @Mock
    private GameService gameService;

    @Before
    public void setUp() {
        underTest = new GameStateProcessor(gameService);
    }

    @Test
    public void testProcessNull() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> underTest.process(null, null));
    }

    @Test
    public void testProcessNullState() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> underTest.process(mock(WebSocketSession.class), new GameStateDto(null, null)));
    }

    @Test
    public void testProcessStart() {
        final WebSocketSession session = mock(WebSocketSession.class);
        final HashMap<String, Object> attributesMap = new HashMap<>();
        when(session.getAttributes()).thenReturn(attributesMap);
        when(session.getId()).thenReturn("abc123");

        underTest.process(session, new GameStateDto(GameState.START, null));

        verify(session, times(2)).getAttributes();
        assertNotNull(attributesMap.get("abc123"));
    }

    @Test
    public void testProcessInvalidStartWithInProgress() {
        final WebSocketSession session = mock(WebSocketSession.class);
        final HashMap<String, Object> attributesMap = new HashMap<>();
        when(session.getAttributes()).thenReturn(attributesMap);
        when(session.getId()).thenReturn("abc123");

        Assertions.assertThatIllegalStateException().isThrownBy(() -> underTest.process(session, new GameStateDto(GameState.IN_PROGRESS, null)));
    }

    @Test
    public void testProcessInvalidStartWithEnd() {
        final WebSocketSession session = mock(WebSocketSession.class);
        final HashMap<String, Object> attributesMap = new HashMap<>();
        when(session.getAttributes()).thenReturn(attributesMap);
        when(session.getId()).thenReturn("abc123");

        Assertions.assertThatIllegalStateException().isThrownBy(() -> underTest.process(session, new GameStateDto(GameState.END, null)));
    }

    @Test
    public void testProcessInProgressTied() {
        final String sessionId = "abc123";
        final WebSocketSession session = mock(WebSocketSession.class);
        final HashMap<String, Object> attributesMap = new HashMap<>();
        attributesMap.put(sessionId, new Context(null, null, new UserStats()));

        when(session.getAttributes()).thenReturn(attributesMap);

        when(session.getId()).thenReturn(sessionId);
        when(gameService.play(any(Context.class), any(Gesture.class))).thenReturn(ImmutablePair.of(Gesture.ROCK, GameResult.TIED));

        underTest.process(session, new GameStateDto(GameState.IN_PROGRESS, Gesture.ROCK));

        verifyInProgressInteractions(session);
        final UserStats userStats = verifyInProgressContext(sessionId, attributesMap);
        assertEquals(1, userStats.getTotalGames());
        assertEquals(1, userStats.getTied());
        assertEquals(0, userStats.getUserWon());
        assertEquals(0, userStats.getComputerWon());
    }

    @Test
    public void testProcessInProgressWon() {
        final String sessionId = "abc123";
        final WebSocketSession session = mock(WebSocketSession.class);
        final HashMap<String, Object> attributesMap = new HashMap<>();
        attributesMap.put(sessionId, new Context(null, null, new UserStats()));

        when(session.getAttributes()).thenReturn(attributesMap);

        when(session.getId()).thenReturn(sessionId);
        when(gameService.play(any(Context.class), any(Gesture.class))).thenReturn(ImmutablePair.of(Gesture.ROCK, GameResult.WON));

        underTest.process(session, new GameStateDto(GameState.IN_PROGRESS, Gesture.ROCK));

        verifyInProgressInteractions(session);
        final UserStats userStats = verifyInProgressContext(sessionId, attributesMap);
        assertEquals(1, userStats.getTotalGames());
        assertEquals(0, userStats.getTied());
        assertEquals(1, userStats.getUserWon());
        assertEquals(0, userStats.getComputerWon());
    }

    @Test
    public void testProcessInProgressLost() {
        final String sessionId = "abc123";
        final WebSocketSession session = mock(WebSocketSession.class);
        final HashMap<String, Object> attributesMap = new HashMap<>();
        attributesMap.put(sessionId, new Context(null, null, new UserStats()));

        when(session.getAttributes()).thenReturn(attributesMap);

        when(session.getId()).thenReturn(sessionId);
        when(gameService.play(any(Context.class), any(Gesture.class))).thenReturn(ImmutablePair.of(Gesture.ROCK, GameResult.LOST));

        underTest.process(session, new GameStateDto(GameState.IN_PROGRESS, Gesture.ROCK));

        verifyInProgressInteractions(session);
        final UserStats userStats = verifyInProgressContext(sessionId, attributesMap);
        assertEquals(1, userStats.getTotalGames());
        assertEquals(0, userStats.getTied());
        assertEquals(0, userStats.getUserWon());
        assertEquals(1, userStats.getComputerWon());
    }

    @Test
    public void testProcessEnd() {
        final String sessionId = "abc123";
        final WebSocketSession session = mock(WebSocketSession.class);
        final HashMap<String, Object> attributesMap = new HashMap<>();
        attributesMap.put(sessionId, new Context(null, null, new UserStats()));
        when(session.getAttributes()).thenReturn(attributesMap);
        when(session.getId()).thenReturn(sessionId);

        underTest.process(session, new GameStateDto(GameState.END, null));

        assertNotNull(attributesMap.get(sessionId));
    }

    private void verifyInProgressInteractions(WebSocketSession session) {
        verify(gameService).play(any(Context.class), any(Gesture.class));
        verify(session).getAttributes();
    }

    private UserStats verifyInProgressContext(String sessionId, HashMap<String, Object> attributesMap) {
        final Context context = (Context) attributesMap.get(sessionId);
        assertNotNull(context);
        final UserStats userStats = context.getUserStats();
        assertNotNull(userStats);
        return userStats;
    }
}