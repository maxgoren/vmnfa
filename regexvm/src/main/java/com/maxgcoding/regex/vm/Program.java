package com.maxgcoding.regex.vm;

import com.maxgcoding.regex.vm.inst.MatchInstruction;

public class Program {
    private Instruction[] code;
    private int n;
    private int MAX_CODE;

    public Program(int size) {
        MAX_CODE = size;
        code = new Instruction[size];
        n = 0;
    }

    public void addInstruction(Instruction inst, int ip) {
        if (code[ip] != null) {
            throw new RuntimeException("Error: invalid IP provided.");
        }
        if (n + 1 == MAX_CODE) {
            grow(2 * MAX_CODE);
        }
        code[ip] = inst;
        n++;
    }

    public Instruction getInstruction(int ip) {
        if (ip > n || code[ip] == null) {
            throw new RuntimeException("Error: invalid IP provided.");
        }
        return code[ip];
    }

    private void grow(int newSize) {
        Instruction[] tmp = code;
        code = new Instruction[newSize];
        System.arraycopy(tmp, 0, code, 0, MAX_CODE);
        MAX_CODE = newSize;
    }

    public void print() {
        for (int i = 0; ; i++) {
            VirtualMachine.printInstruction(i, code[i]);
            if (code[i] instanceof MatchInstruction) {
                return;
            }
        }
    }
}
