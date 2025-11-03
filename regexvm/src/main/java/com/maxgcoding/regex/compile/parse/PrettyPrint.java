package com.maxgcoding.regex.compile.parse;

import com.maxgcoding.regex.compile.parse.ast.*;

public class PrettyPrint {

    private PrettyPrint() {}

    private static void traverse(AST node, int depth) {
        if (node != null) {
            indent(depth);
            switch (node) {
                case LiteralNode _ -> System.out.println(node.getData());
                case CharClassNode ccl -> System.out.printf("[%s]\n", ccl.getCcl());
                case OrNode _, ConcatNode _, QuestClosureNode _, PlusClosureNode _, StarClosureNode _ -> System.out.println(node.getData());
                default -> { }
            }
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
