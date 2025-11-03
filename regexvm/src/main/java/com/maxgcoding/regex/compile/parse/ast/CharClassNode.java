package com.maxgcoding.regex.compile.parse.ast;

import com.maxgcoding.regex.compile.parse.AST;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@NoArgsConstructor
public class CharClassNode extends AST {

    private String ccl;

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
        return '[';
    }

    public AST setCcl(String data) {
        ccl = data;
        return this;
    }

    public String getCcl() {
        return ccl;
    }

    @Override
    public AST setData(Character ch) {
        return this;
    }
}
