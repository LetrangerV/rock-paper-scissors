package com.test.rockpaperscissors.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserStats {
    private Long totalGames;
    private Long userWon;
    private Long tied;
    private Long computerWon;

    @Override
    public String toString() {
        return String.format("Total played: %d; You won: %d; Tied: %d; You lost: %d; Winrate: %.2f %%", totalGames, userWon, tied, computerWon, calculateWinrate());
    }

    private float calculateWinrate() {
        return 1.0f * userWon / totalGames * 100;
    }
}
