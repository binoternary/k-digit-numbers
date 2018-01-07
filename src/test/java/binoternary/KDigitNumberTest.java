package binoternary;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KDigitNumberTest {

    @Test
    void bookExamples() {
        KDigitNumber.Solution b10 = KDigitNumber.solveForBase(10);
        assertEquals(1, b10.solutions().size());
        assertTrue(b10.isSolution(new int[]{3, 8, 1, 6, 5, 4, 7, 2, 9}));

        KDigitNumber.Solution b4 = KDigitNumber.solveForBase(4);
        assertEquals(2, b4.solutions().size());
        assertTrue(b4.isSolution(new int[]{1, 2, 3}));
        assertTrue(b4.isSolution(new int[]{3, 2, 1}));

        KDigitNumber.Solution b6 = KDigitNumber.solveForBase(6);
        assertEquals(2, b6.solutions().size());
        assertTrue(b6.isSolution(new int[]{1, 4, 3, 2, 5}));
        assertTrue(b6.isSolution(new int[]{1, 4, 3, 2, 5}));

        KDigitNumber.Solution b14 = KDigitNumber.solveForBase(14);
        assertEquals(1, b14.solutions().size());
        assertTrue(b14.isSolution(new int[]{9, 12, 3, 10, 5, 4, 7, 6, 11, 8, 1, 2, 13}));
    }

    @Test
    void base2Test() {
        KDigitNumber.Solution b2 = KDigitNumber.solveForBase(2);
        assertEquals(1, b2.solutions().size());
        assertTrue(b2.isSolution(new int[]{1}));
    }
}
