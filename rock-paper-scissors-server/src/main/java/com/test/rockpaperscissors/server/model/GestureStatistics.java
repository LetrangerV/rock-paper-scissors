package com.test.rockpaperscissors.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GestureStatistics {
    private static final double EQUAL_GESTURE_PROBABILITY = 0.33333333333333;
    private Double probability = EQUAL_GESTURE_PROBABILITY;
    private Double numberOfObservations = 0d;
}
