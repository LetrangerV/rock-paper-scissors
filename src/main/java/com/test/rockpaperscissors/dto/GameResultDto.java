package com.test.rockpaperscissors.dto;

import com.test.rockpaperscissors.model.GameResult;
import com.test.rockpaperscissors.model.Gesture;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameResultDto {
    private Gesture yourInput;
    private Gesture computerInput;
    private GameResult result;
}
