package com.maxgcoding.regex.compile.parse;

public abstract class Node {
    abstract public Character getData();
    abstract public Node getLeft();
    abstract public Node getRight();
    abstract public Node setLeft(Node l);
    abstract public Node setRight(Node r);
    abstract public Node setData(Character ch);
}
