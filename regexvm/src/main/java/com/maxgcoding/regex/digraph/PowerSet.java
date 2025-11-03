package com.maxgcoding.regex.digraph;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.maxgcoding.regex.digraph.transitions.CharClassTransition;
import com.maxgcoding.regex.digraph.transitions.CharacterTransition;
import com.maxgcoding.regex.digraph.transitions.EpsilonTransition;
import com.maxgcoding.regex.pm.PatternMatcher;

public class PowerSet implements PatternMatcher {
    private int matchFrom;
    private int matchLen;
    private int cpos;
    private final NFA nfa;

    public PowerSet(NFA nfa) {
        this.nfa = nfa;
    }

    /* this version matches eagerly, returning true upon
     * finding the first matching transition, more apropos
     * for use in grep-like searches
     */
    @Override
    public boolean match(String text) {
        matchFrom = 0;
        matchLen = 0;
        cpos = 0;
        Set<NFAState> next = Set.of(nfa.getStart());
        next = e_closure(next);
        for (Character c : text.toCharArray()) {
            System.out.println("Current Position: " + c);
            next = move(c, next);
            next = e_closure(next);
            cpos++;
            if (next.contains(nfa.getAccept())) {
                return true;
            } else if (next.isEmpty()) {
                return false;
            }
        }
        return next.contains(nfa.getAccept());
    }

    /*
    *   This version upon discovering that there are no valid transitions to take
    *   re-initialize to start over from the current position, and is generally
    *   _not_ what you are after. It's use case is to disambiguate the longest
    *   match amongst several possible.
     */
    public boolean matchAny(String text) {
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
                if (next.contains(nfa.getAccept()))
                    return true;
            }
            return next.contains(nfa.getAccept());
    }

    private Set<NFAState> e_closure(Set<NFAState> states) {
        System.out.println("epsilon closure: ");
        Set<NFAState> next = new HashSet<>();
        Stack<NFAState> st = new Stack<>();
        states.forEach(state -> {next.add(state); st.add(state); });
        while (!st.isEmpty()) {
            NFAState currState = st.pop();
            for (Transition trans : currState.getTrans()) {
                switch (trans) {
                    case EpsilonTransition ct -> {
                        if (!next.contains(ct.getDestination())) {
                            System.out.println(currState.getLabel() + "-(" + ct.edgeLabel() + ")-> " + ct.getDestination().getLabel());
                            next.add(ct.getDestination());
                            st.push(ct.getDestination());
                        }
                    }
                    case CharacterTransition _ -> { }
                    case CharClassTransition _ -> { }
                    default -> { }
                }
            }
        }
        System.out.println("=====================");
        return next;
    }


    private Set<NFAState> move(Character c, Set<NFAState> states) {
        Set<NFAState> next = new HashSet<>();
        System.out.println("Move on " + c);
        for (NFAState state : states) {
            for (Transition trans : state.getTrans()) {
                switch (trans) {
                    case EpsilonTransition _ -> { }
                    case CharacterTransition ct -> {
                        if (ct.match(c) && !next.contains(ct.getDestination())) {
                            System.out.println(state.getLabel() + "-(" + ct.edgeLabel() + ")-> " + ct.getDestination().getLabel());
                            next.add(ct.getDestination());
                        }
                     }
                    case CharClassTransition ct -> {
                        if (ct.match(c) && !next.contains(ct.getDestination())) {
                            System.out.println(state.getLabel() + "-(" + ct.edgeLabel() + ")-> " + ct.getDestination().getLabel());
                            next.add(ct.getDestination());
                        }
                    }
                    default -> { }
                }
            }
        }
        System.out.println("---------------------");
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
