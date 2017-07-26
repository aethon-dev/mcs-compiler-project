package com.kasunperera;

import com.kasunperera.grammar.Grammar;
import com.kasunperera.grammar.SymbolType;
import com.kasunperera.lexer.Lexeme;
import com.kasunperera.lexer.Lexer;
import com.kasunperera.parser.Parser;

public class Main {

    public static void main(String[] args) {

        Grammar grammar = new Grammar();
        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        String program =    "   Start{  "  +
                            "   int a = 10;  "  +
                            "   float c = 1 + 10 + 20;  "  +
                            "   for(int b = 0; b < 5; b++)  "  +
                            "   {  "  +
                            "   a = a + c;  "  +
                            "   }  "  +
                            "  }  ";

        try {
            lexer.analyze(grammar.getSymbols(), program);

            for (Lexeme lexeme : lexer.getLexemes()) {
                System.out.println(lexeme.getToken() + " : " + lexeme.getSymbolType().toString());
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        parser.parse(lexer.getLexemes());
    }
}
