package com.tsys.utils;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomStringGenerator {
    private static final Random random = new Random();

    static String generate(final int minSize, final int maxSize) {

        int length = (maxSize <= minSize) ? minSize: maxSize;

        final StringBuilder name = random.ints(33, 126)
                .limit(length)
                .mapToObj(c -> Character.valueOf((char) c))
                .filter(Character::isAlphabetic)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);

        return name.toString();
    }

    static Stream<Character> generateSpecialCharacters(final int minSize, final int maxSize) {
        int length = (maxSize <= minSize) ? minSize: maxSize;
        int [][] specialCharsRanges =
                new int [][] {
                        {33, 45},
                        {58, 64},
                        {91, 96}
                };

        final int index = random.nextInt(3);
        final int lower = specialCharsRanges[index][0];
        final int upper = specialCharsRanges[index][1];

        return random.ints(lower, upper)
                .limit(length)
                .mapToObj(c -> Character.valueOf((char) c));
    }

    static Stream<Character> generateAlphabets(final int minSize, final int maxSize, final boolean isLowerCase) {
        int length = (maxSize <= minSize) ? minSize: maxSize;
        int lower = isLowerCase? 97 : 65;
        int upper = isLowerCase? 122: 90;

        return random.ints(lower, upper)
                .limit(length)
                .mapToObj(c -> Character.valueOf((char) c));
    }

    static Stream<Character> generateDigits(final int minSize, final int maxSize) {
        int length = (maxSize <= minSize) ? minSize: maxSize;

        return random.ints(48, 57)
                .limit(length)
                .mapToObj(c -> Character.valueOf((char) c));
    }

    static String password(int minSize, int maxSize) {
        final Stream<Character> lowerCaseAlpha = generateAlphabets(1, 3, true);
        final Stream<Character> upperCaseAlpha = generateAlphabets(1, 3, false);
        final Stream<Character> digits = generateDigits(1, 3);
        final Stream<Character> specialCharacters = generateSpecialCharacters(1, 3);

        final List<Character> password =
                Stream.concat(lowerCaseAlpha,
                        Stream.concat(upperCaseAlpha,
                                Stream.concat(digits, specialCharacters)))
                        .collect(Collectors.toList());

        java.util.Collections.shuffle(password);
        System.out.println("password = " + password);
        int length = maxSize <= minSize ? minSize : maxSize;
        return password.stream()
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
