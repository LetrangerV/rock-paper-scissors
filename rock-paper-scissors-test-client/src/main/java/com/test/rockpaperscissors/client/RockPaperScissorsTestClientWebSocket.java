package com.test.rockpaperscissors.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.rockpaperscissors.client.dto.GameState;
import com.test.rockpaperscissors.client.dto.GameStateDto;
import com.test.rockpaperscissors.client.dto.Gesture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//websocket client for dev/test purpose
@Slf4j
public class RockPaperScissorsTestClientWebSocket {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final String WS_ROCK_PAPER_SCISSORS_URL = "ws://localhost:8080/rock-paper-scissors";
    private static final int THREADS_COUNT = 10;
    private static final int TASK_COUNT = 20;
    private static final long BLOCK_DURATION_SECONDS = 5L;

    public static void main(String[] args) {
        Integer threads = parseThreadsArg();
        Integer tasks = parseTasksArg();

        WebSocketClient client = new ReactorNettyWebSocketClient();

        log.info("Started test client");
        ExecutorService service = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < tasks; i++) {
            service.submit(() -> executeSingleClient(client));
        }
        service.shutdown();
        log.info("Shutdown test client");
    }

    private static Integer parseTasksArg() {
        Integer tasks;
        try {
            tasks = Integer.parseInt(System.getProperty("tasks"));
            log.info("Running test client for {} users/tasks", tasks);
        } catch (NumberFormatException ex) {
            log.info("Number of users/tasks was not specified. Defaulting to {}", TASK_COUNT);
            tasks = TASK_COUNT;
        }
        return tasks;
    }

    private static Integer parseThreadsArg() {
        Integer threads;
        try {
            threads = Integer.parseInt(System.getProperty("threads"));
            log.info("Running test client in {} threads", threads);
        } catch (NumberFormatException ex) {
            log.info("Number of threads was not specified. Defaulting to {}", THREADS_COUNT);
            threads = THREADS_COUNT;
        }
        return threads;
    }

    private static void executeSingleClient(WebSocketClient client) {
        client.execute(
                URI.create(WS_ROCK_PAPER_SCISSORS_URL),
                session -> session.send(
                        Flux.concat(
                                Mono.just(writeStartGameMessage(session)),
                                Mono.just(writeRockMessage(session)),
                                Mono.just(writeRockMessage(session)),
                                Mono.just(writeRockMessage(session)),
                                Mono.just(writeScissorsMessage(session)),
                                Mono.just(writeScissorsMessage(session)),
                                Mono.just(writeScissorsMessage(session)),
                                Mono.just(writeScissorsMessage(session)),
                                Mono.just(writeRockMessage(session)),
                                Mono.just(writeRockMessage(session)),
                                Mono.just(writeRockMessage(session)),
                                Mono.just(writeEndGameMessage(session))
                        )
                        )
                        .thenMany(session.receive()
                                .map(webSocketMessage -> {
                                    final String text = webSocketMessage.getPayloadAsText();
                                    log.info("Received message from server: {}", text);
                                    return text;
                                }))
                        .then())
                .block(Duration.ofSeconds(BLOCK_DURATION_SECONDS));
    }

    private static WebSocketMessage writeEndGameMessage(WebSocketSession session) {
        return session.textMessage(writeMessage(new GameStateDto(GameState.END, Gesture.NONE)));
    }

    private static WebSocketMessage writeStartGameMessage(WebSocketSession session) {
        return session.textMessage(writeMessage(new GameStateDto(GameState.START, Gesture.NONE)));
    }

    private static WebSocketMessage writeScissorsMessage(WebSocketSession session) {
        return session.textMessage(writeMessage(new GameStateDto(GameState.IN_PROGRESS, Gesture.SCISSORS)));
    }

    private static WebSocketMessage writeRockMessage(WebSocketSession session) {
        return session.textMessage(writeMessage(new GameStateDto(GameState.IN_PROGRESS, Gesture.ROCK)));
    }

    private static String writeMessage(GameStateDto message) {
        try {
            return JSON_MAPPER.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
