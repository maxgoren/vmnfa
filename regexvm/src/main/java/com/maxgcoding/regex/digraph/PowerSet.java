package com.maxgcoding.regex.digraph;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.maxgcoding.regex.pm.PatternMatcher; 

public class PowerSet implements PatternMatcher {
    private int matchFrom;
    private int matchLen;
    private int cpos;
    private final NFA nfa;

    public PowerSet(NFA nfa) {
        this.nfa = nfa;
    }

    @Override
    public boolean match(String text) {
        matchFrom = 0;
        matchLen = 0;
        cpos = 0;
        Set<NFAState> next = Set.of(nfa.getStart());
        next = e_closure(next);
        for (Character c : text.toCharArray()) {
            next = move(c, next);
            next = checkMatchAndReset(nfa, text, next);
            next = e_closure(next);
            next = checkMatchAndReset(nfa, text, next);
            cpos++;
        }
        return next.contains(nfa.getAccept());
    }

    private Set<NFAState> e_closure(Set<NFAState> states) {
        Set<NFAState> next = new HashSet<>();
        Stack<NFAState> st = new Stack<>();
        states.forEach(state -> {next.add(state); st.add(state); });
        while (!st.isEmpty()) {
            NFAState currState = st.pop();
            for (Transition t : currState.getTrans()) {
                if (t.getIsEpsilon() && !next.contains(t.getDestination())) {
                    System.out.println(currState.getLabel() + "-(" + t.getEdgeLabel() + ")-> " + t.getDestination().getLabel());
                    st.push(t.getDestination());
                    next.add(t.getDestination());
                }
            }
        }
        return next;
    }


    private Set<NFAState> move(Character c, Set<NFAState> states) {
        Set<NFAState> next = new HashSet<>();
        for (NFAState state : states) {
            for (Transition t : state.getTrans()) {
                if (!t.getIsEpsilon() && (t.getCcl() != null ? t.getCcl().contains(String.valueOf(c)):t.getEdgeLabel().equals(c)) && !next.contains(t.getDestination())) {
                    System.out.println(state.getLabel() + "-(" + t.getEdgeLabel() + ")-> " + t.getDestination().getLabel());
                    next.add(t.getDestination());
                }
            }
        }
        return next;
    }

    private Set<NFAState> checkMatchAndReset(NFA nfa, String text, Set<NFAState> next) {
        if (next.contains(nfa.getAccept())) {
            System.out.print("Match found: ");
            matchLen = cpos - matchFrom;
            for (int i = matchFrom; i <= matchFrom+matchLen; i++) {
                System.out.print(text.charAt(i));
            }
            System.out.println();
        } else if (next.isEmpty()) {
            next = e_closure(Set.of(nfa.getStart()));
            matchFrom = cpos;
            matchLen = 0;
        }
        return next;
    }

}
