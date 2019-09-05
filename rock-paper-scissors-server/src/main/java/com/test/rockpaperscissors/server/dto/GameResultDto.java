package com.test.rockpaperscissors.server.dto;

import com.test.rockpaperscissors.server.model.GameResult;
import com.test.rockpaperscissors.server.model.Gesture;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameResultDto {
    private Gesture yourInput;
    private Gesture computerInput;
    private GameResult result;
}
