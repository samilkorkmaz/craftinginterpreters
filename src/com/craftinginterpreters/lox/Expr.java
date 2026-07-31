package com.craftinginterpreters.lox;

abstract class Expr {

    static class Binary extends Expr {
        // The static keyword makes Expr act purely as a namespace without implicit outer instance reference. We use this patterns because Java lacks namespaces.
        // In Java, marking nested classes as static is the standard design pattern to achieve clean namespacing with zero memory overhead.
        // If you don't nest them, your package directory gets cluttered with dozens of tiny top-level .java files.
        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }

    // Other expressions...
}
