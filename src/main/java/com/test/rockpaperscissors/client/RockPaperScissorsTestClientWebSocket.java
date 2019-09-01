package com.test.rockpaperscissors.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.rockpaperscissors.dto.GameState;
import com.test.rockpaperscissors.dto.GameStateDto;
import com.test.rockpaperscissors.model.Gesture;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

//todo: temporary websocket client for test purpose
public class RockPaperScissorsTestClientWebSocket {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    public static void main(String[] args) {

        WebSocketClient client = new ReactorNettyWebSocketClient();
        client.execute(
                URI.create("ws://localhost:8080/rock-paper-scissors"),
                session -> session.send(
                        Mono.just(session.textMessage(writeMessage(new GameStateDto(GameState.IN_PROGRESS, Gesture.ROCK)))))
                        .thenMany(session.receive()
                                .map(WebSocketMessage::getPayloadAsText)
                                .log())
                        .then())
                .block(Duration.ofSeconds(5L));
    }

    private static String writeMessage(GameStateDto message) {
        try {
            return JSON_MAPPER.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
