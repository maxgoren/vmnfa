package com.maxgcoding.regex.vm;

public abstract class Instruction {
    public abstract Integer getNext();
    public abstract Integer getAlternate();
    public abstract Instruction setNext(Integer i);
    public abstract Instruction setAlternate(Integer i);
    public abstract Boolean canMatch(Character ch);
}
