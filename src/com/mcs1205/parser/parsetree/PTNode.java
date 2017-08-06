package com.mcs1205.parser.parsetree;

import com.mcs1205.lexer.Lexeme;
import com.mcs1205.util.Printable;
import com.mcs1205.util.TreePrinter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kasunp on 8/1/17.
 */
public class PTNode implements Printable {

    private PTNodeType PTNodeType;
    private Lexeme lexeme;
    private PTNode parent;
    private List<PTNode> children;
    private TreePrinter<PTNode> treePrinter;

    public PTNode(PTNodeType nodeType, Lexeme lexeme, PTNode... children) {
        this.PTNodeType = nodeType;
        this.lexeme = lexeme;
        this.children = new ArrayList<>();
        for (PTNode node : children) {
            node.setParent(this);
            this.children.add(node);
        }

        treePrinter = new TreePrinter<>(this, this.children);
    }

    public PTNode getParent() {
        return parent;
    }

    public void setParent(PTNode parent) {
        this.parent = parent;
    }

    public PTNodeType getPTNodeType() {
        return PTNodeType;
    }

    public Lexeme getLexeme() { return lexeme; }

    public List<PTNode> getChildren() { return children; }

    @Override
    public String toString() {
        return (lexeme != null ? (lexeme.getToken()) : "<" + this.PTNodeType.toString()+ ">");
    }

    public void print() {
        print("", true);
    }

    public void print(String prefix, boolean isTail) {
        treePrinter.print(prefix, isTail);
    }
}
