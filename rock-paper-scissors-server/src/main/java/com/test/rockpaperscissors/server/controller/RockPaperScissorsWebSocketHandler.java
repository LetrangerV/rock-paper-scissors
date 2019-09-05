package com.test.rockpaperscissors.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.rockpaperscissors.server.dto.GameStateDto;
import com.test.rockpaperscissors.server.dto.UserStats;
import com.test.rockpaperscissors.server.model.Context;
import com.test.rockpaperscissors.server.service.GameStateProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
public class RockPaperScissorsWebSocketHandler implements WebSocketHandler {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private final GameStateProcessor gameStateProcessor;

    public RockPaperScissorsWebSocketHandler(GameStateProcessor gameStateProcessor) {
        this.gameStateProcessor = gameStateProcessor;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        final Flux<WebSocketMessage> flux = webSocketSession.receive()
                .doOnNext(message -> {
                    final String text = message.getPayloadAsText();
                    GameStateDto gameStateDto = readMessage(text);
                    gameStateProcessor.process(webSocketSession, gameStateDto);
                }).map(message -> {
                    final Context sessionContext = (Context) webSocketSession.getAttributes()
                            .get(webSocketSession.getId());
                    return webSocketSession.textMessage(writeMessage(sessionContext.getUserStats()));
                }).doOnError(exception -> log.error("Unexpected exception: ", exception));

        return webSocketSession.send(flux);
    }

    private GameStateDto readMessage(String message) {
        try {
            return JSON_MAPPER.readValue(message, GameStateDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String writeMessage(UserStats message) {
        try {
            return JSON_MAPPER.writeValueAsString(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
