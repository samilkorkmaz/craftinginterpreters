package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

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

    ScannerSimple(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
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
                addToken(PLUS);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '*':
                addToken(STAR);
                break;
            case '/':
                addToken(SLASH);
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
                } else {
                    Lox.error(line, "Unexpected character: '" + c + "'");
                }
                break;
        }
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
