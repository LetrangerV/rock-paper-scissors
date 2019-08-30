package com.test.rockpaperscissors.config;

import com.test.rockpaperscissors.controller.GameController;
import com.test.rockpaperscissors.service.GameService;
import com.test.rockpaperscissors.service.HumanAdaptedGameService;
import com.test.rockpaperscissors.service.WinnerCalculator;
import com.test.rockpaperscissors.service.ai.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
