package com.maxgcoding.regex.digraph;

public abstract class Transition {
    public abstract void setDestination(NFAState dest);
    public abstract NFAState getDestination();
    public abstract Boolean match(Character ch);
    public abstract String edgeLabel();
}
