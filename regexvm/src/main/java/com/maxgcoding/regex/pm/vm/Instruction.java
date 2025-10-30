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
        char[] raw = cclOperand.toCharArray();
        for (int i = 0; i < raw.length; i++) {
            Character c = raw[i];
            if (i+2 < raw.length && raw[i+1] == '-') {
                return ch.compareTo(c) >= 0 && ch.compareTo(raw[i+2]) <= 0;
            } else if (c.equals(ch)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        switch (this.inst) {
            case CHAR ->  { return "[" + inst + "     " + this.operand + " " + this.next + " ]"; }
            case CCL ->   { return "[" + inst + "     [" + this.cclOperand + "] " + this.next + " ]"; }
            case JMP ->   { return "[" + inst + "      " + this.next + " - ]"; }
            case SPLIT -> { return "[" + inst + "    " + next + " " + (alternate == null ? "-":alternate) + " ]"; }
            case MATCH -> { return "[ MATCH ]"; }
            case HALT ->  { return "[ HALT ]"; }
            default -> { return ""; }
        }
    }
}
