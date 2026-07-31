package com.craftinginterpreters.lox;

import java.util.List;

class Parser {

    // Grammar, lowest precedence first. Each rule calls the one below it,
    // so the ORDER OF THESE METHODS IS THE PRECEDENCE TABLE — moving one changes the language.
    //
    //   expression → term
    //   term       → factor ( ( "+" | "-" ) factor )*     left-assoc  (loop)
    //   factor     → unary  ( ( "*" | "/" ) unary  )*     left-assoc  (loop)
    //   unary      → "-" unary | power                    right-assoc (recursion)
    //   power      → primary ( "^" unary )*               right-assoc (recurses into unary)
    //   primary    → NUMBER | "(" expression ")"
    //
    // Associativity comes from the shape of the code, not from any flag:
    //   while (match(op)) { ... }  folds left-to-right → (a-b)-c
    //   return op(recurse());      builds right-to-left → a^(b^c)
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    double parse() {
        return expression();
    }

    // A full expression: the bottom of the precedence ladder. Both parse() and
    // parenthesised groups restart here.
    private double expression() {
        return term();
    }

    private double term() {
        double left = factor();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            TokenType op = previous().type;
            double right = factor();
            switch (op) {
                case PLUS:
                    left += right;
                    break;
                case MINUS:
                    left -= right;
                    break;
                default:
                    throw new IllegalStateException("Unhandled operator " + op);
            }
        }
        return left;
    }

    private double factor() {
        double left = unary();
        while (match(TokenType.STAR, TokenType.SLASH)) {
            TokenType op = previous().type;
            double right = unary();
            switch (op) {
                case STAR:
                    left *= right;
                    break;
                case SLASH:
                    left /= right;
                    break;
                default:
                    throw new IllegalStateException("Unhandled operator " + op);
            }
        }
        return left;
    }

    private double unary() { // Unary expressions have a single operand, e.g. !a, -5
        if (match(TokenType.MINUS)) {
            return -unary(); // a succesful MINUS match increments the current index (goes to next token), so that the recursive call to this function will not have that minus sign
        }
        return power();
    }

    private double power() {
        double value = primary();    
        if (match(TokenType.CARET)) {
            double exponent = unary(); // unary, NOT power: gives 2^3^2 = 2^(3^2) = 512 (not 64), and lets the exponent be negative, as in 2^-2 = 0.25
            // Example 2 ^ 3 ^ 2 steps: 
            // power#1: value = 2, sees ^
            //          └── unary() → power#2: value = 3, sees ^
            //                  └── unary() → power#3: value = 2, no ^  → returns 2
            //                  value = 3^2 = 9                           → returns 9   ← computed FIRST
            // exponent = 9
            // value = 2^9 = 512                                                 ← computed SECOND
            // After unary() returns, every consecutive ^ has already been consumed by the recursion
            value = Math.pow(value, exponent);
        }

        return value;
    }

    private double primary() {
        if (match(TokenType.NUMBER)) {
            // Scanner stored a Double in `literal` for every NUMBER token, so this cast can't fail.
            return (double) previous().literal;
        }
        if (match(TokenType.LEFT_PAREN)) {
            double value = expression(); // process the expression inside the parens
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression."); // the token after the expression must be right paren
            return value;
        }
        throw error(peek(), "Expect expression.");
    }

    // --- token inspection (no side effects) ---
    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean check(TokenType type) {
        return !isAtEnd() && peek().type == type;
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    // --- token consumption (advances current) ---
    // Consumes and returns true on the first matching type; leaves the position alone on failure.
    // NOTE: on success `current` has already moved past the operator — read it back with previous().
    private boolean match(TokenType... types) {
        for (TokenType t : types) {
            if (check(t)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw error(peek(), message);
    }

    // --- error reporting ---
    private RuntimeException error(Token token, String message) {
        Lox.error(token.line, message);
        return new RuntimeException(message);
    }
}
