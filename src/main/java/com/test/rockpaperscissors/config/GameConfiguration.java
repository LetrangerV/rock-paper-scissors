package com.test.rockpaperscissors.config;

import com.test.rockpaperscissors.controller.GameController;
import com.test.rockpaperscissors.service.GameService;
import com.test.rockpaperscissors.service.HumanAdaptedGameService;
import com.test.rockpaperscissors.service.WinnerCalculator;
import com.test.rockpaperscissors.service.ai.GameAi;
import com.test.rockpaperscissors.service.ai.StubAi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameConfiguration {
    @Bean
    GameAi gameAi() {
        return new StubAi();
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
