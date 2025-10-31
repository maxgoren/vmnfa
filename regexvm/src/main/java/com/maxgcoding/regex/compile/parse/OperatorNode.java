package com.maxgcoding.regex.compile.parse;

public class OperatorNode extends Node {
 private Node left;
    private Node right;
    private Character data;
    @Override
    public Node getLeft() {
        return left;
    }

    @Override
    public Node getRight() {
        return right;
    }
    @Override
    public Node setLeft(Node l) {
        left = l;
        return this;
    }
    @Override
    public Node setRight(Node r) {
        right = r;
        return this;
    }
    @Override
    public Character getData() {
        return data;
    }
    @Override
    public Node setData(Character ch) {
        this.data = ch;
        return this;
    }
}
