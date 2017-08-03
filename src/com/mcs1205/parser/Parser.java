package com.mcs1205.parser;

import com.mcs1205.parser.ast.ASTNode;
import com.mcs1205.parser.ast.ASTNodeType;
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
    

    public ASTNode parse(List<Lexeme> lexemes) {
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
    private ASTNode matchTerminal(SymbolType target) throws Exception{
        if (nextLexeme.getSymbolType() != target) {
            throw new Exception("Illegal: " + nextLexeme.getToken());
        }

        ASTNode terminalNode = new ASTNode(ASTNodeType.TERMINAL, nextLexeme);

        lookAhead();

        return terminalNode;
    }

    private ASTNode matchProgram() throws Exception {
        matchTerminal(SymbolType.START);
        matchTerminal(SymbolType.CURLY_BRACE_OPEN);
        ASTNode stmtListNode = matchStatementList();
        matchTerminal(SymbolType.CURLY_BRACE_CLOSE);

        return new ASTNode(ASTNodeType.PROGRAM, null, stmtListNode);
    }

    private ASTNode matchStatementList() throws Exception{
        ASTNode stmtNode = matchStatement();
        if (nextLexeme.getSymbolType() != SymbolType.CURLY_BRACE_CLOSE) {
            ASTNode stmtListNode = matchStatementList();
            return new ASTNode(ASTNodeType.STATEMENT_LIST, null, stmtNode, stmtListNode);
        }

        return new ASTNode(ASTNodeType.STATEMENT_LIST, null, stmtNode);
    }

    private ASTNode matchStatement() throws Exception {
        ASTNode node;
        if (nextLexeme.getSymbolType() == SymbolType.FOR) {
            node = matchForLoop();
        }
        else {
            node = matchTerminatedStatement();
        }

        return new ASTNode(ASTNodeType.STATEMENT, null, node);
    }

    private ASTNode matchForLoop() throws Exception{
        matchTerminal(SymbolType.FOR);
        matchTerminal(SymbolType.BRACKET_OPEN);
        ASTNode exprNode1;
        if (nextLexeme.getSymbolType() == SymbolType.INT || nextLexeme.getSymbolType() == SymbolType.FLOAT) {
            exprNode1 = matchDeclaration();
        }
        else {
            ASTNode varNode = matchVariable();
            ASTNode asgnExprNode = matchAssignmentExpression();
            exprNode1 = new ASTNode(ASTNodeType.EXPRESSION, null, varNode, asgnExprNode);
        }
        matchTerminal(SymbolType.SEMICOLON);
        ASTNode exprNode2 = matchBooleanExpression();
        matchTerminal(SymbolType.SEMICOLON);
        ASTNode exprNode3 = matchExpression();
        matchTerminal(SymbolType.BRACKET_CLOSE);
        matchTerminal(SymbolType.CURLY_BRACE_OPEN);
        ASTNode stmtListNode = matchStatementList();
        matchTerminal(SymbolType.CURLY_BRACE_CLOSE);

        return new ASTNode(ASTNodeType.FOR_LOOP, null, exprNode1, exprNode2, exprNode3, stmtListNode);
    }

    private ASTNode matchTerminatedStatement() throws Exception {
        ASTNode node;
        if (nextLexeme.getSymbolType() == SymbolType.INT || nextLexeme.getSymbolType() == SymbolType.FLOAT) {
            node = matchDeclaration();
        }
        else {
            node = matchExpression();
        }

        matchTerminal(SymbolType.SEMICOLON);

        return new ASTNode(ASTNodeType.TERMINATED_STATEMENT, null, node);
    }

    private ASTNode matchExpression() throws Exception {
        ASTNode varNode = matchVariable();
        if (nextLexeme.getSymbolType() == SymbolType.INCREMENT) {
            ASTNode incrNode = matchIncrementExpression();
            return new ASTNode(ASTNodeType.EXPRESSION, null, varNode, incrNode);
        }
        else {
            ASTNode asgnExprNode = matchAssignmentExpression();
            return new ASTNode(ASTNodeType.EXPRESSION, null, varNode, asgnExprNode);
        }
    }

    private ASTNode matchVariable() throws Exception {
        return matchTerminal(SymbolType.VARIABLE);
    }

    private ASTNode matchBooleanExpression() throws Exception {
        ASTNode termNode1 = matchTerm();
        ASTNode lessThanNode = matchTerminal(SymbolType.LESS_THAN);
        ASTNode termNode2 = matchTerm();

        return new ASTNode(ASTNodeType.BOOLEAN_EXPRESSION, null, termNode1, lessThanNode, termNode2);
    }

    private ASTNode matchDeclaration() throws Exception {
        ASTNode dtypeNode = matchDataType();
        ASTNode varNode = matchVariable();
        if (nextLexeme.getSymbolType() != SymbolType.SEMICOLON) {
            ASTNode asgnExprNode = matchAssignmentExpression();
            return new ASTNode(ASTNodeType.DECLARATION, null, dtypeNode, varNode, asgnExprNode);
        }

        return new ASTNode(ASTNodeType.DECLARATION, null, dtypeNode, varNode);
    }

    private ASTNode matchDataType() throws Exception {
        if (nextLexeme.getSymbolType() == SymbolType.INT) {
            return matchTerminal(SymbolType.INT);
        }
        else {
            return matchTerminal(SymbolType.FLOAT);
        }
    }

    private ASTNode matchAssignmentExpression() throws Exception {
        ASTNode asgnNode = matchTerminal(SymbolType.ASSIGNMENT);
        ASTNode algebraicExprNode = matchAlgebraicExpression();

        return new ASTNode(ASTNodeType.ASSIGNMENT_EXPRESSION, null, asgnNode, algebraicExprNode);
    }

    private ASTNode matchIncrementExpression() throws Exception {
        return matchTerminal(SymbolType.INCREMENT);
    }

    private ASTNode matchAlgebraicExpression() throws Exception {
        ASTNode termNode = matchTerm();
        if (nextLexeme.getSymbolType() == SymbolType.PLUS) {
            ASTNode arithmeticExprNode = matchArithmeticExpression();
            return new ASTNode(ASTNodeType.ALGEBRAIC_EXPRESSION, null, termNode, arithmeticExprNode);
        }
        else if (nextLexeme.getSymbolType() == SymbolType.LESS_THAN) {
            ASTNode booleanExprNode = matchBooleanExpression();
            return new ASTNode(ASTNodeType.ALGEBRAIC_EXPRESSION, null, termNode, booleanExprNode);
        }

        return termNode;
    }

    private ASTNode matchTerm() throws Exception {
        if (nextLexeme.getSymbolType() == SymbolType.VARIABLE) {
            return matchVariable();
        }
        else {
            return matchNumber();
        }
    }

    private ASTNode matchArithmeticExpression() throws Exception {
        ASTNode opNode = matchTerminal(SymbolType.PLUS);
        ASTNode termNode = matchTerm();
        if (nextLexeme.getSymbolType() == SymbolType.PLUS) {
            ASTNode arithmeticExprNode = matchArithmeticExpression();
            return new ASTNode(ASTNodeType.ARITHMETIC_EXPRESSION, null, opNode, termNode, arithmeticExprNode);
        }

        return new ASTNode(ASTNodeType.ARITHMETIC_EXPRESSION, null, opNode, termNode);
    }

    private ASTNode matchNumber() throws Exception {
        if (nextLexeme.getSymbolType() == SymbolType.INT_VALUE) {
            return matchTerminal(SymbolType.INT_VALUE);
        }
        else if (nextLexeme.getSymbolType() == SymbolType.FLOAT_VALUE) {
            return matchTerminal(SymbolType.FLOAT_VALUE);
        }

        return null;
    }
}
