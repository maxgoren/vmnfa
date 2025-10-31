package com.maxgcoding.regex.vm.inst;

import com.maxgcoding.regex.vm.Instruction;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Accessors(chain=true)
public class MatchInstruction extends Instruction {
    private Integer next;
    private Integer alternate;
    private String operand;

    public String getOperand() {
        return operand;
    }

    public Instruction setOperand(String ccl) {
        this.operand = ccl;
        return this;
    }

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

    @Override
    public Boolean canMatch(Character ch) {
        return true;
    }

    @Override
    public String toString() {
         return "[ MATCH ]";
    }

}