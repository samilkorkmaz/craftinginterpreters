package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;
import java.util.ArrayDeque;
import java.util.Deque;

/*
 * Compared to Scanner.java: *
 * - No keywords map or identifier() — letters are an error *
 * - No string scanning 
 * - No // comment handling 
 * - No two-character tokens (!=, ==, etc.) *
 * For "(1 + 3) * 123 / (5-1) - 120" it would produce: 
 * LEFT_PAREN ( null 
 * NUMBER 1 1.0 
 * PLUS + null 
 * NUMBER 3 3.0 
 * RIGHT_PAREN ) null 
 * STAR * null 
 * NUMBER 123 123.0 
 * SLASH / null
 * LEFT_PAREN ( null 
 * NUMBER 5 5.0 
 * MINUS - null 
 * NUMBER 1 1.0 
 * RIGHT_PAREN ) null 
 * MINUS - null 
 * NUMBER 120 120.0 
 * EOF null *
 * @author OEM
 */
class ScannerSimple {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private TokenType activeOp = PLUS; // Keeps track of the last seen operator (default to PLUS)
    // Stacks to handle arithmetic operator precedence, i.e. 3 + 5 * 2 = 13, not 16
    Deque<Double> numberStack = new ArrayDeque<>();
    Deque<TokenType> operatorStack = new ArrayDeque<>();

    ScannerSimple(String source) {
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
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '+':
                activeOp = PLUS;
                operatorStack.push(activeOp);
                addToken(activeOp);
                break;
            case '-':
                activeOp = MINUS;
                operatorStack.push(activeOp);
                addToken(activeOp);
                break;
            case '*':
                activeOp = STAR;
                operatorStack.push(activeOp);
                addToken(activeOp);
                break;
            case '/':
                activeOp = SLASH;
                operatorStack.push(activeOp);
                addToken(activeOp);
                break;
            case ' ':
            case '\r':
            case '\t':
                break; // ignore whitespace
            case '\n':
                line++;
                break;
            default: // multi character string like "123"
                if (isDigit(c)) {
                    number();
                    double value = (Double) tokens.get(tokens.size() - 1).literal;
                    if (operatorStack.peek() == STAR) {
                        operatorStack.pop();
                        value *= numberStack.pop(); // Since multiplication has higher precedence than +-, apply it to the two numbers and remove multiplication from the operatorStack
                    } else if (operatorStack.peek() == SLASH) {
                        operatorStack.pop();
                        value = numberStack.pop() / value; // Since division has higher precedence than +-, apply it to the two numbers and remove divisiob from the operatorStack
                    }
                    numberStack.push(value);

                } else {
                    Lox.error(line, "Unexpected character: '" + c + "'");
                }
                break;
        }
    }

    public double calculateResult() {
        // Numbers and operators are pushed in left-to-right order, but Stack.pop() is LIFO. Reverse stacks so you can consume them in original operation order:
        Deque<Double> numbers = new ArrayDeque<>();
        Deque<TokenType> operators = new ArrayDeque<>();
        while (!numberStack.isEmpty()) {
            numbers.push(numberStack.pop());
        }
        while (!operatorStack.isEmpty()) {
            operators.push(operatorStack.pop());
        }
        double result = numbers.pop();
        while (!operators.isEmpty()) {
            TokenType operator = operators.pop();
            double val = numbers.pop();
            if (operator == PLUS) {
                result += val;
            } else if (operator == MINUS) {
                result -= val;
            }
        }
        return result;
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
