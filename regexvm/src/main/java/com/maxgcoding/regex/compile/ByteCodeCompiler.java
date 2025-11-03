package com.maxgcoding.regex.compile;

import com.maxgcoding.regex.compile.parse.AST;
import com.maxgcoding.regex.compile.parse.ast.*;
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

    public Program compile(AST node) {
        init(node.getLeft());
        build(node.getLeft());
        emit(new MatchInstruction().setOperand(((CharClassNode)node).getCcl()));
        return pg;
    }

    private void init(AST node) {
        int MAX_CODE = countInstructions(node) + 1;
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

    private void handleOperators(OperatorNode operatorNode) {
        switch (operatorNode) {
            case OrNode node -> handleOrOperator(node);
            case StarClosureNode node -> handleKleeneOp(node);
            case PlusClosureNode node -> handleAtLeastOnce(node);
            case QuestClosureNode node -> handleZeroOrOnce(node);
            case ConcatNode node -> {
                build(node.getLeft());
                build(node.getRight());
            }
            default -> { }
        }
    }

    private void build(AST curr) {
        switch (curr) {
            case ConcatNode node -> handleOperators(node);
            case OrNode node -> handleOperators(node);
            case QuestClosureNode node -> handleOperators(node);
            case PlusClosureNode node -> handleOperators(node);
            case StarClosureNode node -> handleOperators(node);
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

    private void handleOrOperator(OrNode node) {
        int splitPos = skipEmit(0);
        int L1 = skipEmit(1);
        build(node.getLeft());
        int jumpPos = skipEmit(0);
        int L2 = skipEmit(1);
        build(node.getRight());
        int L3 = skipEmit(0);
        skipTo(jumpPos);
        emit(new JumpInstruction().setNext(L3));
        skipTo(splitPos);
        emit(new SplitInstruction().setNext(L1).setAlternate(L2));
        skipTo(L3);
    }

    private void handleKleeneOp(StarClosureNode node) {
        int L1 = skipEmit(0);
        int L2 = skipEmit(1);
        build(node.getLeft());
        int jumpPos = skipEmit(0);
        int L3 = skipEmit(1);
        skipTo(L1);
        if (node.isLazy()) {
            emit(new SplitInstruction().setNext(L3).setAlternate(L2));
        } else {
            emit(new SplitInstruction().setNext(L2).setAlternate(L3));
        }
        skipTo(jumpPos);
        emit(new JumpInstruction().setNext(L1));
    }

    private void handleZeroOrOnce(QuestClosureNode node) {
        int splitPos = skipEmit(0);
        int L1 = skipEmit(1);
        build(node.getLeft());
        int L2 = skipEmit(0);
        skipTo(splitPos);
        if (node.isLazy()) {
            emit(new SplitInstruction().setNext(L2).setAlternate(L1));
        } else {
            emit(new SplitInstruction().setNext(L1).setAlternate(L2));
        }
        skipTo(L2);
    }

    private void handleAtLeastOnce(PlusClosureNode node) {
        int L1 = skipEmit(0);
        build(node.getLeft());
        int L3 = skipEmit(0)+1;
        if (node.isLazy()) {
            emit(new SplitInstruction().setNext(L3).setAlternate(L1));
        } else {
            emit(new SplitInstruction().setNext(L1).setAlternate(L3));
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
