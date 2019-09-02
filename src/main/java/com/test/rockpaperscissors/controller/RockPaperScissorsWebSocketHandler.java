package com.test.rockpaperscissors.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.rockpaperscissors.dto.GameResultDto;
import com.test.rockpaperscissors.dto.GameStateDto;
import com.test.rockpaperscissors.dto.UserStats;
import com.test.rockpaperscissors.model.GameResult;
import com.test.rockpaperscissors.model.Gesture;
import com.test.rockpaperscissors.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
public class RockPaperScissorsWebSocketHandler implements WebSocketHandler {
    private static final ObjectMapper json = new ObjectMapper();
    private final GameService gameService;

    public RockPaperScissorsWebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    //todo do we need to store user id or session is enough?
    //how do we create separate matrices for each user? spring scope = prototype/session? how much memory could it take?

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.receive() //access the stream of inbound messages
        .doOnNext(message -> {
            final String text = message.getPayloadAsText();
            GameStateDto gameStateDto = readMessage(text);
            switch (gameStateDto.getState()) { //todo java 12 switch expression
                case START:
                    log.info("START GAME! WOOHOOO!");
                    //
                    break;
                case IN_PROGRESS:
                    log.info("PLAY GAME! WOOHOOO!");
                    final String sessionId = webSocketSession.getId();
                    final UserStats currentStats = (UserStats) webSocketSession.getAttributes().get(sessionId);

                    GameResultDto gameResult = produceGameResult(gameStateDto);

                    if (currentStats == null) {
                        final UserStats userStats = new UserStats(0L, 0L, 0L, 0L);
                        updateUserStats(gameResult, userStats);
                        log.info("Current statistics: {}", userStats);
                        webSocketSession.getAttributes().put(sessionId, userStats); //todo put matrix into session?
                    } else {
                        updateUserStats(gameResult, currentStats);
                        log.info("Current statistics: {}", currentStats);
                    }

                    break;
                case END:
                    log.info("END GAME! WOOHOOO!");
                    final UserStats finalStats = (UserStats) webSocketSession.getAttributes().get(webSocketSession.getId());
                    log.info("Your final statistics: {}", finalStats);
                    webSocketSession.close(CloseStatus.NORMAL);
                    break;
                default:
                    throw new RuntimeException("Unsupported game state.");
            }
        }) //do something with each message
                .doOnError((ex) -> log.info("Oops error", ex))
//                .concatMap(message -> {new WebSocketMessage(message).getPayloadAsText();}) //perform nested async operations that use the message content
                .map(WebSocketMessage::getPayloadAsText)
                .then(); //return a Mono<Void that completes when receiving completes
    }

    private GameResultDto produceGameResult(GameStateDto gameStateDto) {
        Pair<Gesture, GameResult> result = gameService.play(gameStateDto.getUserInput());
        log.info(
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

    private GameStateDto readMessage(String message) {
        try {
            return json.readValue(message, GameStateDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
