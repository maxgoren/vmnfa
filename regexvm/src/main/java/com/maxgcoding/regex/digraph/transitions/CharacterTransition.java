package com.maxgcoding.regex.digraph.transitions;

import com.maxgcoding.regex.digraph.NFAState;
import com.maxgcoding.regex.digraph.Transition;

public class CharacterTransition extends Transition {
    private final Character edgeLabel;
    private NFAState destination;
    public CharacterTransition(Character ch, NFAState dest) {
        this.edgeLabel = ch;
        this.destination = dest;
    }

    @Override
    public Boolean match(Character c) {
        return c.equals(edgeLabel);
    }

    @Override
    public void setDestination(NFAState dest) {
        destination = dest;
    }

    @Override
    public NFAState getDestination() {
        return destination;
    }
    @Override
    public String edgeLabel() {
        return String.valueOf(edgeLabel);
    }
}
