package com.test.rockpaperscissors.dto;

import com.test.rockpaperscissors.model.Gesture;
import lombok.Data;

@Data
public class GameInputDto {
    private Gesture userInput;
}
