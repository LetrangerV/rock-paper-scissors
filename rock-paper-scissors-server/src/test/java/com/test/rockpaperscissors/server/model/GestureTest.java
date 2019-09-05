package com.test.rockpaperscissors.server.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GestureTest {
    @Test
    public void testFromValueRock() {
        Gesture result = Gesture.fromValue("ROCK");

        assertEquals(Gesture.ROCK, result);
    }

    @Test
    public void testFromValuePaper() {
        Gesture result = Gesture.fromValue("PAPER");

        assertEquals(Gesture.PAPER, result);
    }

    @Test
    public void testFromValueScissors() {
        Gesture result = Gesture.fromValue("SCISSORS");

        assertEquals(Gesture.SCISSORS, result);
    }

    @Test
    public void testFromValueNone() {
        Gesture result = Gesture.fromValue("NONE");

        assertEquals(Gesture.NONE, result);
    }

    @Test
    public void testFromValueCaseSensitive() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> Gesture.fromValue("scissors"));
    }

    @Test
    public void testFromValueInvalid() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> Gesture.fromValue("Never gonna give you up"));
    }
}