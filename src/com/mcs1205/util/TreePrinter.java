package com.mcs1205.util;

import java.util.List;

/**
 * Created by kasunp on 8/6/17.
 */
public class TreePrinter<T extends Printable> {

    private T root;
    private List<T> children;

    public TreePrinter(T root, List<T> children) {
        this.root = root;
        this.children = children;
    }

    public void print(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + root.toString());
        for (int i = 0; i < children.size() - 1; i++) {
            children.get(i).print(prefix + (isTail ? "    " : "│   "), false);
        }
        if (children.size() > 0) {
            children.get(children.size() - 1)
                    .print(prefix + (isTail ?"    " : "│   "), true);
        }
    }
}
