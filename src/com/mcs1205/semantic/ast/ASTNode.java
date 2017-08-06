package com.mcs1205.semantic.ast;

import com.mcs1205.syntax.SymbolType;
import com.mcs1205.util.Printable;
import com.mcs1205.util.TreePrinter;

import java.util.ArrayList;

/**
 * Created by kasunp on 8/5/17.
 */
public class ASTNode implements Printable {

    private ASTNode parent;
    private ArrayList<ASTNode> children;
    private String nodeName;
    private TreePrinter<ASTNode> treePrinter;

    // Attributes
    private String dataType;
    private String name;
    private String value;

    public ASTNode() {
        children = new ArrayList<>();
        treePrinter = new TreePrinter<>(this, this.children);
    }

    public ASTNode getParent() {
        return parent;
    }

    public void setParent(ASTNode parent) {
        this.parent = parent;
    }

    public ArrayList<ASTNode> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<ASTNode> children) {
        this.children = children;
    }

    public void addChild(ASTNode child) {
        child.setParent(this);
        children.add(child);
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getIntValue() {
        // If the value is a float consider the whole part only
        if (dataType == SymbolType.FLOAT.toString()) {
            return Integer.parseInt(value.split("\\.")[0]);
        }

        return Integer.parseInt(value);
    }

    public float getFloatValue() {
        return Float.parseFloat(value);
    }

    @Override
    public String toString() {

        String str = "";
        if (nodeName != null) {
            str += nodeName + ":";
        }

        str += "[";
        if (dataType != null) {
            str += "dataType:" + dataType + ", ";
        }

        if (name != null) {
            str += "name:" + name + ", ";
        }

        if (value != null) {
            str += "value:" + value + ", ";
        }

        if (str.endsWith(", ")) {
            str = str.substring(0, str.length() - 2);
        }

        str += "]";

        return str;
    }

    public void print() {
        print("", true);
    }

    public void print(String prefix, boolean isTail) {
        treePrinter.print(prefix, isTail);
    }

}
