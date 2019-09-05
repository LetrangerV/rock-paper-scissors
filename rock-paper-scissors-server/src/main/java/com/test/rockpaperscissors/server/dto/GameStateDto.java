package com.test.rockpaperscissors.server.dto;

import com.test.rockpaperscissors.server.model.Gesture;
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
