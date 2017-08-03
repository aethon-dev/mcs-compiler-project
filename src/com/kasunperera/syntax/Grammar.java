package com.kasunperera.syntax;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by kasunp on 7/26/17.
 */
public class Grammar {

    private ArrayList<Symbol> symbols;

    public Grammar() {
        symbols = new ArrayList<>();
        initialize();
    }

    private void add(String regex, SymbolType symbolType) {
        symbols.add(new Symbol(Pattern.compile("^(" + regex + ")"), symbolType));
    }

    private void initialize() {
        add("Start", SymbolType.START);
        add("for", SymbolType.FOR);
        add("\\{", SymbolType.CURLY_BRACE_OPEN);
        add("\\}", SymbolType.CURLY_BRACE_CLOSE);
        add(";", SymbolType.SEMICOLON);
        add("int", SymbolType.INT);
        add("float", SymbolType.FLOAT);
        add("=", SymbolType.ASSIGNMENT);
        add("\\+\\+", SymbolType.INCREMENT);
        add("\\(", SymbolType.BRACKET_OPEN);
        add("\\)", SymbolType.BRACKET_CLOSE);
        add("\\+", SymbolType.PLUS);
        add("<", SymbolType.LESS_THAN);
        add("[0-9]*\\.[0-9]+", SymbolType.FLOAT_VALUE);
        add("[0-9]+", SymbolType.INT_VALUE);
        add("[a-zA-Z][a-zA-Z0-9_]*", SymbolType.VARIABLE);
        add("\\s", SymbolType.WHITESPACE);
    }

    public ArrayList<Symbol> getSymbols() {
        return symbols;
    }
}