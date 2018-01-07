package binoternary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class KDigitNumber {

    private static final Logger log = LogManager.getLogger();

    private final int base;
    private final int[] solutionPrefix;
    private final BitSet availableDigits;

    public static Solution solveForBase(int base) {
        log.info("Solving for base {} ...", base);
        long start = System.nanoTime();
        List<KDigitNumber> solutionsForBase = IntStream.range(1, base)
                .parallel()
                .mapToObj(i -> new KDigitNumber(base, i))
                .map(KDigitNumber::solutions)
                .flatMap(s -> s)
                .filter(KDigitNumber::isValidSolution)
                .collect(toList());
        long end = System.nanoTime();
        log.info("Done solving for base {} in {}.", base, Duration.ofNanos(end - start));

        return new Solution(base, solutionsForBase);
    }

    public int base() {
        return base;
    }

    public int[] digits() {
        return Arrays.copyOf(solutionPrefix, solutionPrefix.length);
    }

    private KDigitNumber(int base, int firstDigit) {
        if (base < 2) {
            throw new IllegalArgumentException("base: " + base);
        }
        this.base = base;
        if (isInvalidDigit(firstDigit)) {
            throw new IllegalArgumentException("digit: " + firstDigit);
        }
        this.solutionPrefix = new int[]{firstDigit};
        this.availableDigits = new BitSet(base);
        availableDigits.set(1, base);
        availableDigits.clear(firstDigit);
    }

    private KDigitNumber(KDigitNumber previous, int nextDigit) {
        base = previous.base;
        if (isElementInArray(previous.solutionPrefix, nextDigit) || isInvalidDigit(nextDigit)) {
            throw new IllegalArgumentException("nextDigit: " + nextDigit);
        }
        solutionPrefix = Arrays.copyOf(previous.solutionPrefix, previous.solutionPrefix.length + 1);
        solutionPrefix[solutionPrefix.length - 1] = nextDigit;


        availableDigits = new BitSet();
        availableDigits.or(previous.availableDigits);
        availableDigits.clear(nextDigit);
    }

    private Stream<KDigitNumber> solutions() {
        if (availableDigits.isEmpty()) {
            return Stream.of(this);
        }
        return availableDigits.stream()
                .mapToObj(d -> new KDigitNumber(this, d))
                .filter(s -> s.isValidPrefix(solutionPrefix.length))
                .map(KDigitNumber::solutions)
                .flatMap(s -> s)
                ;
    }

    private boolean isValidSolution() {
        if (solutionPrefix.length != base - 1) {
            return false;
        }
        for (int i = base - 1; i > 1; i--) {
            if (!isValidPrefix(i)) {
                return false;
            }
        }
        return true;
    }

    private long digitsToDecimal(int firstN) {
        long dec = 0L;
        for (int i = 0, c = firstN - 1; i < firstN; i++, c--) {
            dec += Math.pow(base, c) * solutionPrefix[i];
        }
        return dec;
    }

    private boolean isValidPrefix(int prefixLength) {
        return prefixLength == 1 || digitsToDecimal(prefixLength) % prefixLength == 0;
    }

    private boolean isElementInArray(int[] arr, int e) {
        for (int i : arr) {
            if (i == e) {
                return true;
            }
        }
        return false;
    }

    private boolean isInvalidDigit(int d) {
        return d <= 0 || d >= base;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('b').append(base).append('-');
        String prefix = "";
        for (int d : solutionPrefix) {
            sb.append(prefix).append(d);
            prefix = "_";
        }
        if (!availableDigits.isEmpty()) {
            sb.append('_').append("...");
        }

        return sb.toString();
    }

    public static class Solution {
        private final int base;
        private final List<KDigitNumber> solutions;

        Solution(int base, List<KDigitNumber> solutions) {
            this.base = base;
            for (KDigitNumber solution : solutions) {
                if (solution.base != this.base) {
                    throw new IllegalArgumentException("Illegal solution for base-" + base);
                }
            }
            this.solutions = new ArrayList<>(solutions);
        }

        public int base() {
            return base;
        }

        public List<KDigitNumber> solutions() {
            return Collections.unmodifiableList(solutions);
        }

        public boolean isSolution(int[] digits) {
            for (KDigitNumber solution : solutions) {
                if (Arrays.equals(solution.digits(), digits)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return "base-" + base + " : " + solutions;
        }
    }
}
