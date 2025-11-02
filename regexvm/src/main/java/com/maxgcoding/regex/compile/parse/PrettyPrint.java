package com.maxgcoding.regex.compile.parse;

public class PrettyPrint {

    private PrettyPrint() {}

    private static void traverse(AST node, int depth) {
        if (node != null) {
            indent(depth);
            System.out.println(node.getData());
            traverse(node.getLeft(), depth+1);
            traverse(node.getRight(), depth+1);
        }
    }

    private static void indent(int depth) {
        for (int i = 0; i < depth; i++) System.out.print(" ");
    }

    public static void traverse(AST node) {
        traverse(node, 1);
    }
}