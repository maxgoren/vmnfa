package com.maxgcoding.regex.digraph.transitions;

import com.maxgcoding.regex.digraph.NFAState;
import com.maxgcoding.regex.digraph.Transition;

public class EpsilonTransition extends Transition{
    private NFAState destination;
    public EpsilonTransition(NFAState dest) {
        this.destination = dest;
    }

    @Override
    public NFAState getDestination() {
        return destination;
    }

    @Override
    public Boolean match(Character ch) {
        return true;
    }
    @Override
    public String edgeLabel() {
        return "&";
    }
}
