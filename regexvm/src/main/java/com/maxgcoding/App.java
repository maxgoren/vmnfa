package com.maxgcoding;

import java.util.List;
import java.util.Scanner;

import com.maxgcoding.regex.compile.ByteCodeCompiler;
import com.maxgcoding.regex.compile.NFACompiler;
import com.maxgcoding.regex.compile.parse.Parser;
import com.maxgcoding.regex.digraph.PowerSet;
import com.maxgcoding.regex.vm.VirtualMachine;

public final class App {
    private App() {
    
    }

    
    public static void main(String[] args) {
        App app = new App();
        app.demo();
        app.repl();
    }

    public void demo() {
        vmDemo("(a*b|ac)d", List.of("aaabd", "aaaacd", "abd", "acd", "bd", "cd"));
        digraphDemo("(a*b|ac)d", List.of("aaabd", "aaaacd", "abd", "acd", "bd", "cd"));
    }


    public void vmDemo(String pattern, List<String> testStrings) {
        VirtualMachine vm = new VirtualMachine(new ByteCodeCompiler()
                                                    .compile(new Parser()
                                                            .parse(pattern)));
        testStrings.forEach(str -> {
            if (vm.match(str)) {
                System.out.println("Match Found!");
            } else {
                System.out.println("No match :(");
            }
        });
    }

    public void digraphDemo(String pattern, List<String> testStrings) {
        PowerSet pm = new PowerSet(new NFACompiler()
                                        .build(new Parser()
                                                .parse(pattern)));
        testStrings.forEach(str -> {
            if (pm.match(str)){
                System.out.println("Match Found!");
            } else {
                System.out.println("No match :(");
            }
        });
    }

    private void repl() {
        try (Scanner inScan = new Scanner(System.in)) {
            String input;
            System.out.print("Pattern: ");
            VirtualMachine vm = new VirtualMachine(new ByteCodeCompiler()
                                                    .compile(new Parser()
                                                            .parse(inScan.nextLine())));
            while (true) {
                System.out.print("RegExSh> ");
                input = inScan.nextLine();
                if (input.equals("quit") || input.equals("exit"))
                    return;
                if (vm.match(input)) {
                    System.out.println("Match!");
                } else {
                    System.out.println("No Match :(");
                }
            }
        } catch (Exception ex) {

        }
    }

}
