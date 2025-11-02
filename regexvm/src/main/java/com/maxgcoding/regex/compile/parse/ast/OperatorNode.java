package com.maxgcoding.regex.compile.parse.ast;

import com.maxgcoding.regex.compile.parse.AST;

public class OperatorNode extends AST {
    
    private AST left;
    private AST right;
    private Character data;

    @Override
    public AST getLeft() {
        return left;
    }

    @Override
    public AST getRight() {
        return right;
    }

    @Override
    public AST setLeft(AST l) {
        left = l;
        return this;
    }

    @Override
    public AST setRight(AST r) {
        right = r;
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
