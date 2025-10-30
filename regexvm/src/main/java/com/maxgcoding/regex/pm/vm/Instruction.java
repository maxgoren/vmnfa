package com.maxgcoding.regex.pm.vm;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Instruction {
    private InstType inst;
    private Character operand;
    private String cclOperand;
    private Integer next;
    private Integer alternate;

    public boolean match(Character ch) {
        switch (this.inst) {
            case CCL -> { return matchCcl(ch); }
            case CHAR -> { return (this.operand.equals(ch) || this.operand.equals('.')); }
            default -> { return false; }
        }
    } 

    private boolean matchCcl(Character ch) {
        for (Character c : cclOperand.toCharArray()) {
            if (c.equals(ch))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        switch (this.inst) {
            case CHAR -> { return "[" + inst + "     " + this.operand + " " + this.next + " ]"; }
            case JMP -> { return ": [" + inst + "      " + this.next + " - ]"; }
            case MATCH -> { return " [ MATCH ]"; }
            default -> { return " [" + inst + "    " + next + " " + (alternate == null ? "-":alternate) + " ]"; }
        }
    }
}
