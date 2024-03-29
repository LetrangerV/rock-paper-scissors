package com.test.rockpaperscissors.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameStateDto {
    private GameState state;
    private Gesture userInput;
}
