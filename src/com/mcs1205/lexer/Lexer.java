package com.mcs1205.lexer;

import com.mcs1205.lexer.exception.InvalidSyntaxException;
import com.mcs1205.syntax.Grammar;
import com.mcs1205.syntax.Symbol;
import com.mcs1205.syntax.SymbolType;

import java.util.ArrayList;
import java.util.regex.Matcher;

/**
 * Created by kasunp on 7/23/17.
 */
public class Lexer {

    private ArrayList<Lexeme> lexemes;

    public Lexer() {
        this.lexemes = new ArrayList<>();
    }

    public void analyze(Grammar grammar, String str) throws Exception {
        ArrayList<Symbol> symbols = grammar.getSymbols();
        String source = new String(str);
        lexemes.clear();

        while (!source.equals("")) {
            boolean match = false;

            for (Symbol symbol : symbols) {
                Matcher matcher = symbol.getRegexPattern().matcher(source);
                if (matcher.find()) {
                    match = true;

                    String stringToken = matcher.group().trim();
                    if (symbol.getType() != SymbolType.WHITESPACE) {
                        lexemes.add(new Lexeme(symbol.getType(), stringToken));
                    }

                    source = matcher.replaceFirst("");
                    break;
                }
            }

            if (!match) throw new InvalidSyntaxException("Unexpected character in input: " + source);
        }
    }

    public ArrayList<Lexeme> getLexemes() {
        return lexemes;
    }

    public void print() {
        for (Lexeme lexeme : lexemes) {
            System.out.printf("%-6s : %s\n", lexeme.getToken(), lexeme.getSymbolType().toString());
        }
    }
}
