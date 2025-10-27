package com.maxgcoding.fa;

import lombok.Data;

@Data
public class Transition {
    private Character edgeLabel;
    private Boolean isEpsilon;
    private NFAState destination;
    public Transition(Character label, NFAState dest) {
        this.edgeLabel = label;
        this.destination = dest;
        this.isEpsilon = false;
    }
    public Transition(NFAState dest) {
        this.edgeLabel = '&';
        this.destination = dest;
        this.isEpsilon = true;
    }
    @Override
    public int hashCode() {
        return edgeLabel.hashCode();
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof Transition) {
            return this.edgeLabel.equals(((Transition) o).getEdgeLabel()) && this.destination.equals(((Transition) o).getDestination());
        }
        return false;
    }
}
