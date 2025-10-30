package com.maxgcoding.regex.digraph;

import lombok.Data;

@Data
public class Transition {
    private Character edgeLabel;
    private String ccl;
    private boolean isCcl;
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

    private boolean matchCharClass(Character c) {
        return false;
    }
}
