package com.maxgcoding.regex.compile.parse;

import com.maxgcoding.regex.compile.exception.MismatchedTokenException;
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
        current = rexpr;
        spos = 0;
        AST t;
        try {
            t = expr();
        } catch (Exception e) {
            System.out.println("Shit: " + e.getMessage());
            return null;
        }
        return new CharClassNode().setCcl(rexpr).setLeft(t);
    }

    private void match(char c) throws MismatchedTokenException {
        if (expect(c)) {
            System.out.println("Matched " + c);
            advance();
            return;
        }
        throw new MismatchedTokenException(String.format("Uh oh, mismatched token at position %s: %s", spos, lookahead()));
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

    private AST parseClosure(AST lhs) throws MismatchedTokenException {
        if (expect('*') || expect('+') || expect('?')) {
            char op = lookahead();
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

    private AST parseLiteral() throws MismatchedTokenException {
        AST lhs = null;
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
        return lhs;
    }

    private AST parseEscapeChars() {
        AST lhs = null;
        if (isPerlEscape(lookahead())) {
            switch (lookahead()) {
                case 'D' -> { lhs = new CharClassNode().setCcl("[0-9]"); }
                case 'd' -> { lhs = new CharClassNode().setCcl("[A-Za-z]"); }
            }
        } else {
            advance();
            lhs = new LiteralNode();
            lhs.setData(lookahead());
        }
        return lhs;
    }

    private AST factor() throws MismatchedTokenException {
        AST lhs = null;
        if (expect('(')) {
            match('(');
            lhs = expr();
            match(')');
        } else if (isEscapeChar(lookahead())) {
            lhs = parseEscapeChars();
        } else if (isValLiteral(lookahead())) {
            lhs = parseLiteral();
        }
        if (lhs != null) {
            lhs = parseClosure(lhs);
        }
        return lhs;
    }

    private AST term() throws MismatchedTokenException {
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

    private AST expr() throws MismatchedTokenException {
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
