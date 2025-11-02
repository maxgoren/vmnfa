package com.maxgcoding.regex.compile.parse;

import com.maxgcoding.regex.compile.parse.ast.CharClassNode;
import com.maxgcoding.regex.compile.parse.ast.LazyOperatorNode;
import com.maxgcoding.regex.compile.parse.ast.LiteralNode;
import com.maxgcoding.regex.compile.parse.ast.OperatorNode;

import lombok.Data;

@Data
public class Parser {
    private String current;
    private int spos;

    public AST parse(String rexpr) {
        this.current = rexpr;
        this.spos = 0;
        AST t = expr();
        return new CharClassNode().setCcl(rexpr).setLeft(t);
    }
    
    private boolean match(char c) {
        if (expect(c)) {
            System.out.println("Matched " + c);
            advance();
            return true;
        }
        System.out.println("Uh oh, mismatched token: " + lookahead());
        advance();
        return false;
    }
    
    private boolean expect(char c) {
        return c == lookahead();
    }
    
    private char lookahead() {
        return spos < current.length() ? current.charAt(spos):Character.MIN_VALUE;
    }
    
    private void advance() {
            spos++;
    }

    private void rewind() {
        spos--;
    }

    private Boolean isValLiteral(Character ch) {
        return Character.isAlphabetic(ch) || Character.isDigit(ch) || expect('.') || expect('[');
    }

    private Boolean isPerlEscape(Character c) {
        if (isEscapeChar(c)) {
            advance();
            if (c.equals('d') || c.equals('s') || c.equals('w'))
                return true;
        }
        rewind();
        return false;
    }

    private Boolean isEscapeChar(Character c) {
        return c.equals('\\');
    }

    private AST factor() {
        AST lhs = null;
        if (expect('(')) {
            match('(');
            AST m = expr();
            lhs = m;
            match(')');
        } else if (isEscapeChar(lookahead())) {
            if (isPerlEscape(lookahead())) {
                switch (lookahead()) {
                    case 'D' -> { lhs = new CharClassNode().setCcl("[0-9]"); }
                    case 'd' -> { lhs = new CharClassNode().setCcl("[A-Za-z]"); }
                    case 'S' -> { }
                    case 's' -> { }
                    case 'w' -> { }
                    case 'W' -> { }
                }
            } else {
                advance();
                lhs = new LiteralNode();
                lhs.setData(lookahead());
            }
        } else if (isValLiteral(lookahead())) {
            if (expect('[')) {
                match('[');
                StringBuilder data = new StringBuilder();
                while (!expect(']')) {
                    data.append(lookahead());
                    match(lookahead());
                }
                match(']');
                lhs = new CharClassNode().setCcl(data.toString());
            } else {
                lhs = new LiteralNode();
                lhs.setData(lookahead());
                System.out.println("Matched Literal: " + lookahead());
                advance();
            }
        }
        if (expect('*') || expect('+') || expect('?')) {
            Character op = lookahead();
            match(op);
            AST n;
            if (expect('?')) {
                n = new LazyOperatorNode();
                match('?');
            } else {
                n = new OperatorNode();
            }
            n.setData(op);
            n.setLeft(lhs);
            lhs = n;
        }
        return lhs;
    }

    private AST term() {
        AST lhs = factor();
        if (expect('(') || isValLiteral(lookahead()) || isEscapeChar(lookahead())) {
            AST t = new OperatorNode();
            t.setData('@'); 
            t.setLeft(lhs);
            System.out.println("Making Concat");
            t.setRight(term());
            lhs = t;
        }
        return lhs;
    }

    private AST expr() {
        AST lhs = term();
        if (expect('|')) {
            match('|');
            AST t = new OperatorNode();
            t.setData('|');
            t.setLeft(lhs);
            t.setRight(expr());
            lhs = t;
        }
        return lhs;
    }
}