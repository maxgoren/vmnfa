package com.maxgcoding.regex.parse;

import lombok.Data;

@Data
public class Parser {
    private String current;
    private int spos;

    
    public Node parse(String rexpr) {
        this.current = rexpr;
        this.spos = 0;
        Node t = expr();
        return t;
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

    private Boolean isLit() {
        return Character.isAlphabetic(lookahead()) || Character.isDigit(lookahead()) || expect('.') || expect('[');
    }

    private Node factor() {
        Node lhs = null;
        if (expect('(')) {
            match('(');
            Node m = expr();
            lhs = m;
            match(')');
        } else if (isLit()) {
            if (expect('[')) {
                lhs = new Node();
                lhs.setType(NodeType.CCL);
                StringBuilder data = new StringBuilder();
                while (!expect(']')) {
                    data.append(lookahead());
                    advance();
                }
                advance();
                lhs.setCcl(data.toString());
            } else {
                lhs = new Node();
                lhs.setType(NodeType.LITERAL);
                lhs.setData(lookahead());
                System.out.println("Matched Literal: " + lookahead());
                advance();
            }
        }
        if (expect('*') || expect('+') || expect('?')) {
            Node n = new Node();
            n.setData(lookahead());
            match(lookahead());
            if (expect('?')) {
                n.setType(NodeType.LAZY_OPERATOR);
                match('?');
            } else {
                n.setType(NodeType.OPERATOR);
             }
            n.setLeft(lhs);
            lhs = n;
        }
        return lhs;
    }
    private Node term() {
        Node lhs = factor();
        if (expect('(') || isLit()) {
            Node t = new Node();
            t.setType(NodeType.OPERATOR);
            t.setData('@'); 
            t.setLeft(lhs);
            System.out.println("Making Concat");
            t.setRight(term());
            lhs = t;
        }
        return lhs;
    }
    private Node expr() {
        Node lhs = term();
        if (expect('|')) {
            match('|');
            Node t = new Node();
            t.setType(NodeType.OPERATOR);
            t.setData('|');
            t.setLeft(lhs);
            t.setRight(expr());
            lhs = t;
        }
        return lhs;
    }
}
