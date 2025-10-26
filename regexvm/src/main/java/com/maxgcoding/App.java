package com.maxgcoding;

import java.util.List;

import com.maxgcoding.pm.NFAType;
import com.maxgcoding.pm.PatternMatcher;

public final class App {
    private App() {
    
    }

    
    public static void main(String[] args) {
        App app = new App();
        app.example(NFAType.VIRTUAL_MACHINE, "(a*b|a+c)d", List.of("aaaabd", "abd", "aaaacd", "acd", "bd","cd"));
        app.example(NFAType.DIGRAPH, "(a*b|a+c)d", List.of("aaaabd", "abd", "aaaacd", "acd", "bd","cd"));
   
    }
    
    private void example(NFAType type, String pattern, List<String> testStrings) {
        PatternMatcher pm = Match.patternMatcherFactory(pattern, type);
        for (String str : testStrings) {
            System.out.println("Checking " + str + " with '" + pattern + "':");
            if (pm.match(str)) {
                System.out.println("Match found!");
            } else {
                System.out.println("No match :(");
            }
        }
    }
}
