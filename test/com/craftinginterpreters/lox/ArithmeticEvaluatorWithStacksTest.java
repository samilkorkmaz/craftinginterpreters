package com.craftinginterpreters.lox;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArithmeticEvaluatorWithStacksTest {

    // ==========================================
    // Basic Precedence (Multiplication/Division first)
    // ==========================================
    @Test
    public void testCalculateResult1() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("3 + 5 * 2");
        assertEquals(13.0, scanner.getResult(), 1e-15);
    }

    @Test
    public void testCalculateResult2() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("5 * 2 + 3");
        assertEquals(13.0, scanner.getResult(), 1e-15);
    }

    @Test
    public void testCalculateResult3() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("10 - 6 / 2");
        assertEquals(7.0, scanner.getResult(), 1e-15);
    }

    @Test
    public void testCalculateResult4() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("8 / 2 - 1");
        assertEquals(3.0, scanner.getResult(), 1e-15);
    }

    // ==========================================
    // Left-to-Right Tie-Breakers (Equal Precedence)
    // ==========================================
    @Test
    public void testLeftToRightSubtraction() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("10 - 4 - 2");
        assertEquals(4.0, scanner.getResult(), 1e-15); // (10-4)-2 = 4
    }

    @Test
    public void testLeftToRightDivision() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("12 / 3 / 2");
        assertEquals(2.0, scanner.getResult(), 1e-15); // (12/3)/2 = 2
    }

    @Test
    public void testMixedEqualPrecedence1() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("6 * 4 / 2");
        assertEquals(12.0, scanner.getResult(), 1e-15); // (6*4)/2 = 12
    }

    @Test
    public void testMixedEqualPrecedence2() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("12 / 3 * 2");
        assertEquals(8.0, scanner.getResult(), 1e-15); // (12/3)*2 = 8
    }

    // ==========================================
    // Mixed Multi-Step Expressions
    // ==========================================
    @Test
    public void testMultiStepPrecedence() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("2 * 3 + 4 * 5");
        assertEquals(26.0, scanner.getResult(), 1e-15); // 6 + 20 = 26
    }

    @Test
    public void testLongMixedExpression1() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("10 + 4 * 2 - 3");
        assertEquals(15.0, scanner.getResult(), 1e-15); // 10 + 8 - 3 = 15
    }

    @Test
    public void testLongMixedExpression2() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("5 + 12 / 3 * 2 - 1");
        assertEquals(12.0, scanner.getResult(), 1e-15); // 5 + 8 - 1 = 12
    }

    // ==========================================
    // Edge Cases (Decimals & Spaces)
    // ==========================================
    @Test
    public void testDecimalHandling() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("1.5 * 2 + 4.5");
        assertEquals(7.5, scanner.getResult(), 1e-15); // 3.0 + 4.5 = 7.5
    }

    @Test
    public void testHeavyWhitespace() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("10   +   2   *   3");
        assertEquals(16.0, scanner.getResult(), 1e-15); // 10 + 6 = 16
    }

    // ==========================================
    // Deeper Left-to-Right Associativity (+ / -)
    // ==========================================
    @Test
    public void testMixedPlusMinusChain() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("10 - 4 + 3 - 1");
        assertEquals(8.0, scanner.getResult(), 1e-15); // ((10-4)+3)-1 = 8
    }

    @Test
    public void testFourTermSubtractionChain() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("10 - 3 - 2 - 1");
        assertEquals(4.0, scanner.getResult(), 1e-15); // ((10-3)-2)-1 = 4
    }

    @Test
    public void testFourTermMixedChain() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("1 + 2 - 3 + 4 - 5");
        assertEquals(-1.0, scanner.getResult(), 1e-15); // (((1+2)-3)+4)-5 = -1
    }

    // ==========================================
    // Single Operand
    // ==========================================
    @Test
    public void testSingleOperand() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("5");
        assertEquals(5.0, scanner.getResult(), 1e-15);
    }

    @Test
    public void testSingleOperandDecimal() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("3.75");
        assertEquals(3.75, scanner.getResult(), 1e-15);
    }

    // ==========================================
    // Negative Results
    // ==========================================
    @Test
    public void testNegativeResultSimple() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("3 - 10");
        assertEquals(-7.0, scanner.getResult(), 1e-15);
    }

    @Test
    public void testNegativeResultMixed() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("2 * 3 - 20");
        assertEquals(-14.0, scanner.getResult(), 1e-15); // 6 - 20 = -14
    }

    // ==========================================
    // Non-Integer Division Results
    // ==========================================
    @Test
    public void testFractionalDivisionResult() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("7 / 2 + 1");
        assertEquals(4.5, scanner.getResult(), 1e-15); // 3.5 + 1 = 4.5
    }

    @Test
    public void testFractionalDivisionChain() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("10 / 4 / 2");
        assertEquals(1.25, scanner.getResult(), 1e-15); // (10/4)/2 = 1.25
    }

    @Test
    public void testFirstNumberNegative() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("-30 + 20");
        assertEquals(-10, scanner.getResult(), 1e-15); // -30 + 20 = -10
    }

    @Test
    public void testMultiplyWithNegativeNumber() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("3 * -2");
        assertEquals(-6, scanner.getResult(), 1e-15); // 3 * -2 = -6
    }

    @Test
    public void testMultiplyTwoNegativeNumbers() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("-3 * -2");
        assertEquals(6, scanner.getResult(), 1e-15); // 3 * -2 = -6
    }
    
    // ==========================================
    // Parantheses
    // ==========================================

    @Test
    public void testCalculateResultParens1() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("(3 + 5) * 2");
        assertEquals(16.0, scanner.getResult(), 1e-15);
    }

    @Test
    public void testCalculateResultParens2() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("2 * (3 + 4)");
        assertEquals(14.0, scanner.getResult(), 1e-15);
    }

    @Test
    public void testCalculateResultParens3() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("(10 - 4) / 3");
        assertEquals(2.0, scanner.getResult(), 1e-15);
    }

    @Test
    public void testCalculateResultParens4() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("10 - (4 + 3)");
        assertEquals(3.0, scanner.getResult(), 1e-15);
    }

    @Test
    public void testCalculateResultParens5() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("(2 + 3) * (4 - 1)");
        assertEquals(15.0, scanner.getResult(), 1e-15);
    }

    @Test
    public void testCalculateResultParens6() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("3 * (4 + 2) / 2");
        assertEquals(9.0, scanner.getResult(), 1e-15);
    }

    @Test
    public void testCalculateResultParens7() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("(6 / 2) + 4");
        assertEquals(7.0, scanner.getResult(), 1e-15);
    }

    @Test
    public void testCalculateResultParens8() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("((2 + 3) * 4)");
        assertEquals(20.0, scanner.getResult(), 1e-15);
    }

    @Test
    public void testCalculateResultParens9() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("2 * (3 * (4 + 1))");
        assertEquals(30.0, scanner.getResult(), 1e-15);
    }

    @Test
    public void testCalculateResultParens10() {
        ArithmeticEvaluator scanner = new ArithmeticEvaluator("(1.5 + 2.5) * 2");
        assertEquals(8.0, scanner.getResult(), 1e-15);
    }
}
