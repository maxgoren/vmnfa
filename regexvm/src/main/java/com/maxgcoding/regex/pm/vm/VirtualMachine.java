package com.maxgcoding.regex.pm.vm;

import java.util.Stack;

import com.maxgcoding.regex.pm.PatternMatcher;

import lombok.AllArgsConstructor;
import lombok.Data;

public class VirtualMachine implements PatternMatcher {
    
    private final Instruction[] code;
    
    private String toMatch;

    @Data
    @AllArgsConstructor
    private class VMThread {
        private Integer inst;
        private Integer strPos;
    };
    
    public VirtualMachine(Instruction[] c) {
        this.code = c;
        dump();
    }
    
    @Override
    public boolean match(String text) {
        this.toMatch = text;
        return iterativeBT();
    }

    public boolean iterativeBT() {
        Stack<VMThread> threads = new Stack<>();
        threads.push(new VMThread(0, 0));
        while (!threads.empty()) {
            VMThread thread = threads.pop();
            if (runThread(threads, thread.getInst(), thread.getStrPos())) {
                return true;
            }
        }
        return false;
    }

    private Instruction fetch(int index) {
        if (index > code.length || code[index] == null)
            throw new RuntimeException("Illegal instruction");
        Instruction inst = code[index];
        printInstruction(index, inst);
        return inst;
    }

    private boolean runThread(Stack<VMThread> st, Integer ipos, Integer spos) {
        while (true) {
            Instruction inst;
            try {
                inst = fetch(ipos);
            } catch (Exception e) {
                System.out.println("Out of threads.");
                return false;
            }
            switch (inst.getInst()) {
                case CHAR -> {
                    if (spos < toMatch.length() && !inst.match(toMatch.charAt(spos))) {
                        return false;
                    }
                    ipos++; spos++;
                }
                case CCL -> {
                    if (spos < toMatch.length() && !inst.match(toMatch.charAt(spos))) {
                        return false;
                    }
                    ipos++; spos++;
                }
                case SPLIT -> {
                    st.push(new VMThread(inst.getAlternate(), spos));
                    ipos = inst.getNext();
                } 
                case JMP -> { ipos = inst.getNext(); }
                case MATCH -> { return true; }
                case HALT -> { return false; }
            }
        }
    }

    public final void dump() {
        System.out.println("Loaded program: ");
        printProgram(code);
        System.err.println("-------------------");
    }

    public static void printInstruction(int index, Instruction inst) {
        System.out.println("Instruction " + index + ": " + inst.toString());
    }

    public static void printProgram(Instruction[] instructions) {
        for (int i = 0; ; i++) {
            printInstruction(i, instructions[i]);
            if (instructions[i].getInst().equals(InstType.MATCH)) {
                return;
            }
        }
    }

}
