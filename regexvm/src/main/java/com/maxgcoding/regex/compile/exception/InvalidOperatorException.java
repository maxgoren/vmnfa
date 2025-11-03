package com.maxgcoding.regex.compile.exception;

public class InvalidOperatorException extends RuntimeException {
    public InvalidOperatorException(String formatted) {
        super(formatted);
    }
}
