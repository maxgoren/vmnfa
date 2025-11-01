package com.maxgcoding.regex.compile;

import com.maxgcoding.regex.compile.parse.AST;
import com.maxgcoding.regex.compile.parse.ast.CharClassNode;
import com.maxgcoding.regex.compile.parse.ast.LazyOperatorNode;
import com.maxgcoding.regex.compile.parse.ast.LiteralNode;
import com.maxgcoding.regex.compile.parse.ast.OperatorNode;
import com.maxgcoding.regex.vm.Instruction;
import com.maxgcoding.regex.vm.Program;
import com.maxgcoding.regex.vm.inst.AnyInstruction;
import com.maxgcoding.regex.vm.inst.CharClassInstruction;
import com.maxgcoding.regex.vm.inst.CharInstruction;
import com.maxgcoding.regex.vm.inst.JumpInstruction;
import com.maxgcoding.regex.vm.inst.MatchInstruction;
import com.maxgcoding.regex.vm.inst.SplitInstruction;

public class ByteCodeCompiler {
    private Program pg;
    private int ip;
    private int MAX_CODE;
    
    public Program compile(AST node) {
        init(node.getLeft());
        build(node.getLeft());
        emit(new MatchInstruction().setOperand(((CharClassNode)node).getCcl()));
        return pg;
    }
    private void init(AST node) {
        MAX_CODE = countInstructions(node) + 1;
        System.out.println("Resrving space for " + MAX_CODE + " instructions");
        pg = new Program(MAX_CODE);
    }

    private int countInstructions(AST curr) {
        if (curr == null)
            return 0;
        int numEmits = 0;
        switch (curr) {
            case CharClassNode _ -> numEmits = 1; 
            case LiteralNode _ -> numEmits = 1;
            case OperatorNode node -> { numEmits = countEmits(node); }
            case LazyOperatorNode node -> { numEmits = countEmits(node); }
            default -> { }
        }
        return numEmits;
    }

    private int countEmits(AST node) {
        int numEmits = 0;
        switch (node.getData()) {
            case '@' -> numEmits = countInstructions(node.getLeft()) + countInstructions(node.getRight());
            case '|', '*' -> numEmits = 2 + countInstructions(node.getLeft()) + countInstructions(node.getRight());
            case '+', '?' -> numEmits = 1 + countInstructions(node.getLeft()) + countInstructions(node.getRight());
            default -> { }
        }
        return numEmits;
    }

    private void handleOperators(AST node, Boolean makeLazy) {
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
    private void build(AST curr) {
        switch (curr) {
            case OperatorNode node -> handleOperators(node, Boolean.FALSE);
            case LazyOperatorNode node -> handleOperators(node, Boolean.TRUE);
            case LiteralNode node ->  handleLiteral(node);
            case CharClassNode node -> handleCCL(node);
            default -> { }
        }
    }

    private void emit(Instruction inst) {
        pg.addInstruction(inst, ip++);
    }

    private int skipEmit(int numplace) {
        ip += numplace;
        return ip;
    }
    
    private void skipTo(int oldpos) {
        ip = oldpos;
    }

    private void handleOrOperator(AST node) {
        int spos = skipEmit(0);
        skipEmit(1);
        int L1 = skipEmit(0);
        build(node.getLeft());
        int L2 = skipEmit(0);
        skipEmit(1);
        build(node.getRight());
        int npos = skipEmit(0); 
        skipTo(L2);
        emit(new JumpInstruction().setNext(npos));
        skipTo(npos);
        int L3 = skipEmit(0);
        skipTo(spos);
        emit(new SplitInstruction().setNext(L1).setAlternate(L2+1));
        skipTo(L3); 
    }

    private void handleKleeneOp(AST node, Boolean makeLazy) {
        int spos = skipEmit(0);
        skipEmit(1);
        int L1 = skipEmit(0);
        build(node.getLeft());
        int L2 = skipEmit(0);
        skipTo(spos);
        if (makeLazy) {
            emit(new SplitInstruction().setNext(L2+1).setAlternate(L1));
        } else {
            emit(new SplitInstruction().setNext(L1).setAlternate(L2+1));
        }
        skipTo(L2);
        emit(new JumpInstruction().setNext(spos));
    }

    private void handleZeroOrOnce(AST node, Boolean makeLazy) {
        int spos = skipEmit(0);
        skipEmit(1);
        build(node.getLeft());
        int L1 = skipEmit(0);
        skipTo(spos);
        if (makeLazy) {
            emit(new SplitInstruction().setNext(L1).setAlternate(spos+1));
        } else {
            emit(new SplitInstruction().setNext(spos+1).setAlternate(L1));
        }
        skipTo(L1);
    }

    private void handleAtLeastOnce(AST node, Boolean makeLazy) {
        int spos = skipEmit(0);
        build(node.getLeft());
        int L1 = skipEmit(0);
        if (makeLazy) {
            emit(new SplitInstruction().setNext(L1+1).setAlternate(spos));
        } else {
            emit(new SplitInstruction().setNext(spos).setAlternate(L1+1));
        }
    }

    private void handleLiteral(AST node) {
        if (node.getData().equals('.')) {
            emit(new AnyInstruction().setNext(ip+1));
        } else {
            emit(new CharInstruction().setOperand(node.getData()).setNext(ip+1));
        }
    }

    private void handleCCL(AST node) {
        emit(new CharClassInstruction().setOperand(((CharClassNode)node).getCcl()).setNext(ip+1));
    }

}
