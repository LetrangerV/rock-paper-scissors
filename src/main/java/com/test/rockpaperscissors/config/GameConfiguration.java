package com.test.rockpaperscissors.config;

import com.test.rockpaperscissors.controller.RockPaperScissorsWebSocketHandler;
import com.test.rockpaperscissors.service.GameService;
import com.test.rockpaperscissors.service.GameStateProcessor;
import com.test.rockpaperscissors.service.HumanAdaptedGameService;
import com.test.rockpaperscissors.service.WinnerCalculator;
import com.test.rockpaperscissors.service.ai.GameAi;
import com.test.rockpaperscissors.service.ai.MarkovChainBasedAi;
import com.test.rockpaperscissors.service.ai.MarkovChainBasedPredictor;
import com.test.rockpaperscissors.service.ai.RandomAi;
import com.test.rockpaperscissors.service.ai.TransitionMatrixUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    MarkovChainBasedPredictor markovChainBasedPredictor(RandomAi randomAi) {
        return new MarkovChainBasedPredictor(randomAi);
    }

    @Bean
    TransitionMatrixUpdater transitionMatrixUpdater(@Value("${decay:1}") float decay) {
        return new TransitionMatrixUpdater(decay);
    }

    @Bean
    GameAi gameAi(MarkovChainBasedPredictor markovChainBasedPredictor, RandomAi randomAi,
                  TransitionMatrixUpdater transitionMatrixUpdater) {
        return new MarkovChainBasedAi(markovChainBasedPredictor, randomAi, transitionMatrixUpdater);
    }

    @Bean
    GameService gameService(WinnerCalculator winnerCalculator,
                            GameAi gameAi) {
        return new HumanAdaptedGameService(winnerCalculator, gameAi);
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
    GameStateProcessor gameStateProcessor(GameService gameService) {
        return new GameStateProcessor(gameService);
    }

    @Bean
    RockPaperScissorsWebSocketHandler reactiveWebSocketHandler(GameStateProcessor gameStateProcessor) {
        return new RockPaperScissorsWebSocketHandler(gameStateProcessor);
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
