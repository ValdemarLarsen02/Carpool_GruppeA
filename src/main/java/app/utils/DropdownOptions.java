package app.utils;

import java.util.stream.IntStream;

public class DropdownOptions {
    //Generere valgmuligheder til dropdown menu
    public static String[] generateOptions(int start, int end, int step) {
        return IntStream.rangeClosed(start, end)
                .filter(i -> i % step == 0)
                .mapToObj(String::valueOf)
                .toArray(String[]::new);
    }
}
