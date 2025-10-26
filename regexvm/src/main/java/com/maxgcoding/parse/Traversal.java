package com.maxgcoding.parse;

public class Traversal {
    private static void traverse(Node node, int depth) {
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
    public static void traverse(Node node) {
        traverse(node, 1);
    }
}
