package com.maxgcoding;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.maxgcoding.pm.NFAType;
import com.maxgcoding.pm.PatternMatcher;

/**
 * Unit test for RegEx VM and Digraph NFA matchers
 */
class AppTest {
    /**
     * test VM
     */
    @Test
    void testVM() {
        String pattern = "(a*b|a+c)d";
        NFAType type = NFAType.VIRTUAL_MACHINE;
        List<String> testStrings = List.of("aaaabd", "abd", "aaaacd", "acd", "bd");
        PatternMatcher pm = Match.patternMatcherFactory(pattern, type);
        for (String str : testStrings) {
            System.out.println("Checking " + str + " with '" + pattern + "':");
            assertTrue(pm.match(str));
        }
    }

    /*
     * test Linked Digraph NFA
     */
    @Test
    void testPowerSet() {
        String pattern = "(a*b|a+c)d";
        NFAType type = NFAType.DIGRAPH;
        List<String> testStrings = List.of("aaaabd", "abd", "aaaacd", "acd", "bd");
        PatternMatcher pm = Match.patternMatcherFactory(pattern, type);
        for (String str : testStrings) {
            System.out.println("Checking " + str + " with '" + pattern + "':");
            assertTrue(pm.match(str));
        }
    }

}
