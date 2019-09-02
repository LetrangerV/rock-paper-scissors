package com.test.rockpaperscissors.model;

import com.test.rockpaperscissors.dto.UserStats;
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
