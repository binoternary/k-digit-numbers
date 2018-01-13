package binoternary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;
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
    private final BigInteger biBase; // small optimization
    private final int[] solutionPrefix;
    private final BitSet availableDigits;
    private final BigInteger decimalValue;

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

    static KDigitNumber of(int base, int[] digits) {
        KDigitNumber n = null;
        for (int d : digits) {
            if (n == null) {
                n = new KDigitNumber(base, d);
            }
            else {
                n = new KDigitNumber(n, d);
            }
        }
        return n;
    }

    private KDigitNumber(int base, int firstDigit) {
        if (base < 2) {
            throw new IllegalArgumentException("base: " + base);
        }
        this.base = base;
        if (isInvalidDigit(firstDigit)) {
            throw new IllegalArgumentException("digit: " + firstDigit);
        }
        solutionPrefix = new int[]{firstDigit};
        availableDigits = new BitSet(base);
        availableDigits.set(1, base);
        availableDigits.clear(firstDigit);
        biBase = BigInteger.valueOf(base);
        decimalValue = BigInteger.valueOf(firstDigit);
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
        biBase = previous.biBase;
        decimalValue = previous.decimalValue.multiply(biBase).add(BigInteger.valueOf(nextDigit));
    }

    public int base() {
        return base;
    }

    public int[] digits() {
        return Arrays.copyOf(solutionPrefix, solutionPrefix.length);
    }

    public BigInteger getDecimalValue() {
        return decimalValue;
    }

    private Stream<KDigitNumber> solutions() {
        if (availableDigits.isEmpty()) {
            return Stream.of(this);
        }
        return availableDigits.stream()
                .mapToObj(d -> new KDigitNumber(this, d))
                .filter(KDigitNumber::isValidPrefix)
                .map(KDigitNumber::logSignificantProgress)
                .map(KDigitNumber::solutions)
                .flatMap(s -> s);
    }

    private boolean isValidPrefix() {
        return decimalValue.mod(BigInteger.valueOf(solutionPrefix.length)).equals(BigInteger.ZERO);
    }

    private KDigitNumber logSignificantProgress() {
        if (isValidSolution()) {
            log.info("! {}", this);
        }
        else if (base - solutionPrefix.length < 5) {
            log.trace(this);
        }
        return this;
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

    private boolean isValidPrefix(int prefixLength) {
        return prefixLength == 1
                || digitsToDecimal(prefixLength).mod(BigInteger.valueOf(prefixLength)).equals(BigInteger.ZERO);
    }

    private BigInteger digitsToDecimal(int firstN) {
        BigInteger dec = BigInteger.ZERO;
        for (int i = 0, c = firstN - 1; i < firstN; i++, c--) {
            dec = dec.add(BigInteger.valueOf(solutionPrefix[i]).multiply(BigInteger.valueOf(base).pow(c)));
        }
        return dec;
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
