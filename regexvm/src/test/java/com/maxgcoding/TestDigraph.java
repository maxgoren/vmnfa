package com.maxgcoding;

import com.maxgcoding.regex.Match;
import com.maxgcoding.regex.pm.EngineType;
import com.maxgcoding.regex.pm.PatternMatcher;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDigraph {
    @Test
    void testPlusClosure() {
        PatternMatcher pm = Match.patternMatcherFactory("a+b+", EngineType.DIGRAPH);
        List.of("ab", "aab", "abab", "aaaaaaaaaaabbbbbbbbbb", "abbbbb").forEach(str -> assertTrue(pm.match(str)));
        List.of("a", "b", "zab").forEach(str -> assertFalse(pm.match(str)));
    }

    @Test
    void testEagerOperators() {
        PatternMatcher pm = Match.patternMatcherFactory("(a*b|a+c)d", EngineType.DIGRAPH);
        assertTrue(pm.match("aaaabd"));
        assertTrue(pm.match("abd"));
        assertTrue(pm.match("aaaaaacd"));
        assertFalse(pm.match("cd"));
        assertTrue(pm.match("bd"));
    }

    @Test
    void testLazyOperators() {
        PatternMatcher pm = Match.patternMatcherFactory("(a*?b|a+?c)d", EngineType.DIGRAPH);
        assertTrue(pm.match("aaaabd"));
        assertTrue(pm.match("abd"));
        assertTrue(pm.match("aaaaaacd"));
        assertFalse(pm.match("cd"));
        assertTrue(pm.match("bd"));
    }
    @Test
    void testZeroOrOnceOperator() {
        PatternMatcher pm = Match.patternMatcherFactory("ab?c", EngineType.DIGRAPH);
        List.of("abc", "ac").forEach(str -> assertTrue(pm.match(str)));
        assertFalse(pm.match("azc"));
    }
    @Test
    void testCharClassVowels() {
        PatternMatcher pm = Match.patternMatcherFactory("(r[aeiou]n.*)", EngineType.DIGRAPH);
        List.of("run", "ran", "ring", "render").forEach(str -> assertTrue(pm.match(str)));
    }
}
