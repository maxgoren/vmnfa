package com.maxgcoding.regex.compile.parse;

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

    private Node factor() {
        Node lhs = null;
        if (expect('(')) {
            match('(');
            Node m = expr();
            lhs = m;
            match(')');
        } else if (isEscapeChar(lookahead())) {
            if (isPerlEscape(lookahead())) {
                switch (lookahead()) {
                    case 'D' -> { lhs = new Node().setType(NodeType.CCL).setCcl("[0-9]"); }
                    case 'd' -> { lhs = new Node().setType(NodeType.CCL).setCcl("[A-Za-z]"); }
                    case 'S' -> { }
                    case 's' -> { }
                    case 'w' -> { }
                    case 'W' -> { }
                }
            } else {
                advance();
                lhs = new Node();
                lhs.setType(NodeType.LITERAL);
                lhs.setData(lookahead());
            }
        } else if (isValLiteral(lookahead())) {
            if (expect('[')) {
                lhs = new Node();
                lhs.setType(NodeType.CCL);
                match('[');
                StringBuilder data = new StringBuilder();
                while (!expect(']')) {
                    data.append(lookahead());
                    match(lookahead());
                }
                match(']');
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
        if (expect('(') || isValLiteral(lookahead()) || isEscapeChar(lookahead())) {
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