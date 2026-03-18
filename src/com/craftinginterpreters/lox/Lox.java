package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    static boolean hadError = false;

    public static void main(String[] args) throws IOException {
        System.out.println("Arguments to main:");
        for (String arg : args) {
            System.out.println(arg);
        }
        if (args.length > 1) {
            System.out.println("args.length (" + args.length + ") > 1!");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) {
            System.exit(65);
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null || line.isEmpty()) {
                System.out.println("Exiting prompt...");
                break;
            }
            run(line);
            hadError = false;
        }
    }

    private static void run(String source) {
        //Scanner scanner = new Scanner(source);
        ScannerSimple scanner = new ScannerSimple(source);
        List<Token> tokens = scanner.scanTokens();

        // For now, just print the tokens.
        tokens.forEach((token) -> {
            System.out.println(token);
        });
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where,
            String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}
