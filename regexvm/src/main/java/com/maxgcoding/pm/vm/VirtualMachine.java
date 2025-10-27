package com.maxgcoding.pm.vm;

import com.maxgcoding.pm.PatternMatcher;

public class VirtualMachine implements PatternMatcher {
    private Instruction[] code;
    private int ip;
    private String toMatch;
    private int sp;
    public VirtualMachine(Instruction[] c) {
        this.code = c;
        this.ip = 0;
    }
    
    @Override
    public boolean match(String text) {
        this.toMatch = text;
        this.ip = 0;
        return execute(0, 0);
    }
    private Instruction fetch(int index) {
        if (index > code.length || code[index] == null)
            throw new RuntimeException("Illegal instruction");
        Instruction inst = code[index];
        if (inst.getInst().equals(InstType.CHAR))
            System.out.println("Executing " + index + ": [" + inst.getInst() + "    " + inst.getOperand() + " ]");
        else
            System.out.println("Executing " + index + ": [" + inst.getInst() + "    " + inst.getNext() + " " + inst.getAlternate() + "]");
        return inst;
    }
    private boolean execute(int ipos, int spos) {
        Instruction inst = null;
        while (true) {
            try {
                inst = fetch(ipos);
            } catch (Exception e) {
                return false;
            }
            switch (inst.getInst()) {
                case JMP: ipos = inst.getNext(); break;
                case CHAR: {
                    if (!inst.getOperand().equals(toMatch.substring(spos, spos+1)))
                        return false;
                    ipos++;
                    spos++;
                } break;
                case SPLIT: {
                    if (execute(inst.getNext(), spos))
                        return true;
                    ipos = inst.getAlternate();
                } break; 
                case MATCH:
                    return true;
                case HALT:
                    return false;
            }
        }
    }
}
