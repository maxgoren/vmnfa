package com.maxgcoding.regex.vm.inst;

import com.maxgcoding.regex.vm.Instruction;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Accessors(chain = true)
public class CharClassInstruction extends Instruction {
    private Integer next;
    private Integer alternate;
    private String operand;
    
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

    public Instruction setOperand(String ccl) {
        this.operand = ccl;
        return this;
    }

    @Override
    public Boolean canMatch(Character ch) {
        char[] raw = operand.toCharArray();
        for (int i = 0; i < raw.length; i++) {
            Character c = raw[i];
            if (i + 2 < raw.length && raw[i + 1] == '-') {
                return (ch.compareTo(c) >= 0) && (ch.compareTo(raw[i + 2]) <= 0);
            } else if (c.equals(ch)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "[ CCL       [" + operand + "] " + next + " ]";
    }
}