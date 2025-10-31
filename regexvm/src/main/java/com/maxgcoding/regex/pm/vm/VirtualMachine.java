package com.maxgcoding.regex.pm.vm;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import com.maxgcoding.regex.pm.PatternMatcher;

import lombok.AllArgsConstructor;
import lombok.Data;

public class VirtualMachine implements PatternMatcher {
    
    private final Instruction[] code;
    private String pattern;
    private String toMatch;
    
    @Data
    @AllArgsConstructor
    private class VMThread {
        private Integer inst;
        private Integer strPos;
    };
    
    public VirtualMachine(Instruction[] c) {
        this.code = c;
        for (int i = c.length-1; i >= 0; i--) {
            if (c[i] != null && c[i].getInst().equals(InstType.MATCH)) {
                this.pattern = c[i].getCclOperand();
                break;
            }
        }
        dump();
    }
    
    @Override
    public boolean match(String text) {
        this.toMatch = text;
        return matchThompson();
    }

    public boolean matchThompson() {
        Queue<VMThread> threads = new LinkedList<>();
        Queue<VMThread> alt = new LinkedList<>();
        threads.add(new VMThread(0, 0));
        for (int i = 0; ; i++) {
            while (!threads.isEmpty()) {
                VMThread thread = threads.poll();
                Instruction inst = fetch(thread.getInst());
                switch (inst.getInst()) {
                    case CCL,CHAR -> {
                        if (i < toMatch.length()) {
                            if (!inst.match(toMatch.charAt(i)))
                                break;
                            alt.add(new VMThread(inst.getNext(), i));
                        }
                        break;
                    }
                    case JMP -> {
                        threads.add(new VMThread(inst.getNext(), i));
                    }
                    case SPLIT -> { 
                        threads.add(new VMThread(inst.getNext(), i)); 
                        threads.add(new VMThread(inst.getAlternate(), i));
                    }
                    case MATCH -> { return true; }
                    case HALT -> { return false; }
                }
            }
            System.out.println("------------------------");
            threads.addAll(alt);
            alt.clear();
            if (i > toMatch.length())
                return false;
        }
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
                System.out.println("Out of instructions.");
                return false;
            }
            switch (inst.getInst()) {
                case CHAR, CCL -> {
                    if (spos < toMatch.length()) {
                        if (!inst.match(toMatch.charAt(spos))) {
                            System.out.println("Nope.");
                            return false;
                        }
                        ipos = inst.getNext();
                        System.out.println(spos);
                        spos = spos + 1;
                        System.out.println(spos);
                    } else {
                        return false;
                    }
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
        System.out.println("Loaded program for pattern: " + pattern);
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
