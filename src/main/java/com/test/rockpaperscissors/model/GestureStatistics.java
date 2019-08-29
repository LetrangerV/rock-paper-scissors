package com.test.rockpaperscissors.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GestureStatistics {
    private Double probability = 0.33333333333333;
    private Double numberOfObservations = 0d;
}
