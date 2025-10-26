package com.maxgcoding.vm;

import com.maxgcoding.pm.PatternMatcher;

public class VirtualMachine implements PatternMatcher {
    private Instruction[] code;
    private int ip;
    private String toMatch;
    private int sp;
    public VirtualMachine(Instruction[] c, int i) {
        this.code = c;
        this.ip = i;
    }
    
    @Override
    public boolean match(String text) {
        this.toMatch = text;
        this.sp = 0;
        return recursive(0, 0);
    }

    private boolean recursive(int ipos, int spos) {
        System.out.println("Executing " + ipos + ": " + code[ipos]);
        switch (code[ipos].getInst()) {
            case CHAR:
                if (!code[ipos].getOperand().equals(toMatch.substring(spos, spos+1)))
                    return false;
                return recursive(ipos+1, spos+1);
            case JMP: return recursive(code[ipos].getNextInst(), spos);
            case SPLIT:
                if (recursive(code[ipos].getNextInst(), spos))
                    return true;
                return recursive(code[ipos].getAltInst(), spos);
            case MATCH:
                return true;
            case HALT:
                return false;
        }
        return false;
    }
}
