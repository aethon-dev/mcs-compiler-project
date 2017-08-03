package com.kasunperera;

import com.kasunperera.parser.ast.ASTNode;
import com.kasunperera.syntax.Grammar;
import com.kasunperera.lexer.Lexeme;
import com.kasunperera.lexer.Lexer;
import com.kasunperera.parser.Parser;

public class Main {

    public static void main(String[] args) {

        Grammar grammar = new Grammar();
        Lexer lexer = new Lexer();
        Parser parser = new Parser();

        String program =    "   Start {  "  +
                            "     int a = 10.2;  "  +
                            "     float c = 1 + 10;  "  +
                            "     for(int b = 0; b < 5; b++)  "  +
                            "     {  "  +
                            "       a = a + c;  "  +
                            "     }  "  +
                            "   }  ";

        try {
            lexer.analyze(grammar.getSymbols(), program);
            System.out.println("List of tokens");
            System.out.println("------------------");
            lexer.print();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        ASTNode astRootNode = parser.parse(lexer.getLexemes());
        if (astRootNode != null) {
            System.out.println("\n");
            System.out.println("Parse Tree");
            System.out.println("------------------");
            astRootNode.print();
        }
    }
}
