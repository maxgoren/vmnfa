package com.maxgcoding;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.maxgcoding.regex.Match;
import com.maxgcoding.regex.pm.EngineType;
import com.maxgcoding.regex.pm.PatternMatcher;

/**
 * Unit test for RegEx VM and Digraph NFA matchers
 */
class AppTest {
    /**
     * test VM
     */
    @Test
    void testVM() {
        example(EngineType.VIRTUAL_MACHINE, "a+b+",  List.of("ab", "aab", "abab", "aaaaaaaaaaabbbbbbbbbb", "abbbbb"));
        example(EngineType.VIRTUAL_MACHINE, "(a*?b|a+?c)d", List.of("aaaabd", "abd", "aaaacd", "acd", "bd","cd"));
        example(EngineType.VIRTUAL_MACHINE, "ab?c", List.of("abc", "ac", "abbc"));
        example(EngineType.VIRTUAL_MACHINE, "(r[aeiou]n.)", List.of("run", "ran", "ring", "render"));
        List.of("ab", "aab", "abab", "aaaaaaaaaaabbbbbbbbbb", "abbbbb").forEach(str -> { if (Match.patternMatcherFactory("a+b+", EngineType.VIRTUAL_MACHINE).match(str)) { System.out.println("Match Found for %s :)".formatted(str)); } } );
    }


    /*
     * test Linked Digraph NFA
     */
    @Test
    void testDigraphPositiveCase() {
        example(EngineType.DIGRAPH, "a+b+",  List.of("ab", "aab", "abab", "aaaaaaaaaaabbbbbbbbbb", "abbbbb"));
        example(EngineType.DIGRAPH, "(a*b|a+c)d", List.of("aaaabd", "abd", "aaaacd", "acd", "bd","cd"));
        example(EngineType.DIGRAPH, "0x([0-9]|[A-F])([0-9]|[A-F])*", List.of("0xff", "0x1337", "0x3AF1", "12"));
        example(EngineType.DIGRAPH, "(a+[bc])d", List.of("aaaabd", "abd", "aaaacd", "acd", "bd","cd"));
    }

    private boolean example(EngineType type, String pattern, List<String> testStrings) {
        PatternMatcher pm = Match.patternMatcherFactory(pattern, type);
        for (String str : testStrings) {
            System.out.println("Checking " + str + " with '" + pattern + "':");
            if (pm.match(str)) {
                System.out.println("Match found!");
                return false;
            } else {
                System.out.println("No match :(");
                return true;
            }
        }
        return false;
    }

}
