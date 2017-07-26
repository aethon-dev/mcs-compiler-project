package com.kasunperera.lexer;

import com.kasunperera.grammar.Symbol;
import com.kasunperera.grammar.SymbolType;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kasunp on 7/23/17.
 */
public class Lexer {

    private ArrayList<Lexeme> lexemes;

    public Lexer() {
        this.lexemes = new ArrayList<>();
    }

    public void analyze(ArrayList<Symbol> symbols, String str) throws Exception {
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

            if (!match) throw new Exception("Unexpected character in input: " + source);
        }
    }

    public ArrayList<Lexeme> getLexemes() {
        return lexemes;
    }
}
