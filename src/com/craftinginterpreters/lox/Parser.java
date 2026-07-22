package com.craftinginterpreters.lox;

import java.util.List;

class Parser {
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    double parse() {
        return expression();
    }

    private double expression() {
        return term();
    }

    private double term() {
        double value = factor();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            TokenType op = previous().type;
            double right = factor();
            value = (op == TokenType.PLUS) ? value + right : value - right;
        }
        return value;
    }

    private double factor() {
        double value = unary();
        while (match(TokenType.STAR, TokenType.SLASH)) {
            TokenType op = previous().type;
            double right = unary();
            value = (op == TokenType.STAR) ? value * right : value / right;
        }
        return value;
    }

    private double unary() {
        if (match(TokenType.MINUS)) {
            return -unary();
        }
        return primary();
    }

    private double primary() {
        if (match(TokenType.NUMBER)) {
            return (double) previous().literal;
        }
        if (match(TokenType.LEFT_PAREN)) {
            double value = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return value;
        }
        throw error(peek(), "Expect expression.");
    }

    // --- helpers ---
    private boolean match(TokenType... types) {
        for (TokenType t : types) {
            if (check(t)) { advance(); return true; }
        }
        return false;
    }

    private boolean check(TokenType type) {
        return !isAtEnd() && peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() { return peek().type == TokenType.EOF; }
    private Token peek() { return tokens.get(current); }
    private Token previous() { return tokens.get(current - 1); }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private RuntimeException error(Token token, String message) {
        Lox.error(token.line, message);
        return new RuntimeException(message);
    }
}