package com.maxgcoding.regex.digraph;

import lombok.Data;

@Data
public class Transition {
    private Character edgeLabel;
    private String ccl;
    private Boolean isCcl;
    private Boolean isEpsilon;
    private NFAState destination;
    public Transition(Character label, NFAState dest) {
        this.edgeLabel = label;
        this.destination = dest;
        this.isEpsilon = false;
        this.isCcl = false;
        this.ccl = null;
    }
    public Transition(String ccl, NFAState dest) {
        this.ccl = ccl;
        this.isCcl = true;
        this.destination = dest;
        this.edgeLabel = ccl.charAt(0);
        this.isEpsilon = false;
    }
    public Transition(NFAState dest) {
        this.edgeLabel = '&';
        this.destination = dest;
        this.isEpsilon = true;
        this.isCcl = false;
        this.ccl = null;
    }
    @Override
    public int hashCode() {
        return edgeLabel.hashCode();
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof Transition transition) {
            if (isCcl)
                return this.ccl.equals(transition.getCcl()) && this.destination.equals(transition.getDestination());
            return this.edgeLabel.equals(transition.getEdgeLabel()) && this.destination.equals(transition.getDestination());
        }
        return false;
    }
    public boolean match(Character c) {
        if (isCcl) {
            return matchCharClass(c);
        }
        return false;
    }

    private boolean matchCharClass(Character ch) {
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
}
