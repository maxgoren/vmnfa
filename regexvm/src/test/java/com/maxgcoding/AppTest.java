package com.maxgcoding;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void testPlusClosure() {
        PatternMatcher pm = Match.patternMatcherFactory("a+b+", EngineType.VIRTUAL_MACHINE);
        List.of("ab", "aab", "abab", "aaaaaaaaaaabbbbbbbbbb", "abbbbb").forEach(str -> assertTrue(pm.match(str)));
        List.of("a", "b", "zab").forEach(str -> assertFalse(pm.match(str)));
    }
    @Test
    void testLazyOperators() {
        PatternMatcher pm = Match.patternMatcherFactory("(a*?b|a+?c)d", EngineType.VIRTUAL_MACHINE);
        assertTrue(pm.match("aaaabd"));
        assertTrue(pm.match("abd"));
        assertTrue(pm.match("aaaaaacd"));
        assertFalse(pm.match("cd"));
        assertTrue(pm.match("bd"));
    }
    @Test
    void testZeroOrOnceOperator() {
        PatternMatcher pm = Match.patternMatcherFactory("ab?c", EngineType.VIRTUAL_MACHINE);
        List.of("abc", "ac").forEach(str -> assertTrue(pm.match(str)));
        assertFalse(pm.match("azc"));
    }
    @Test
    void testCharClassVowles() {
        PatternMatcher pm = Match.patternMatcherFactory("(r[aeiou]n.)", EngineType.VIRTUAL_MACHINE);
        List.of("run", "ran", "ring", "render").forEach(str -> assertTrue(pm.match(str)));
    }   
}
