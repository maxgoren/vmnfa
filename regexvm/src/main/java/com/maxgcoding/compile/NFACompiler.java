package com.maxgcoding.compile;

import java.util.Stack;

import com.maxgcoding.fa.NFA;
import com.maxgcoding.fa.NFAState;
import com.maxgcoding.fa.Transition;
import com.maxgcoding.parse.Node;
import com.maxgcoding.parse.NodeType;

public class NFACompiler {
    
    private Stack<NFA> st;
    private int nextLabel;

    private int makeLabel() {
        return nextLabel++;
    }
    
    public NFACompiler() {
        st = new Stack<>();
    }

    public NFA build(Node node) {
        compile(node);
        return st.pop();
    }

    private NFA makeAtomic(String str) {
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
    private void compile(Node node) {
        if (node == null)
            return;
        if (node.getType().equals(NodeType.LITERAL)) {
            st.push(makeAtomic(node.getData()));
        } else {
            switch (node.getData()) {
                case "|" -> {
                        compile(node.getLeft());
                        compile(node.getRight());
                        NFA rhs = st.pop();
                        NFA lhs = st.pop();
                        st.push(makeAlternate(lhs, rhs));
                        break;
                    }
                case "@" -> {
                        compile(node.getLeft());
                        compile(node.getRight());
                        NFA rhs = st.pop();
                        NFA lhs = st.pop();
                        st.push(makeConcat(lhs, rhs));
                        break;
                    }
                case "*" -> {
                        compile(node.getLeft());
                        NFA lhs = st.pop();
                        st.push(makeKleene(lhs, false));
                        break;
                    }
                case "+" -> {
                        compile(node.getLeft());
                        NFA lhs = st.pop();
                        st.push(makeKleene(lhs, true));
                        break;
                    }
                    case "?" -> {
                        compile(node.getLeft());
                        NFA lhs = st.pop();
                        st.push(makeAlternate(lhs, makeEpsilonAtomic()));
                        break;
                    }
            }
        }
    }
}
