package com.craftinginterpreters.lox;

// Represents a single token produced by the Scanner.
class Token {
  final TokenType type;    // The category of token (e.g. NUMBER, IF, LEFT_PAREN)
  final String lexeme;     // A lexeme is the raw substring of source text that a token was scanned from (e.g. "var x = 12;", "123", "if", "(")
  final Object literal;    // The parsed value for literals: Double for numbers, String for strings, null otherwise. Used for evaluation of expressions like "12+3" = 15
  final int line;          // Source line where the token appears, used for error reporting

  Token(TokenType type, String lexeme, Object literal, int line) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
  }

  @Override
  public String toString() {
    return type + " " + lexeme + " " + literal;
  }
}