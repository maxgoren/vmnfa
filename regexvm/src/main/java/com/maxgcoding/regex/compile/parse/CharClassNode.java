package com.maxgcoding.regex.compile.parse;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@NoArgsConstructor
public class CharClassNode extends Node {
    private Node left;
    private Node right;
    private String ccl;
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
        return '[';
    }

    public Node setCcl(String data) {
        ccl = data;
        return this;
    }
    public String getCcl() {
        return ccl;
    }

    @Override
    public Node setData(Character ch) {
        return this;
    }

}
