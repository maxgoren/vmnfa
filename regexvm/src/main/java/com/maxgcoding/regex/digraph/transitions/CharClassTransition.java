package com.maxgcoding.regex.digraph.transitions;

import com.maxgcoding.regex.digraph.NFAState;
import com.maxgcoding.regex.digraph.Transition;

public class CharClassTransition extends Transition {
    private String ccl;
    private NFAState destination;

    public CharClassTransition(String ccl, NFAState dest) {
        this.ccl = ccl;
        this.destination = dest;
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
    public Boolean match(Character ch) {
        char[] raw = ccl.toCharArray();
        for (int i = 0; i < raw.length; i++) {
            Character c = raw[i];
            if (i+2 < raw.length && raw[i+1] == '-') {
                return ch.compareTo(c) >= 0 && ch.compareTo(raw[i+2]) <= 0;
            } else if (c.equals(ch)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public String edgeLabel() {
        return ccl;
    }
}
