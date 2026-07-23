package com.craftinginterpreters.lox;

import org.junit.Test;
import static org.junit.Assert.*;

public class ScannerParserTest {

    // ==========================================
    // Basic Precedence (Multiplication/Division first)
    // ==========================================
    @Test
    public void testCalculateResult1() {
        Scanner scanner = new Scanner("3 + 5 * 2");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(13.0, parser.parse(), 1e-15);
    }

    @Test
    public void testCalculateResult2() {
        Scanner scanner = new Scanner("5 * 2 + 3");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(13.0, parser.parse(), 1e-15);
    }

    @Test
    public void testCalculateResult3() {
        Scanner scanner = new Scanner("10.9 - 6 / 1.5");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(6.9, parser.parse(), 1e-15);
    }

    @Test
    public void testCalculateResult4() {
        Scanner scanner = new Scanner("8 / 2 - 1");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(3.0, parser.parse(), 1e-15);
    }
    
    @Test
    public void testCalculateResult5() {
        Scanner scanner = new Scanner("8 / 2 - 1.7");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(2.3, parser.parse(), 1e-15);
    }

    // ==========================================
    // Left-to-Right Tie-Breakers (Equal Precedence)
    // ==========================================
    @Test
    public void testLeftToRightSubtraction() {
        Scanner scanner = new Scanner("10 - 4 - 2");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(4.0, parser.parse(), 1e-15); // (10-4)-2 = 4
    }

    @Test
    public void testLeftToRightDivision() {
        Scanner scanner = new Scanner("12 / 3 / 2");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(2.0, parser.parse(), 1e-15); // (12/3)/2 = 2
    }

    @Test
    public void testMixedEqualPrecedence1() {
        Scanner scanner = new Scanner("6 * 4 / 2");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(12.0, parser.parse(), 1e-15); // (6*4)/2 = 12
    }

    @Test
    public void testMixedEqualPrecedence2() {
        Scanner scanner = new Scanner("12 / 3 * 2");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(8.0, parser.parse(), 1e-15); // (12/3)*2 = 8
    }

    // ==========================================
    // Mixed Multi-Step Expressions
    // ==========================================
    @Test
    public void testMultiStepPrecedence() {
        Scanner scanner = new Scanner("2 * 3 + 4 * 5");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(26.0, parser.parse(), 1e-15); // 6 + 20 = 26
    }

    @Test
    public void testLongMixedExpression1() {
        Scanner scanner = new Scanner("10 + 4 * 2 - 3");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(15.0, parser.parse(), 1e-15); // 10 + 8 - 3 = 15
    }

    @Test
    public void testLongMixedExpression2() {
        Scanner scanner = new Scanner("5 + 12 / 3 * 2 - 1");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(12.0, parser.parse(), 1e-15); // 5 + 8 - 1 = 12
    }

    // ==========================================
    // Edge Cases (Decimals & Spaces)
    // ==========================================
    @Test
    public void testDecimalHandling() {
        Scanner scanner = new Scanner("1.5 * 2 + 4.5");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(7.5, parser.parse(), 1e-15); // 3.0 + 4.5 = 7.5
    }

    @Test
    public void testHeavyWhitespace() {
        Scanner scanner = new Scanner("10   +   2   *   3");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(16.0, parser.parse(), 1e-15); // 10 + 6 = 16
    }

    // ==========================================
    // Deeper Left-to-Right Associativity (+ / -)
    // ==========================================
    @Test
    public void testMixedPlusMinusChain() {
        Scanner scanner = new Scanner("10 - 4 + 3 - 1");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(8.0, parser.parse(), 1e-15); // ((10-4)+3)-1 = 8
    }

    @Test
    public void testFourTermSubtractionChain() {
        Scanner scanner = new Scanner("10 - 3 - 2 - 1");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(4.0, parser.parse(), 1e-15); // ((10-3)-2)-1 = 4
    }

    @Test
    public void testFourTermMixedChain() {
        Scanner scanner = new Scanner("1 + 2 - 3 + 4 - 5");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(-1.0, parser.parse(), 1e-15); // (((1+2)-3)+4)-5 = -1
    }

