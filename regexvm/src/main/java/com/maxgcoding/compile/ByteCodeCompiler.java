package com.maxgcoding.compile;

import com.maxgcoding.parse.Node;
import com.maxgcoding.pm.vm.InstType;
import com.maxgcoding.pm.vm.Instruction;

public class ByteCodeCompiler {

    private Instruction[] code;
    private int ip;
    private int MAX_CODE;
    
    public Instruction[] compile(Node node) {
        init();
        build(node);
        emit(new Instruction().setInst(InstType.MATCH));
        return code;
    }
    private void init() {
        MAX_CODE = 5;
        code = new Instruction[MAX_CODE];
        ip = 0;
    }

    private void handleOperators(Node node) {
        switch (node.getData().charAt(0)) {
            case '|' -> handleOrOperator(node);
            case '*' -> handleKleeneOp(node);
            case '+' -> handleAtLeastOnce(node);
            case '?' -> handleZeroOrOnce(node);
            case '@' -> { 
                build(node.getLeft());
                build(node.getRight());
            }
            default -> { }
        }
    }
    private void build(Node node) {
        switch (node.getType()) {
            case OPERATOR -> handleOperators(node);
            case LITERAL ->  handleLiteral(node);
        }
    }

    private void emit(Instruction inst) {
        if (ip+1 == MAX_CODE)
            grow(2*MAX_CODE);
        code[ip++] = inst;
    }

    private int skipEmit(int numplace) {
        ip += numplace;
        return ip;
    }
    
    private void skipTo(int oldpos) {
        ip = oldpos;
    }

    private void handleOrOperator(Node node) {
        int spos = skipEmit(0);
        skipEmit(1);
        int L1 = skipEmit(0);
        build(node.getLeft());
        int L2 = skipEmit(0);
        skipEmit(1);
        build(node.getRight());
        int npos = skipEmit(0);
        skipTo(L2);
        emit(new Instruction().setInst(InstType.JMP).setNext(npos));
        skipTo(npos);
        int L3 = skipEmit(0);
        skipTo(spos);
        Instruction ni = new Instruction().setInst(InstType.SPLIT).setNext(L1).setAlternate(L2+1);
        emit(ni);
        skipTo(L3); 
    }

    private void handleKleeneOp(Node node) {
        int spos = skipEmit(0);
        skipEmit(1);
        int L1 = skipEmit(0);
        build(node.getLeft());
        int L2 = skipEmit(0);
        skipTo(spos);
        emit(new Instruction().setInst(InstType.SPLIT).setNext(L1).setAlternate(L2+1));
        skipTo(L2);
        emit(new Instruction().setInst(InstType.JMP).setNext(spos));
    }

    private void handleZeroOrOnce(Node node) {
        int spos = skipEmit(0);
        build(node.getLeft());
        int L1 = skipEmit(0);
        emit(new Instruction().setInst(InstType.SPLIT).setNext(spos).setAlternate(L1+1));
    }

    private void handleAtLeastOnce(Node node) {
        int spos = skipEmit(0);
        build(node.getLeft());
        int L1 = skipEmit(0);
        emit(new Instruction().setInst(InstType.SPLIT).setNext(spos).setAlternate(L1+1));
    }

    private void handleLiteral(Node node) {
        emit(new Instruction().setInst(InstType.CHAR).setOperand(node.getData()));
    }

    private void grow(int newSize) {
        Instruction[] tmp = code;
        code = new Instruction[newSize];
        System.arraycopy(tmp, 0, code, 0, MAX_CODE);
        MAX_CODE = newSize;
    }
}
