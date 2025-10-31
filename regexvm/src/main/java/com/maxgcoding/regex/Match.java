package com.maxgcoding.regex;

import com.maxgcoding.regex.compile.ByteCodeCompiler;
import com.maxgcoding.regex.compile.NFACompiler;
import com.maxgcoding.regex.compile.parse.AST;
import com.maxgcoding.regex.compile.parse.Parser;
import com.maxgcoding.regex.compile.parse.PrettyPrint;
import com.maxgcoding.regex.digraph.NFA;
import com.maxgcoding.regex.digraph.PowerSet;
import com.maxgcoding.regex.pm.EngineType;
import com.maxgcoding.regex.pm.PatternMatcher;
import com.maxgcoding.regex.vm.VirtualMachine;

public class Match {
    
    private Match() {
    
    }

    public static PatternMatcher patternMatcherFactory(String pattern, EngineType type) {
        Parser parser = new Parser();
        AST ast = parser.parse(pattern);
        return type.equals(EngineType.DIGRAPH) ? new PowerSet(new NFACompiler().build(ast)):new VirtualMachine(new ByteCodeCompiler().compile(ast));
    }

    public static boolean match(String text, String pattern, EngineType type) {
        if (type == null || type.equals(EngineType.VIRTUAL_MACHINE))
            return matchVM(text, pattern);
        return matchPowerSet(text, pattern);
    }

    public static boolean matchVM(String text, String pattern) {
        Parser p = new Parser();
        ByteCodeCompiler c = new ByteCodeCompiler();
        AST ast = p.parse(pattern);
        PrettyPrint.traverse(ast);
        VirtualMachine vm = new VirtualMachine(c.compile(ast));
        return vm.match(text);
    } 

    public static boolean matchPowerSet(String text, String pattern) {
        Parser p = new Parser();
        NFACompiler c = new NFACompiler();
        AST ast = p.parse(pattern);
        PrettyPrint.traverse(ast);
        NFA nfa = c.build(ast);
        PowerSet ps = new PowerSet(nfa);
        return ps.match(text);
    }
}
