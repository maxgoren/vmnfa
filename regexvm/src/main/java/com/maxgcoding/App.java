package com.maxgcoding;

import java.util.Scanner;

import com.maxgcoding.regex.compile.ByteCodeCompiler;
import com.maxgcoding.regex.compile.parse.Parser;
import com.maxgcoding.regex.vm.VirtualMachine;

public final class App {
    private App() {
    
    }

    
    public static void main(String[] args) {
        App app = new App();
        app.repl();
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
