package com.test.rockpaperscissors.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CurrentState {
    private Gesture first;
    private Gesture second;
}
