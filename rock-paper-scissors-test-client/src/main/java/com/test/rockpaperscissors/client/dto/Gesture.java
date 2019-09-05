package com.test.rockpaperscissors.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum Gesture {
    ROCK,
    SCISSORS,
    PAPER,
    NONE; //null-object

    @JsonCreator
    public static Gesture fromValue(String value) {
        return Arrays.stream(values())
                .filter(existingValue -> existingValue.toString().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported value for gesture: " + value));
    }
}
