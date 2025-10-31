package com.maxgcoding.regex.compile;

import java.util.Stack;

import com.maxgcoding.regex.compile.parse.AST;
import com.maxgcoding.regex.compile.parse.ast.CharClassNode;
import com.maxgcoding.regex.compile.parse.ast.LazyOperatorNode;
import com.maxgcoding.regex.compile.parse.ast.LiteralNode;
import com.maxgcoding.regex.compile.parse.ast.OperatorNode;
import com.maxgcoding.regex.digraph.NFA;
import com.maxgcoding.regex.digraph.NFAState;
import com.maxgcoding.regex.digraph.Transition;

public class NFACompiler {
    
    private final Stack<NFA> st;
    private int nextLabel;

    private int makeLabel() {
        return nextLabel++;
    }
    
    public NFACompiler() {
        st = new Stack<>();
    }

    public NFA build(AST node) {
        compile(node.getLeft());
        return st.pop();
    }

    private NFA makeAtomic(Character str) {
        NFAState ns = new NFAState(makeLabel());
        NFAState ts = new NFAState(makeLabel());
        ns.addTransition(new Transition(str, ts));
        return new NFA(ns,ts);
    }

    private NFA makeCCLAtomic(String str) {
        NFAState ns = new NFAState(makeLabel());
        NFAState ts = new NFAState(makeLabel());
        ns.addTransition(new Transition(str, ts));
        return new NFA(ns,ts);
    }

    private NFA makeEpsilonAtomic() {
        NFAState ns = new NFAState(makeLabel());
        NFAState ts = new NFAState(makeLabel());
        ns.addTransition(new Transition(ts));
        return new NFA(ns,ts);
    }

    private NFA makeConcat(NFA lhs, NFA rhs) {
        lhs.getAccept().addTransition(new Transition(rhs.getStart()));
        lhs.setAccept(rhs.getAccept());
        return lhs;
    }
    
    private NFA makeAlternate(NFA lhs, NFA rhs) {
        NFAState ns = new NFAState(makeLabel());
        NFAState ts = new NFAState(makeLabel());
        ns.addTransition(new Transition(lhs.getStart()));
        ns.addTransition(new Transition(rhs.getStart()));
        lhs.getAccept().addTransition(new Transition(ts));
        rhs.getAccept().addTransition(new Transition(ts));
        return new NFA(ns, ts);
    }
    private NFA makeKleene(NFA lhs, boolean mustmatch) {
        NFAState ns = new NFAState(makeLabel());
        NFAState ts = new NFAState(makeLabel());
        if (!mustmatch)
            ns.addTransition(new Transition(ts));
        ns.addTransition(new Transition(lhs.getStart()));
        lhs.getAccept().addTransition(new Transition(lhs.getStart()));
        lhs.getAccept().addTransition(new Transition(ts));
        return new NFA(ns, ts);
    }
    private void compile(AST curr) {
        switch (curr) {
            case null -> { }
            case LiteralNode node -> st.push(makeAtomic(node.getData()));
            case CharClassNode node -> st.push(makeCCLAtomic(node.getCcl()));
            case OperatorNode node -> compileOperator(node);
            case LazyOperatorNode node -> compileOperator(node);
            default -> { }
        }
    }

    private void compileOperator(AST node) {
        switch (node.getData()) {
            case '|' -> {
                compile(node.getLeft());
                compile(node.getRight());
                NFA rhs = st.pop();
                NFA lhs = st.pop();
                st.push(makeAlternate(lhs, rhs));
                break;
            }
            case '@' -> {
                compile(node.getLeft());
                compile(node.getRight());
                NFA rhs = st.pop();
                NFA lhs = st.pop();
                st.push(makeConcat(lhs, rhs));
                break;
            }
            case '*' -> {
                compile(node.getLeft());
                NFA lhs = st.pop();
                st.push(makeKleene(lhs, false));
                break;
            }
            case '+' -> {
                compile(node.getLeft());
                NFA lhs = st.pop();
                st.push(makeKleene(lhs, true));
                break;
            }
            case '?' -> {
                compile(node.getLeft());
                NFA lhs = st.pop();
                st.push(makeAlternate(lhs, makeEpsilonAtomic()));
                break;
            }
        }
    }
}
