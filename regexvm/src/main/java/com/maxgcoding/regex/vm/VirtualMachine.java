package com.maxgcoding.regex.vm;

import java.util.LinkedList;
import java.util.Queue;

import com.maxgcoding.regex.pm.PatternMatcher;
import com.maxgcoding.regex.vm.inst.AnyInstruction;
import com.maxgcoding.regex.vm.inst.CharClassInstruction;
import com.maxgcoding.regex.vm.inst.CharInstruction;
import com.maxgcoding.regex.vm.inst.JumpInstruction;
import com.maxgcoding.regex.vm.inst.MatchInstruction;
import com.maxgcoding.regex.vm.inst.SplitInstruction;

import lombok.AllArgsConstructor;
import lombok.Data;

public class VirtualMachine implements PatternMatcher {
    
    private Program pg;
    private String toMatch;
    
    @Data
    @AllArgsConstructor
    private class VMThread {
        private Integer inst;
        private Integer strPos;
    };
    
    public VirtualMachine(Program pg) {
        this.pg = pg;
        dump();
    }
    
    @Override
    public boolean match(String text) {
        this.toMatch = text;
        return match();
    }

    public boolean match() {
        Queue<VMThread> threads = new LinkedList<>();
        Queue<VMThread> alt = new LinkedList<>();
        threads.add(new VMThread(0, 0));
        for (int i = 0; ; i++) {
            if (i < toMatch.length()) {
                System.out.println("Current position: " + toMatch.charAt(i));
            }
            if (runThread(threads, alt, i)) {
                System.out.println("Match found.");
                return true;
            }
            System.out.println("------------------------");
            threads.addAll(alt);
            alt.clear();
            if (i >= toMatch.length()) {
                System.out.println("Out of string.");
                return false;
            }
        }
    }


    private boolean runThread(Queue<VMThread> threads, Queue<VMThread> alt, int i) {
        while (!threads.isEmpty()) {
            VMThread thread = threads.poll();
            Instruction instr = fetch(thread.getInst());
            switch (instr) {
                case CharClassInstruction inst -> {
                    if (i < toMatch.length()) {
                        if (!inst.canMatch(toMatch.charAt(i)))
                            break;
                        alt.add(new VMThread(inst.getNext(), i));
                    }
                } 
                case CharInstruction inst-> {
                    if (i < toMatch.length()) {
                        if (!inst.canMatch(toMatch.charAt(i)))
                            break;
                        alt.add(new VMThread(inst.getNext(), i));
                    }
                }
                case AnyInstruction inst -> {
                    threads.add(new VMThread(inst.getNext(), i));
                }
                case JumpInstruction inst -> {
                    threads.add(new VMThread(inst.getNext(), i));
                }
                case SplitInstruction inst -> { 
                    threads.add(new VMThread(inst.getNext(), i)); 
                    threads.add(new VMThread(inst.getAlternate(), i));
                }
                case MatchInstruction _ -> { return true; }
                default -> { System.out.println("hmmm..."); }
            }
        }
        return false;
    }

    private Instruction fetch(int index) {
        Instruction inst = pg.getInstruction(index);
        printInstruction(index, inst);
        return inst;
    }

   

    public final void dump() {
        System.out.println("Loaded program for pattern: ");
        System.err.println("-------------------");
        pg.print();
    }

    public static void printInstruction(int index, Instruction inst) {
        System.out.println("Instruction " + index + ": " + inst.toString());
    }

}
