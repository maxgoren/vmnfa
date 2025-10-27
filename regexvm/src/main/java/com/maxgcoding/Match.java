package com.maxgcoding;

import com.maxgcoding.compile.ByteCodeCompiler;
import com.maxgcoding.compile.NFACompiler;
import com.maxgcoding.fa.NFA;
import com.maxgcoding.parse.Node;
import com.maxgcoding.parse.Parser;
import com.maxgcoding.parse.Traversal;
import com.maxgcoding.pm.NFAType;
import com.maxgcoding.pm.PatternMatcher;
import com.maxgcoding.pm.PowerSet;
import com.maxgcoding.pm.vm.VirtualMachine;

public class Match {
    private Parser parser;
    
    private Match() {
    
    }

    public static PatternMatcher patternMatcherFactory(String pattern, NFAType type) {
        Parser parser = new Parser();
        Node ast = parser.parse(pattern);
        return type.equals(NFAType.DIGRAPH) ? new PowerSet(new NFACompiler().build(ast)):new VirtualMachine(new ByteCodeCompiler().compile(ast));
    }

    public static boolean match(String text, String pattern, NFAType type) {
        if (type == null || type.equals(NFAType.VIRTUAL_MACHINE))
            return matchVM(text, pattern);
        return matchPowerSet(text, pattern);
    }

    public static boolean matchVM(String text, String pattern) {
        Parser p = new Parser();
        ByteCodeCompiler c = new ByteCodeCompiler();
        Node ast = p.parse(pattern);
        Traversal.traverse(ast);
        VirtualMachine vm = new VirtualMachine(c.compile(ast));
        return vm.match(text);
    } 

    public static boolean matchPowerSet(String text, String pattern) {
        Parser p = new Parser();
        NFACompiler c = new NFACompiler();
        Node ast = p.parse("(a*b|ac)d");
        Traversal.traverse(ast);
        NFA nfa = c.build(ast);
        PowerSet ps = new PowerSet(nfa);
        return ps.match("aacd");
    }
}
