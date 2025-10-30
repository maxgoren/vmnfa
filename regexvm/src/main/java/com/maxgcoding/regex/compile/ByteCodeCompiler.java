package com.maxgcoding.regex.compile;

import com.maxgcoding.regex.parse.Node;
import com.maxgcoding.regex.parse.NodeType;
import com.maxgcoding.regex.pm.vm.InstType;
import com.maxgcoding.regex.pm.vm.Instruction;

public class ByteCodeCompiler {

    private Instruction[] code;
    private int ip;
    private int MAX_CODE;
    
    public Instruction[] compile(Node node) {
        init(node);
        build(node);
        emit(new Instruction().setInst(InstType.MATCH));
        return code;
    }
    private void init(Node node) {
        MAX_CODE = countInstructions(node);
        System.out.println("Resrving space for " + MAX_CODE + " instructions");
        code = new Instruction[MAX_CODE];
        ip = 0;
    }

    private int countInstructions(Node node) {
        if (node == null)
            return 0;
        if (node.getType().equals(NodeType.LITERAL))
            return 1;
        return 1 + countInstructions(node.getLeft()) + countInstructions(node.getRight());
    }

    private void handleOperators(Node node, Boolean makeLazy) {
        switch (node.getData()) {
            case '|' -> handleOrOperator(node);
            case '*' -> handleKleeneOp(node, makeLazy);
            case '+' -> handleAtLeastOnce(node, makeLazy);
            case '?' -> handleZeroOrOnce(node, makeLazy);
            case '@' -> { 
                build(node.getLeft());
                build(node.getRight());
            }
            default -> { }
        }
    }
    private void build(Node node) {
        switch (node.getType()) {
            case OPERATOR -> handleOperators(node, Boolean.FALSE);
            case LAZY_OPERATOR -> handleOperators(node, Boolean.TRUE);
            case LITERAL ->  handleLiteral(node);
            case CCL -> handleCCL(node);
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

    private void handleKleeneOp(Node node, Boolean makeLazy) {
        int spos = skipEmit(0);
        skipEmit(1);
        int L1 = skipEmit(0);
        build(node.getLeft());
        int L2 = skipEmit(0);
        skipTo(spos);
        if (makeLazy) {
            emit(new Instruction().setInst(InstType.SPLIT).setNext(L2+1).setAlternate(L1));
        } else {
            emit(new Instruction().setInst(InstType.SPLIT).setNext(L1).setAlternate(L2+1));
        }
        skipTo(L2);
        emit(new Instruction().setInst(InstType.JMP).setNext(spos));
    }

    private void handleZeroOrOnce(Node node, Boolean makeLazy) {
        int spos = skipEmit(0);
        skipEmit(1);
        build(node.getLeft());
        int L1 = skipEmit(0);
        skipTo(spos);
        if (makeLazy) {
            emit(new Instruction().setInst(InstType.SPLIT).setNext(L1).setAlternate(spos+1));
        } else {
            emit(new Instruction().setInst(InstType.SPLIT).setNext(spos+1).setAlternate(L1));
        }
        skipTo(L1);
    }

    private void handleAtLeastOnce(Node node, Boolean makeLazy) {
        int spos = skipEmit(0);
        build(node.getLeft());
        int L1 = skipEmit(0);
        if (makeLazy) {
            emit(new Instruction().setInst(InstType.SPLIT).setNext(L1+1).setAlternate(spos));
        } else {
            emit(new Instruction().setInst(InstType.SPLIT).setNext(spos).setAlternate(L1+1));
        }
    }

    private void handleLiteral(Node node) {
        emit(new Instruction().setInst(InstType.CHAR).setOperand(node.getData()).setNext(ip+1));
    }

    private void handleCCL(Node node) {
        emit(new Instruction().setInst(InstType.CCL).setCclOperand(node.getCcl()).setNext(ip+1));
    }

    private void grow(int newSize) {
        Instruction[] tmp = code;
        code = new Instruction[newSize];
        System.arraycopy(tmp, 0, code, 0, MAX_CODE);
        MAX_CODE = newSize;
    }
}
