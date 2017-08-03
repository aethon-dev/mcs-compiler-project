package com.mcs1205.lexer;

import com.mcs1205.syntax.SymbolType;

/**
 * Created by kasunp on 7/23/17.
 */
public class Lexeme {

    private final SymbolType symbolType;
    private final String token;

    public Lexeme(SymbolType symbolType, String token) {
        this.symbolType = symbolType;
        this.token = token;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    public String getToken() {
        return token;
    }
}
