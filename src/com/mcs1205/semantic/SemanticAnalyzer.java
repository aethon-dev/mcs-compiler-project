package com.mcs1205.semantic;

import com.mcs1205.parser.parsetree.PTNode;
import com.mcs1205.parser.parsetree.PTNodeType;
import com.mcs1205.semantic.ast.ASTNode;
import com.mcs1205.semantic.exception.UndeclaredVariableException;
import com.mcs1205.semantic.exception.UninitializedVariableException;
import com.mcs1205.semantic.warning.TruncationWarning;
import com.mcs1205.semantic.warning.Warning;
import com.mcs1205.syntax.SymbolType;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by kasunp on 8/5/17.
 */
public class SemanticAnalyzer {

    private Hashtable<String, ASTNode> symbolTable;
    private List<Warning> warnings;

    public SemanticAnalyzer() {
        symbolTable = new Hashtable<>();
        warnings = new ArrayList<>();
    }

    public void addWarning(Warning warning) {
        warnings.add(warning);
    }

    public List<Warning> getWarnings() {
        return warnings;
    }

    public ASTNode analyze(PTNode ptNode) throws Exception {
        return reduceProgram(ptNode);
    }

    private ASTNode reduceProgram(PTNode ptNode) throws Exception {
        ASTNode program =  reduceStatementList(ptNode.getChildren().get(0));
        program.setNodeName(SymbolType.START.toString());

        return program;
    }

    private ASTNode reduceStatementList(PTNode ptNode) throws Exception {
        ASTNode result = new ASTNode();
        result.setNodeName(PTNodeType.STATEMENT_LIST.toString());
        ASTNode left = reduceStatement(ptNode.getChildren().get(0));
        result.addChild(left);
        if (ptNode.getChildren().size() == 2) {
            ASTNode right = reduceStatementList(ptNode.getChildren().get(1));
            result.addChild(right);
        }

        return result;
    }

    private ASTNode reduceStatement(PTNode ptNode) throws Exception {
        if (ptNode.getChildren().get(0).getPTNodeType() == PTNodeType.TERMINATED_STATEMENT) {
            return reduceTerminatedStatement(ptNode.getChildren().get(0));
        }
        else if (ptNode.getChildren().get(0).getPTNodeType() == PTNodeType.FOR_LOOP) {
            return reduceForLoop(ptNode.getChildren().get(0));
        }

        return null;
    }

    private ASTNode reduceTerminatedStatement(PTNode ptNode) throws Exception {
        if (ptNode.getChildren().get(0).getPTNodeType() == PTNodeType.DECLARATION) {
            return reduceDeclaration(ptNode.getChildren().get(0));
        }
        else if (ptNode.getChildren().get(0).getPTNodeType() == PTNodeType.EXPRESSION){
            return reduceExpression(ptNode.getChildren().get(0));
        }

        return null;
    }

    private ASTNode reduceDeclaration(PTNode ptNode) throws Exception {
        List<PTNode> children = ptNode.getChildren();

        PTNode dataTypeNode = children.get(0);
        PTNode varNode = children.get(1);

        ASTNode variable = new ASTNode();
        variable.setNodeName(varNode.getLexeme().getToken());
        variable.setDataType(dataTypeNode.getLexeme().getSymbolType().toString());
        variable.setName(varNode.getLexeme().getToken());

        if (children.size() == 3) {
            assignValueToVar(children.get(2), variable);
        }

        symbolTable.put(variable.getName(), variable);
        return variable;
    }

    private ASTNode reduceAssignment(PTNode ptNode) throws Exception {
        ASTNode value;
        if (ptNode.getChildren().get(1).getPTNodeType() == PTNodeType.ALGEBRAIC_EXPRESSION) {
            value = reduceAlgebraicExpression(ptNode.getChildren().get(1));
        }
        else {
            value = reduceValue(ptNode.getChildren().get(1));
        }

        return value;
    }

    private ASTNode reduceForLoop(PTNode ptNode) throws Exception {
        ASTNode loopVar;
        if (ptNode.getChildren().get(0).getPTNodeType() == PTNodeType.DECLARATION) {
            loopVar = reduceDeclaration(ptNode.getChildren().get(0));
        }
        else {
            loopVar = symbolTableVarLookup(ptNode.getChildren().get(0).getLexeme().getToken());
        }

        ASTNode booleanExpr = reduceBooleanExpression(ptNode.getChildren().get(1));
        ASTNode expression = reduceExpression(ptNode.getChildren().get(2));
        ASTNode stmtList = reduceStatementList(ptNode.getChildren().get(3));

        ASTNode forLoop = new ASTNode();
        forLoop.setNodeName(SymbolType.FOR.toString());
        forLoop.addChild(loopVar);
        forLoop.addChild(booleanExpr);
        forLoop.addChild(expression);
        forLoop.addChild(stmtList);

        return forLoop;
    }

