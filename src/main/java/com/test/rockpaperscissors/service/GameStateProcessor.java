package com.test.rockpaperscissors.service;

import com.test.rockpaperscissors.dto.GameResultDto;
import com.test.rockpaperscissors.dto.GameState;
import com.test.rockpaperscissors.dto.GameStateDto;
import com.test.rockpaperscissors.dto.UserStats;
import com.test.rockpaperscissors.model.Context;
import com.test.rockpaperscissors.model.CurrentState;
import com.test.rockpaperscissors.model.GameResult;
import com.test.rockpaperscissors.model.Gesture;
import com.test.rockpaperscissors.service.ai.MarkovChain;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.reactive.socket.WebSocketSession;

@Slf4j
public class GameStateProcessor {
    private final GameService gameService;

    public GameStateProcessor(GameService gameService) {
        this.gameService = gameService;
    }

    public void process(WebSocketSession webSocketSession,
                        @NonNull GameStateDto gameStateDto) {
        final GameState gameState = gameStateDto.getState();
        if (gameState == null) {
            throw new IllegalArgumentException("Can't process null game state");
        }

        final String sessionId = webSocketSession.getId();
        final Context context = (Context) webSocketSession.getAttributes().get(sessionId);
        validateGameStarted(gameState, context);

        switch (gameState) {
            case START:
                log.debug("START GAME! WOOHOOO!");
                createNewSessionContextForUser(webSocketSession, sessionId);
                break;
            case IN_PROGRESS:
                log.debug("PLAY GAME! WOOHOOO!");
                final UserStats currentStats = context.getUserStats();
                GameResultDto gameResult = produceGameResult(context, gameStateDto);
                updateUserStats(gameResult, currentStats);
                log.debug("Current statistics: {}", currentStats);
                break;
            case END:
                log.debug("Your final statistics: {}", context.getUserStats());
                break;
            default:
                throw new RuntimeException("Unsupported game state.");
        }
    }

    private void validateGameStarted(GameState gameState, Context context) {
        if (context == null && !GameState.START.equals(gameState)) {
            throw new IllegalStateException(String.format("Can't start game with state: %s", gameState));
        }
    }

    private void createNewSessionContextForUser(WebSocketSession webSocketSession, String sessionId) {
        final MarkovChain markovChain = new MarkovChain();
        final Context sessionContext = new Context(
                markovChain.getTransitionProbabilities(),
                new CurrentState(Gesture.NONE, Gesture.NONE),
                new UserStats(0L, 0L, 0L, 0L)
        );
        webSocketSession.getAttributes().put(sessionId, sessionContext);
    }

    private GameResultDto produceGameResult(Context sessionContext, GameStateDto gameStateDto) {
        Pair<Gesture, GameResult> result = gameService.play(sessionContext, gameStateDto.getUserInput());
        log.debug(
                String.format("Player: %s; Computer: %s; Your result: %s",
                        gameStateDto.getUserInput(), result.getLeft(), result.getRight())
        );
        return GameResultDto.builder()
                .yourInput(gameStateDto.getUserInput())
                .computerInput(result.getLeft())
                .result(result.getRight())
                .build();
    }

    private void updateUserStats(GameResultDto gameResult, UserStats currentStats) {
        currentStats.setTotalGames(currentStats.getTotalGames() + 1);
        switch (gameResult.getResult()) {
            case WON:
                currentStats.setUserWon(currentStats.getUserWon() + 1);
                break;
            case TIED:
                currentStats.setTied(currentStats.getTied() + 1);
                break;
            case LOST:
                currentStats.setComputerWon(currentStats.getComputerWon() + 1);
                break;
            default:
                throw new RuntimeException("Unexpected game result when gathering statistics");
        }
    }

}
