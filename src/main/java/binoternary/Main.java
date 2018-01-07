package binoternary;

import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        int start = 2;
        int end = Integer.MAX_VALUE;

        if (args.length == 1) {
            start = Integer.parseInt(args[0]);
        }
        else if (args.length == 2) {
            start = Integer.parseInt(args[0]);
            end = Integer.parseInt(args[1]);
        }

        IntStream.rangeClosed(start, end)
                .mapToObj(KDigitNumber::solveForBase)
                .forEach(System.out::println);
    }

}
