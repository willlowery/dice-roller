package com.tomakeitgo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StringsTest {

    @ParameterizedTest
    @MethodSource("chunkCases")
    void chunk(String input, int width, List<String> expected) {
        assertEquals(expected, Strings.chunk(input, width));
    }

    static Stream<Arguments> chunkCases() {
        return Stream.of(
                Arguments.of("", 5, List.of("")),
                Arguments.of("abc", 0, List.of("")),
                Arguments.of("abc", -1, List.of("")),
                Arguments.of("abc", 10, List.of("abc")),
                Arguments.of("abcdef", 2, List.of("ab", "cd", "ef")),
                Arguments.of("abcde", 2, List.of("ab", "cd", "e"))
        );
    }
}
