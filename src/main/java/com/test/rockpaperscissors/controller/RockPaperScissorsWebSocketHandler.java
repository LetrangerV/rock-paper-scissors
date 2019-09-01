package com.test.rockpaperscissors.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.rockpaperscissors.dto.GameStateDto;
import com.test.rockpaperscissors.model.GameResult;
import com.test.rockpaperscissors.model.Gesture;
import com.test.rockpaperscissors.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
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

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.receive() //access the stream of inbound messages
        .doOnNext(message -> {
            final String text = message.getPayloadAsText();
            log.info("Hello websocket " + text);
            GameStateDto gameStateDto = readMessage(text);
            switch (gameStateDto.getState()) { //todo java 12 switch expression
                case START:
                    log.info("START GAME! WOOHOOO!");
                    //
                    break;
                case IN_PROGRESS:
                    log.info("PLAY GAME! WOOHOOO!");
                    Pair<Gesture, GameResult> result = gameService.play(gameStateDto.getUserInput());
                    log.info(
                            String.format("Player: %s; Computer: %s; Your result: %s",
                                    gameStateDto.getUserInput(), result.getLeft(), result.getRight())
                    );
                    break;
                case END:
                    log.info("END GAME! WOOHOOO!");
                    //
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

    private GameStateDto readMessage(String message) {
        try {
            return json.readValue(message, GameStateDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
