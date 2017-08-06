package com.mcs1205.parser;

import com.mcs1205.parser.parsetree.PTNode;
import com.mcs1205.parser.parsetree.PTNodeType;
import com.mcs1205.syntax.SymbolType;
import com.mcs1205.lexer.Lexeme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kasunp on 7/25/17.
 */
public class Parser {

    private Lexeme nextLexeme;
    private ArrayList<Lexeme> lexemes;
    private int nextIndex;
    

    public PTNode parse(List<Lexeme> lexemes) {
        this.lexemes = new ArrayList<>(lexemes);
        nextIndex = 0;

        lookAhead();

        try {
            return matchProgram();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void lookAhead() {
        if (nextIndex < lexemes.size()) {
            nextLexeme = lexemes.get(nextIndex);
            nextIndex++;
        }
    }


    //===================================================================
    //                          Production Rules
    //===================================================================
    private PTNode matchTerminal(SymbolType target) throws Exception{
        if (nextLexeme.getSymbolType() != target) {
            throw new Exception("Illegal: " + nextLexeme.getToken());
        }

        PTNode terminalNode = new PTNode(PTNodeType.TERMINAL, nextLexeme);
        lookAhead();

        return terminalNode;
    }

    private PTNode matchProgram() throws Exception {
        matchTerminal(SymbolType.START);
        matchTerminal(SymbolType.CURLY_BRACE_OPEN);
        PTNode stmtListNode = matchStatementList();
        matchTerminal(SymbolType.CURLY_BRACE_CLOSE);

        return new PTNode(PTNodeType.PROGRAM, null, stmtListNode);
    }

    private PTNode matchStatementList() throws Exception{
        PTNode stmtNode = matchStatement();
        if (nextLexeme.getSymbolType() != SymbolType.CURLY_BRACE_CLOSE) {
            PTNode stmtListNode = matchStatementList();
            return new PTNode(PTNodeType.STATEMENT_LIST, null, stmtNode, stmtListNode);
        }

        return new PTNode(PTNodeType.STATEMENT_LIST, null, stmtNode);
    }

    private PTNode matchStatement() throws Exception {
        PTNode node;
        if (nextLexeme.getSymbolType() == SymbolType.FOR) {
            node = matchForLoop();
        }
        else {
            node = matchTerminatedStatement();
        }

        return new PTNode(PTNodeType.STATEMENT, null, node);
    }

    private PTNode matchForLoop() throws Exception{
        matchTerminal(SymbolType.FOR);
        matchTerminal(SymbolType.BRACKET_OPEN);
        PTNode exprNode1;
        if (nextLexeme.getSymbolType() == SymbolType.INT || nextLexeme.getSymbolType() == SymbolType.FLOAT) {
            exprNode1 = matchDeclaration();
        }
        else {
            PTNode varNode = matchVariable();
            PTNode asgnExprNode = matchAssignmentExpression();
            exprNode1 = new PTNode(PTNodeType.EXPRESSION, null, varNode, asgnExprNode);
        }
        matchTerminal(SymbolType.SEMICOLON);
        PTNode exprNode2 = matchBooleanExpression();
        matchTerminal(SymbolType.SEMICOLON);
        PTNode exprNode3 = matchExpression();
        matchTerminal(SymbolType.BRACKET_CLOSE);
        matchTerminal(SymbolType.CURLY_BRACE_OPEN);
        PTNode stmtListNode = matchStatementList();
        matchTerminal(SymbolType.CURLY_BRACE_CLOSE);

        return new PTNode(PTNodeType.FOR_LOOP, null, exprNode1, exprNode2, exprNode3, stmtListNode);
    }

    private PTNode matchTerminatedStatement() throws Exception {
        PTNode node;
        if (nextLexeme.getSymbolType() == SymbolType.INT || nextLexeme.getSymbolType() == SymbolType.FLOAT) {
            node = matchDeclaration();
        }
        else {
            node = matchExpression();
        }

        matchTerminal(SymbolType.SEMICOLON);

        return new PTNode(PTNodeType.TERMINATED_STATEMENT, null, node);
    }

    private PTNode matchExpression() throws Exception {
        PTNode varNode = matchVariable();
        if (nextLexeme.getSymbolType() == SymbolType.INCREMENT) {
            PTNode incrNode = matchIncrementExpression();
            return new PTNode(PTNodeType.EXPRESSION, null, varNode, incrNode);
        }
        else {
            PTNode asgnExprNode = matchAssignmentExpression();
            return new PTNode(PTNodeType.EXPRESSION, null, varNode, asgnExprNode);
        }
    }

    private PTNode matchVariable() throws Exception {
        return matchTerminal(SymbolType.VARIABLE);
    }

    private PTNode matchBooleanExpression() throws Exception {
        PTNode termNode1 = matchTerm();
        PTNode lessThanNode = matchTerminal(SymbolType.LESS_THAN);
        PTNode termNode2 = matchTerm();

        return new PTNode(PTNodeType.BOOLEAN_EXPRESSION, null, termNode1, lessThanNode, termNode2);
    }

    private PTNode matchDeclaration() throws Exception {
        PTNode dtypeNode = matchDataType();
        PTNode varNode = matchVariable();
        if (nextLexeme.getSymbolType() != SymbolType.SEMICOLON) {
            PTNode asgnExprNode = matchAssignmentExpression();
            return new PTNode(PTNodeType.DECLARATION, null, dtypeNode, varNode, asgnExprNode);
        }

        return new PTNode(PTNodeType.DECLARATION, null, dtypeNode, varNode);
    }

    private PTNode matchDataType() throws Exception {
        if (nextLexeme.getSymbolType() == SymbolType.INT) {
            return matchTerminal(SymbolType.INT);
        }
        else {
            return matchTerminal(SymbolType.FLOAT);
        }
    }

    private PTNode matchAssignmentExpression() throws Exception {
        PTNode asgnNode = matchTerminal(SymbolType.ASSIGNMENT);
        PTNode algebraicExprNode = matchAlgebraicExpression();

        return new PTNode(PTNodeType.ASSIGNMENT_EXPRESSION, null, asgnNode, algebraicExprNode);
    }

    private PTNode matchIncrementExpression() throws Exception {
        return matchTerminal(SymbolType.INCREMENT);
    }

    private PTNode matchAlgebraicExpression() throws Exception {
        PTNode termNode = matchTerm();
        if (nextLexeme.getSymbolType() == SymbolType.PLUS) {
            PTNode arithmeticExprNode = matchArithmeticExpression();
            return new PTNode(PTNodeType.ALGEBRAIC_EXPRESSION, null, termNode, arithmeticExprNode);
        }
        else if (nextLexeme.getSymbolType() == SymbolType.LESS_THAN) {
            PTNode booleanExprNode = matchBooleanExpression();
            return new PTNode(PTNodeType.ALGEBRAIC_EXPRESSION, null, termNode, booleanExprNode);
        }

        return termNode;
    }

    private PTNode matchTerm() throws Exception {
        if (nextLexeme.getSymbolType() == SymbolType.VARIABLE) {
            return matchVariable();
        }
        else {
            return matchNumber();
        }
    }

    private PTNode matchArithmeticExpression() throws Exception {
        PTNode opNode = matchTerminal(SymbolType.PLUS);
        PTNode termNode = matchTerm();
        if (nextLexeme.getSymbolType() == SymbolType.PLUS) {
            PTNode arithmeticExprNode = matchArithmeticExpression();
            return new PTNode(PTNodeType.ARITHMETIC_EXPRESSION, null, opNode, termNode, arithmeticExprNode);
        }

        return new PTNode(PTNodeType.ARITHMETIC_EXPRESSION, null, opNode, termNode);
    }

    private PTNode matchNumber() throws Exception {
        if (nextLexeme.getSymbolType() == SymbolType.INT_VALUE) {
            return matchTerminal(SymbolType.INT_VALUE);
        }
        else if (nextLexeme.getSymbolType() == SymbolType.FLOAT_VALUE) {
            return matchTerminal(SymbolType.FLOAT_VALUE);
        }

        return null;
    }
}
