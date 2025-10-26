package com.maxgcoding.compile;

import com.maxgcoding.parse.Node;
import com.maxgcoding.vm.InstType;
import com.maxgcoding.vm.Instruction;

public class ByteCodeCompiler {
    private Instruction[] code;
    private int sp;

    public Instruction[] compile(Node node) {
        init();
        build(node);
        emit(new Instruction().setInst(InstType.MATCH));
        return code;
    }
    private void init() {
        code = new Instruction[50];
    }
    private void emit(Instruction inst) {
        code[sp++] = inst;
    }
    private int skipEmit(int numplace) {
        sp += numplace;
        return sp;
    }
    private void rewind(int oldpos) {
        sp = oldpos;
    }
    private void handleOperators(Node node) {
        switch (node.getData().charAt(0)) {
            case '|': {
                int spos = skipEmit(0);
                skipEmit(1);
                int L1 = skipEmit(0);
                build(node.getLeft());
                int cpos = skipEmit(0);
                skipEmit(1);
                build(node.getRight());
                int npos = skipEmit(0);
                rewind(cpos);
                emit(new Instruction().setInst(InstType.JMP).setNextInst(npos));
                rewind(npos);
                int lpos = skipEmit(0);
                rewind(spos);
                Instruction ni = new Instruction().setInst(InstType.SPLIT).setNextInst(L1).setAltInst(cpos+1);
                emit(ni);
                rewind(lpos); 
            } break;
            case '@': { 
                build(node.getLeft());
                build(node.getRight());
            } break;
            case '*': { 
                int spos = skipEmit(0);
                skipEmit(1);
                int L1 = skipEmit(0);
                build(node.getLeft());
                int L2 = skipEmit(0);
                rewind(spos);
                emit(new Instruction().setInst(InstType.SPLIT).setNextInst(L1).setAltInst(L2+1));
                rewind(L2);
                emit(new Instruction().setInst(InstType.JMP).setNextInst(spos));
            } break;
            case '+': { 
                int spos = skipEmit(0);
                build(node.getLeft());
                int tpos = skipEmit(0);
                emit(new Instruction().setInst(InstType.SPLIT).setNextInst(spos).setAltInst(tpos+1));
            } break;
            case '?': { 
                build(node.getLeft());
            } break;
            default:
                break;
        }
    }
    private void build(Node node) {
        switch (node.getType()) {
            case OPERATOR: {
                handleOperators(node);
            } break;
            case LITERAL: {
                Instruction newInst = new Instruction();
                newInst.setOperand(node.getData());
                newInst.setInst(InstType.CHAR);
                emit(newInst);
            } break;         
        }
    }
}
