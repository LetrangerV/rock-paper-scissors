package com.test.rockpaperscissors.server.dto;

import com.test.rockpaperscissors.server.model.Gesture;
import lombok.Data;

@Data
public class GameInputDto {
    private Gesture userInput;
}
