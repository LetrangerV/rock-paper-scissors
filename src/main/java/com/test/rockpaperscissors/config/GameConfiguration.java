package com.test.rockpaperscissors.config;

import com.test.rockpaperscissors.controller.GameController;
import com.test.rockpaperscissors.controller.RockPaperScissorsWebSocketHandler;
import com.test.rockpaperscissors.service.GameService;
import com.test.rockpaperscissors.service.HumanAdaptedGameService;
import com.test.rockpaperscissors.service.WinnerCalculator;
import com.test.rockpaperscissors.service.ai.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class GameConfiguration {
    @Bean
    RandomAi randomAi() {
        return new RandomAi();
    }

    @Bean
    MarkovChain markovChain(RandomAi randomAi) {
        return new MarkovChain(randomAi);
    }

    @Bean
    GameAi gameAi(MarkovChain markovChain, RandomAi randomAi) {
        return new MarkovChainBasedAi(markovChain, randomAi);
    }

    @Bean
    GameService gameService(WinnerCalculator winnerCalculator,
                            GameAi gameAi) {
        return new HumanAdaptedGameService(winnerCalculator, gameAi);
    }

    @Bean
    GameController gameController(GameService gameService) {
        return new GameController(gameService);
    }

    @Bean
    WinnerCalculator winnerCalculator() {
        return new WinnerCalculator();
    }

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Bean
    public HandlerMapping webSocketHandlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/rock-paper-scissors", webSocketHandler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    @Bean
    RockPaperScissorsWebSocketHandler reactiveWebSocketHandler(GameService gameService) {
        return new RockPaperScissorsWebSocketHandler(gameService);
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
