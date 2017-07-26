package com.kasunperera.parser;

import com.kasunperera.grammar.SymbolType;
import com.kasunperera.lexer.Lexeme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kasunp on 7/25/17.
 */
public class Parser {

    private Lexeme nextLexeme;
    private ArrayList<Lexeme> lexemes;
    private int nextIndex;
    

    public void parse(List<Lexeme> lexemes) {
        this.lexemes = new ArrayList<>(lexemes);
        nextIndex = 0;

        lookAhead();

        try {
            matchProgram();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void lookAhead() {
        if (nextIndex < lexemes.size()) {
            nextLexeme = lexemes.get(nextIndex);
            nextIndex++;
        }
    }

    private void matchTerminal(SymbolType target) throws Exception{
        if (nextLexeme.getSymbolType() != target) {
            throw new Exception("Illegal: " + nextLexeme.getToken());
        }

        lookAhead();
    }

    private void matchProgram() throws Exception {
        matchTerminal(SymbolType.START);
        matchTerminal(SymbolType.CURLY_BRACE_OPEN);
        matchStatementList();
        matchTerminal(SymbolType.CURLY_BRACE_CLOSE);
    }

    private void matchStatementList() throws Exception{
        matchStatement();
        if (nextLexeme.getSymbolType() != SymbolType.CURLY_BRACE_CLOSE) {
            matchStatementList();
        }
    }

    private void matchStatement() throws Exception{
        if (nextLexeme.getSymbolType() == SymbolType.FOR) {
            matchForLoop();
        }
        else {
            matchTerminatedStatement();
        }
    }

    private void matchForLoop() throws Exception{
        matchTerminal(SymbolType.FOR);
        matchTerminal(SymbolType.BRACKET_OPEN);
        if (nextLexeme.getSymbolType() == SymbolType.INT || nextLexeme.getSymbolType() == SymbolType.FLOAT) {
            matchDeclaration();
        }
        else {
            matchVariable();
            matchAssignmentExpression();
        }
        matchTerminal(SymbolType.SEMICOLON);
        matchBooleanExpression();
        matchTerminal(SymbolType.SEMICOLON);
        matchExpression();
        matchTerminal(SymbolType.BRACKET_CLOSE);
        matchTerminal(SymbolType.CURLY_BRACE_OPEN);
        matchStatementList();
        matchTerminal(SymbolType.CURLY_BRACE_CLOSE);
    }

    private void matchTerminatedStatement() throws Exception {
        if (nextLexeme.getSymbolType() == SymbolType.INT || nextLexeme.getSymbolType() == SymbolType.FLOAT) {
            matchDeclaration();
        }
        else {
            matchExpression();
        }

        matchTerminal(SymbolType.SEMICOLON);
    }

    private void matchExpression() throws Exception {
        matchVariable();
        if (nextLexeme.getSymbolType() == SymbolType.INCREMENT) {
            matchIncrementExpression();
        }
        else {
            matchAssignmentExpression();
        }
    }

    private void matchVariable() throws Exception {
        matchTerminal(SymbolType.VARIABLE);
    }

    private void matchBooleanExpression() throws Exception {
        matchTerm();
        matchTerminal(SymbolType.LESS_THAN);
        matchTerm();
    }

    private void matchDeclaration() throws Exception {
        matchDataType();
        matchVariable();
        if (nextLexeme.getSymbolType() != SymbolType.SEMICOLON) {
            matchAssignmentExpression();
        }
    }

    private void matchDataType() throws Exception {
        if (nextLexeme.getSymbolType() == SymbolType.INT) {
            matchTerminal(SymbolType.INT);
        }
        else {
            matchTerminal(SymbolType.FLOAT);
        }
    }

    private void matchAssignmentExpression() throws Exception {
        matchTerminal(SymbolType.ASSIGNMENT);
        matchAlgebraicExpression();
    }

    private void matchIncrementExpression() throws Exception {
        matchTerminal(SymbolType.INCREMENT);
    }

    private void matchAlgebraicExpression() throws Exception {
        matchTerm();
        if (nextLexeme.getSymbolType() == SymbolType.PLUS) {
            matchArithmeticExpression();
        }
        else if (nextLexeme.getSymbolType() == SymbolType.LESS_THAN) {
            matchBooleanExpression();
        }
    }

    private void matchTerm() throws Exception {
        if (nextLexeme.getSymbolType() == SymbolType.VARIABLE) {
            matchVariable();
        }
        else {
            matchTerminal(SymbolType.NUMBER);
        }
    }

    private void matchArithmeticExpression() throws Exception {
        matchTerminal(SymbolType.PLUS);
        matchTerm();
        if (nextLexeme.getSymbolType() == SymbolType.PLUS) {
            matchArithmeticExpression();
        }
    }
}
