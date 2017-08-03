package com.mcs1205.parser.ast;

import com.mcs1205.lexer.Lexeme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kasunp on 8/1/17.
 */
public class ASTNode {

    private ASTNodeType astNodeType;
    private Lexeme data;
    private List<ASTNode> children;

    public ASTNode(ASTNodeType nodeType, Lexeme data, ASTNode... children) {
        this.astNodeType = nodeType;
        this.data = data;
        this.children = new ArrayList<>();
        for (ASTNode node : children) {
            this.children.add(node);
        }
    }

    public ASTNodeType getAstNodeType() {
        return astNodeType;
    }

    public Object getData() { return data; }

    public List<ASTNode> getChildren() { return children; }

    @Override
    public String toString() {
        return (data != null ? (data.getToken()) : "<" + this.astNodeType.toString()+ ">");
    }

    public void print() {
        print("", true);
    }

    private void print(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + this.toString());
        for (int i = 0; i < children.size() - 1; i++) {
            children.get(i).print(prefix + (isTail ? "    " : "│   "), false);
        }
        if (children.size() > 0) {
            children.get(children.size() - 1)
                    .print(prefix + (isTail ?"    " : "│   "), true);
        }
    }
}
