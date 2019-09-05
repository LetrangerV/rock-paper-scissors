package com.test.rockpaperscissors.server.model;

import com.test.rockpaperscissors.server.dto.UserStats;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class Context {
    private Map<CurrentState, Map<Gesture, GestureStatistics>> transitionProbabilities;
    private CurrentState previousState;
    private UserStats userStats;
}
