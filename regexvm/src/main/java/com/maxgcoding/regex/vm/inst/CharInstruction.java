package com.maxgcoding.regex.vm.inst;

import com.maxgcoding.regex.vm.Instruction;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Accessors(chain = true)
public class CharInstruction extends Instruction {
    private Integer next;
    private Integer alternate;
    private Character operand;
    
    @Override
    public Integer getNext() {
        return next;
    }
    
    @Override
    public Integer getAlternate() {
        return alternate;
    }

    @Override
    public Instruction setNext(Integer i) {
        next = i;
        return this;
    }

    @Override
    public Instruction setAlternate(Integer i) {
        alternate = i;
        return this;
    }

    public Instruction setOperand(Character ch) {
        this.operand = ch;
        return this;
    }

    @Override
    public Boolean canMatch(Character ch) {
        return operand.equals(ch);
    }

    @Override
    public String toString() {
        return "[ CHAR     " + operand + " " + next + " ]"; 
    }
}