package com.kasunperera.grammar;

import java.util.regex.Pattern;

/**
 * Created by kasunp on 7/23/17.
 */
public class Symbol {

    private final Pattern regexPattern;
    private final SymbolType type;

    public Symbol(Pattern regexPattern, SymbolType type) {
        this.regexPattern = regexPattern;
        this.type = type;
    }

    public Pattern getRegexPattern() {
        return regexPattern;
    }

    public SymbolType getType() {
        return type;
    }
}