    // ==========================================
    // Single Operand
    // ==========================================
    @Test
    public void testSingleOperand() {
        Scanner scanner = new Scanner("5");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(5.0, parser.parse(), 1e-15);
    }

    @Test
    public void testSingleOperandDecimal() {
        Scanner scanner = new Scanner("3.75");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(3.75, parser.parse(), 1e-15);
    }

    // ==========================================
    // Negative Results
    // ==========================================
    @Test
    public void testNegativeResultSimple() {
        Scanner scanner = new Scanner("3 - 10");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(-7.0, parser.parse(), 1e-15);
    }

    @Test
    public void testNegativeResultMixed() {
        Scanner scanner = new Scanner("2 * 3 - 20");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(-14.0, parser.parse(), 1e-15); // 6 - 20 = -14
    }

    // ==========================================
    // Non-Integer Division Results
    // ==========================================
    @Test
    public void testFractionalDivisionResult() {
        Scanner scanner = new Scanner("7 / 2 + 1");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(4.5, parser.parse(), 1e-15); // 3.5 + 1 = 4.5
    }

    @Test
    public void testFractionalDivisionChain() {
        Scanner scanner = new Scanner("10 / 4 / 2");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(1.25, parser.parse(), 1e-15); // (10/4)/2 = 1.25
    }

    @Test
    public void testFirstNumberNegative() {
        Scanner scanner = new Scanner("-30 + 20");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(-10, parser.parse(), 1e-15); // -30 + 20 = -10
    }

    @Test
    public void testMultiplyWithNegativeNumber() {
        Scanner scanner = new Scanner("3 * -2");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(-6, parser.parse(), 1e-15); // 3 * -2 = -6
    }

    @Test
    public void testMultiplyTwoNegativeNumbers() {
        Scanner scanner = new Scanner("-3 * -2");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(6, parser.parse(), 1e-15); // 3 * -2 = -6
    }
    
    // ==========================================
    // Parantheses
    // ==========================================

    @Test
    public void testCalculateResultParens1() {
        Scanner scanner = new Scanner("(3 + 5) * 2");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(16.0, parser.parse(), 1e-15);
    }

    @Test
    public void testCalculateResultParens2() {
        Scanner scanner = new Scanner("2 * (3 + 4)");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(14.0, parser.parse(), 1e-15);
    }

    @Test
    public void testCalculateResultParens3() {
        Scanner scanner = new Scanner("(10 - 4) / 3");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(2.0, parser.parse(), 1e-15);
    }

    @Test
    public void testCalculateResultParens4() {
        Scanner scanner = new Scanner("10 - (4 + 3)");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(3.0, parser.parse(), 1e-15);
    }

    @Test
    public void testCalculateResultParens5() {
        Scanner scanner = new Scanner("(2 + 3) * (4 - 1)");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(15.0, parser.parse(), 1e-15);
    }

    @Test
    public void testCalculateResultParens6() {
        Scanner scanner = new Scanner("3 * (4 + 2) / 2");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(9.0, parser.parse(), 1e-15);
    }

    @Test
    public void testCalculateResultParens7() {
        Scanner scanner = new Scanner("(6 / 2) + 4");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(7.0, parser.parse(), 1e-15);
    }

    @Test
    public void testCalculateResultParens8() {
        Scanner scanner = new Scanner("((2 + 3) * 4)");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(20.0, parser.parse(), 1e-15);
    }

    @Test
    public void testCalculateResultParens9() {
        Scanner scanner = new Scanner("2 * (3 * (4 + 1))");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(30.0, parser.parse(), 1e-15);
    }

    @Test
    public void testCalculateResultParens10() {
        Scanner scanner = new Scanner("(1.5 + 2.5) * 2");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(8.0, parser.parse(), 1e-15);
    }
    
    // ==========================================
    // Multiple minus signs
    // ==========================================

    @Test
    public void testMultipleMinus1() {
        Scanner scanner = new Scanner("---3");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(-3, parser.parse(), 1e-15);
    }
    
    @Test
    public void testMultipleMinus2() {
        Scanner scanner = new Scanner("---3--4");
        Parser parser = new Parser(scanner.getTokens());
        assertEquals(1, parser.parse(), 1e-15);
    }
}
