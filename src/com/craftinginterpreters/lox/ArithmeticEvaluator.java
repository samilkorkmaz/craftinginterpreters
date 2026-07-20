package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

/*
 * @author skorkmaz
 * @date July 2026
 */
class ArithmeticEvaluator {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private double runningTotal = 0.0;
    private double lastValue = 0.0;
    private boolean hasTerm = false; // guards the very first push
    TokenType activeOp;
    TokenType prevOp;

    ArithmeticEvaluator(String source) {
        this.source = source;
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
    }

    public List<Token> getTokens() {
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '+':
                activeOp = PLUS;
                addToken(activeOp);
                break;
            case '-':
                prevOp = activeOp; // To record if there was a * or / operation before sign operation
                activeOp = MINUS;
                addToken(activeOp);
                break;
            case '*':
                activeOp = STAR;
                addToken(activeOp);
                break;
            case '/':
                activeOp = SLASH;
                addToken(activeOp);
                break;
            case ' ':
            case '\r':
            case '\t':
                break; // ignore whitespace
            case '\n':
                line++;
                break;
            default: // multi character string like "123.45"
                if (isDigit(c)) {
                    number();
                    double value = (Double) tokens.get(tokens.size() - 1).literal;
                    boolean continuesTerm = false; // does this number extend lastValue, or start a new term?

                    if (null != activeOp) {
                        switch (activeOp) { // check operator before number
                            case STAR:
                                value = lastValue * value;
                                continuesTerm = true;
                                break;
                            case SLASH:
                                value = lastValue / value; // Since division has higher precedence than +-, apply it to the two numbers and remove divisiob from the operatorStack
                                continuesTerm = true;
                                break;
                            case MINUS:
                                value = -value;
                                if (prevOp == STAR) {
                                    value *= lastValue;
                                    continuesTerm = true;
                                } else if (prevOp == SLASH) {
                                    value = lastValue / value;
                                    continuesTerm = true;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    activeOp = null;
                    if (continuesTerm) {
                        lastValue = value; // replace the "top of stack" in place
                    } else {
                        if (hasTerm) {
                            runningTotal += lastValue; // flush the finished term
                        }
                        lastValue = value;
                        hasTerm = true;
                    }
                } else {
                    Lox.error(line, "Unexpected character: '" + c + "'");
                }
                break;
        }
    }

    public double getResult() {
        return runningTotal + lastValue; // flush whatever term is still pending;
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }
        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            advance(); // consume the "."
            while (isDigit(peek())) {
                advance();
            }
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }

    private char advance() { // each call pushes current forward as a side-effect
        return source.charAt(current++);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
