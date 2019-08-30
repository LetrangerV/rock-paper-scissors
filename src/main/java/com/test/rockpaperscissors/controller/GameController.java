package com.test.rockpaperscissors.controller;

import com.test.rockpaperscissors.dto.GameInputDto;
import com.test.rockpaperscissors.dto.GameResultDto;
import com.test.rockpaperscissors.model.GameResult;
import com.test.rockpaperscissors.model.Gesture;
import com.test.rockpaperscissors.service.GameService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@RequestMapping("/api/v1/rockpaperscissors")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    //todo tests
    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Mono<GameResultDto> startGame(@RequestBody GameInputDto inputDto) {
        Pair<Gesture, GameResult> gameResult = gameService.play(inputDto.getUserInput());

        return Mono.just(
                GameResultDto.builder()
                        .yourInput(inputDto.getUserInput())
                        .computerInput(gameResult.getLeft())
                        .result(gameResult.getRight())
                        .build()
        );
    }

    //https://www.baeldung.com/spring-5-reactive-websockets
}
