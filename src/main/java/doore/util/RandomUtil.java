package doore.util;

import java.util.Random;

public class RandomUtil {

    private static final Random RANDOM = new Random();

    public static String generateRandomCode(final char leftLimit, final char rightLimit, final int limit) {
        return RANDOM.ints(leftLimit, rightLimit + 1)
                .filter(i -> Character.isAlphabetic(i) || Character.isDigit(i))
                .limit(limit)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
