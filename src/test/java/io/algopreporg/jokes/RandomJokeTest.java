package io.algopreporg.jokes;

import static org.junit.jupiter.api.Assertions.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

//class RandomJokeTest {
//    private RandomJoke randomJoke;
//
//    @BeforeEach
//    void setUp() {
//        randomJoke = new RandomJoke();
//    }
//
//    @Tag("should send a joke")
//    @Test
//    void sendJoke() {
//      String joke = "In lieu of shark week. When Chuck Norris goes shark diving. "
//              + "It's the sharks that jump into a steel cage for their protection.";
//        try {
//            randomJoke.sendTelegramMessage(joke);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}