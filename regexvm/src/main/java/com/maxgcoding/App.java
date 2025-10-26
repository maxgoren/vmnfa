package com.maxgcoding;

import com.maxgcoding.compile.ByteCodeCompiler;
import com.maxgcoding.compile.NFACompiler;
import com.maxgcoding.fa.NFA;
import com.maxgcoding.parse.Node;
import com.maxgcoding.parse.Parser;
import com.maxgcoding.parse.Traversal;
import com.maxgcoding.pm.PowerSet;
import com.maxgcoding.vm.VirtualMachine;

public final class App {
    private App() {
    
    }

    
    public static void main(String[] args) {
        App app = new App();
        app.vmexample();
        //app.psexample();
    }

    private void psexample() {
        Parser p = new Parser();
        NFACompiler c = new NFACompiler();
        Node ast = p.parse("(a*b|ac)d");
        Traversal.traverse(ast);
        NFA nfa = c.build(ast);
        PowerSet ps = new PowerSet(nfa);
        if (ps.match("aacd")) {
            System.out.println("Ok peeta!");
        }
    }
    private void vmexample() {
        Parser p = new Parser();
        ByteCodeCompiler c = new ByteCodeCompiler();
        Node ast = p.parse("(a*b|ac)d");
        Traversal.traverse(ast);
        VirtualMachine vm = new VirtualMachine(c.compile(ast), 0);
        if (vm.match("aaabd")) {
            System.out.println("Ok peeta!");
        }
    }
}
