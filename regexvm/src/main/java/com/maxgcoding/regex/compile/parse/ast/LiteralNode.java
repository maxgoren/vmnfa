package com.maxgcoding.regex.compile.parse.ast;

import com.maxgcoding.regex.compile.parse.AST;

public class LiteralNode extends AST {

    private Character data;

    @Override
    public AST getLeft() {
        return null;
    }

    @Override
    public AST getRight() {
        return null;
    }

    @Override
    public AST setLeft(AST l) {
        return this;
    }

    @Override
    public AST setRight(AST r) {
        return this;
    }

    @Override
    public Character getData() {
        return data;
    }

    @Override
    public AST setData(Character ch) {
        this.data = ch;
        return this;
    }
}
