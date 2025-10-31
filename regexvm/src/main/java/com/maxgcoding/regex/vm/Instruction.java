package com.maxgcoding.regex.vm;

public abstract class Instruction {
    abstract public Integer getNext();
    abstract public Integer getAlternate();
    abstract public Instruction setNext(Integer i);
    abstract public Instruction setAlternate(Integer i);
    abstract public Boolean canMatch(Character ch);
}
