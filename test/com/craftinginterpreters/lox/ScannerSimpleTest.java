package com.craftinginterpreters.lox;

import org.junit.Test;
import static org.junit.Assert.*;

public class ScannerSimpleTest {

    // ==========================================
    // 1. Basic Precedence (Multiplication/Division first)
    // ==========================================

    @Test
    public void testCalculateResult1() {
        ScannerSimple scanner = new ScannerSimple("3 + 5 * 2");
        assertEquals(13.0, scanner.calculateResult(), 1e-15);
    }

    @Test
    public void testCalculateResult2() {
        ScannerSimple scanner = new ScannerSimple("5 * 2 + 3");
        assertEquals(13.0, scanner.calculateResult(), 1e-15);
    }

    @Test
    public void testCalculateResult3() {
        ScannerSimple scanner = new ScannerSimple("10 - 6 / 2");
        assertEquals(7.0, scanner.calculateResult(), 1e-15);
    }

    @Test
    public void testCalculateResult4() {
        ScannerSimple scanner = new ScannerSimple("8 / 2 - 1");
        assertEquals(3.0, scanner.calculateResult(), 1e-15);
    }

    // ==========================================
    // 2. Left-to-Right Tie-Breakers (Equal Precedence)
    // ==========================================

    @Test
    public void testLeftToRightSubtraction() {
        ScannerSimple scanner = new ScannerSimple("10 - 4 - 2");
        assertEquals(4.0, scanner.calculateResult(), 1e-15); // (10-4)-2 = 4
    }

    @Test
    public void testLeftToRightDivision() {
        ScannerSimple scanner = new ScannerSimple("12 / 3 / 2");
        assertEquals(2.0, scanner.calculateResult(), 1e-15); // (12/3)/2 = 2
    }

    @Test
    public void testMixedEqualPrecedence1() {
        ScannerSimple scanner = new ScannerSimple("6 * 4 / 2");
        assertEquals(12.0, scanner.calculateResult(), 1e-15); // (6*4)/2 = 12
    }

    @Test
    public void testMixedEqualPrecedence2() {
        ScannerSimple scanner = new ScannerSimple("12 / 3 * 2");
        assertEquals(8.0, scanner.calculateResult(), 1e-15); // (12/3)*2 = 8
    }

    // ==========================================
    // 3. Mixed Multi-Step Expressions
    // ==========================================

    @Test
    public void testMultiStepPrecedence() {
        ScannerSimple scanner = new ScannerSimple("2 * 3 + 4 * 5");
        assertEquals(26.0, scanner.calculateResult(), 1e-15); // 6 + 20 = 26
    }

    @Test
    public void testLongMixedExpression1() {
        ScannerSimple scanner = new ScannerSimple("10 + 4 * 2 - 3");
        assertEquals(15.0, scanner.calculateResult(), 1e-15); // 10 + 8 - 3 = 15
    }

    @Test
    public void testLongMixedExpression2() {
        ScannerSimple scanner = new ScannerSimple("5 + 12 / 3 * 2 - 1");
        assertEquals(12.0, scanner.calculateResult(), 1e-15); // 5 + 8 - 1 = 12
    }

    // ==========================================
    // 4. Edge Cases (Decimals & Spaces)
    // ==========================================

    @Test
    public void testDecimalHandling() {
        ScannerSimple scanner = new ScannerSimple("1.5 * 2 + 4.5");
        assertEquals(7.5, scanner.calculateResult(), 1e-15); // 3.0 + 4.5 = 7.5
    }

    @Test
    public void testHeavyWhitespace() {
        ScannerSimple scanner = new ScannerSimple("10   +   2   *   3");
        assertEquals(16.0, scanner.calculateResult(), 1e-15); // 10 + 6 = 16
    }
}