    private ASTNode reduceExpression(PTNode ptNode) throws Exception {
        ASTNode variable = symbolTableVarLookup(ptNode.getChildren().get(0).getLexeme().getToken());
        if (ptNode.getChildren().get(1).getPTNodeType() == PTNodeType.ASSIGNMENT_EXPRESSION) {
            variable = assignValueToVar(ptNode.getChildren().get(1), variable);
        }
        else {
            ASTNode increment = new ASTNode();
            increment.setValue("1");
            increment.setDataType("INT");
            ASTNode value = additionWithTypePromotion(reduceValue(ptNode.getChildren().get(0)), increment);

            variable.setValue(value.getValue());
        }

        return variable;
    }

    private ASTNode reduceAlgebraicExpression(PTNode ptNode) throws Exception {
        List<PTNode> children = ptNode.getChildren();

        ASTNode left = reduceValue(children.get(0));
        ASTNode right;
        ASTNode value;
        if (children.get(1).getPTNodeType() == PTNodeType.ARITHMETIC_EXPRESSION) {
            right = reduceArithmeticExpression(children.get(1));
            value = additionWithTypePromotion(left, right);
        }
        else {
            right = reduceBooleanExpression(children.get(1));
            // TODO: remove
            value = additionWithTypePromotion(left, right);
        }

        return value;
    }

    private ASTNode reduceArithmeticExpression(PTNode ptNode) throws Exception {
        ASTNode currentVal = reduceValue(ptNode.getChildren().get(1));
        if (ptNode.getChildren().size() == 3 && ptNode.getChildren().get(2).getPTNodeType() == PTNodeType.ARITHMETIC_EXPRESSION) {
            ASTNode childVal = reduceArithmeticExpression(ptNode.getChildren().get(2));
            return additionWithTypePromotion(currentVal, childVal);
        }

        return currentVal;
    }

    private ASTNode reduceBooleanExpression(PTNode ptNode) throws Exception {
        ASTNode left = reduceValue(ptNode.getChildren().get(0));
        ASTNode right = reduceValue(ptNode.getChildren().get(2));

        ASTNode result = new ASTNode();
        result.setDataType("BOOLEAN");
        if (left.getFloatValue() < right.getFloatValue()) {
            result.setValue("true");
        }
        else {
            result.setValue("true");
        }

        return result;
    }

    private ASTNode reduceValue(PTNode ptNode) throws Exception {
        ASTNode value = new ASTNode();
        if (ptNode.getLexeme().getSymbolType() == SymbolType.VARIABLE) {
            ASTNode variable = symbolTable.get(ptNode.getLexeme().getToken());
            if (variable == null) {
                throw new UndeclaredVariableException("Undeclared variable: " + ptNode.getLexeme().getToken());
            }

            if (variable.getValue() == null) {
                throw new UninitializedVariableException("Variable '" + ptNode.getLexeme().getToken() + "' is not initialized.");
            }

            value.setNodeName(variable.getNodeName());
            value.setDataType(variable.getDataType());
            value.setValue(variable.getValue());
        }
        else {
            value.setNodeName(ptNode.getLexeme().getToken());
            value.setValue(ptNode.getLexeme().getToken());
            value.setDataType(ptNode.getLexeme().getSymbolType().toString());
        }

        return value;
    }

    private ASTNode additionWithTypePromotion(ASTNode left, ASTNode right) {
        ASTNode value = new ASTNode();
        if (left.getDataType() == SymbolType.FLOAT_VALUE.toString()
                || left.getDataType() == SymbolType.FLOAT.toString()
                || right.getDataType() == SymbolType.FLOAT_VALUE.toString()
                || right.getDataType() == SymbolType.FLOAT.toString()) {

            float leftVal = left.getFloatValue();
            float rightVal = right.getFloatValue();

            value.setValue(Float.toString(leftVal + rightVal));
            value.setDataType(SymbolType.FLOAT_VALUE.toString());
        }
        else {
            int leftVal = left.getIntValue();
            int rightVal = right.getIntValue();

            value.setValue(Integer.toString(leftVal + rightVal));
            value.setDataType(SymbolType.INT_VALUE.toString());
        }

        return value;
    }

    private ASTNode symbolTableVarLookup(String varName) throws Exception {
        ASTNode variable = symbolTable.get(varName);
        if (variable == null) {
            throw new UndeclaredVariableException("Undeclared variable: " + varName);
        }

        return variable;
    }

    private ASTNode assignValueToVar(PTNode ptNode, ASTNode variable) throws Exception {
        ASTNode value = reduceAssignment(ptNode);

        if (variable.getDataType() == SymbolType.INT.toString() && value.getDataType() != SymbolType.INT_VALUE.toString()) {
            addWarning(new TruncationWarning("Possible loss of data. Expected: " + SymbolType.INT_VALUE.toString() + ", Found: " + value.getDataType()));
        }

        variable.setValue(value.getValue());

        return variable;
    }
}
