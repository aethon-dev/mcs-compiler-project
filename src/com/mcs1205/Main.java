package com.mcs1205;

import com.mcs1205.parser.parsetree.PTNode;
import com.mcs1205.semantic.SemanticAnalyzer;
import com.mcs1205.semantic.ast.ASTNode;
import com.mcs1205.semantic.warning.Warning;
import com.mcs1205.syntax.Grammar;
import com.mcs1205.lexer.Lexer;
import com.mcs1205.parser.Parser;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        Grammar grammar = new Grammar();
        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();

        String program =    "   Start {  "  +
                            "     int a = 5; "  +
                            "     float c = 1 + 10;  "  +
                            "     for(int b = 0; b < 5; b++)  "  +
                            "     {  "  +
                            "       a = b + 10;  "  +
                            "     }  "  +
                            "   }  ";

        try {
            lexer.analyze(grammar, program);
            System.out.println("List of tokens");
            System.out.println("------------------");
            lexer.print();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        PTNode parseTree = parser.parse(lexer.getLexemes());
        if (parseTree != null) {
            System.out.println("\n");
            System.out.println("Parse Tree");
            System.out.println("------------------");
            parseTree.print();
        }

        try {
            ASTNode node = semanticAnalyzer.analyze(parseTree);
            if (node != null) {
                System.out.println("\n");
                System.out.println("Abstract Syntax Tree");
                System.out.println("------------------");
                node.print();

                List<Warning> warnings = semanticAnalyzer.getWarnings();
                if (warnings.size() > 0) {
                    System.out.println("\n");
                    System.out.println("Warnings");
                    System.out.println("------------------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
