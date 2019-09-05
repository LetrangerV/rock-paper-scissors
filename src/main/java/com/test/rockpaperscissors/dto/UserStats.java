package com.test.rockpaperscissors.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserStats {
    private static final int PERCENT = 100;
    private long totalGames;
    private long userWon;
    private long tied;
    private long computerWon;

    @Override
    public String toString() {
        return String.format("Total played: %d; You won: %d; Tied: %d; You lost: %d; Winrate: %.2f %%",
                totalGames, userWon, tied, computerWon, calculateWinrate());
    }

    private float calculateWinrate() {
        return 1.0f * userWon / totalGames * PERCENT;
    }
}
