package com.sssl.seqrgraphdemo.utils;

public class SignatureVerifyException extends Exception {
    public SignatureVerifyException(String message) {
        super(message);
    }

    public SignatureVerifyException(Exception exp) {
        super(exp);
    }

    public SignatureVerifyException(String message, Exception exp) {
        super(message, exp);
    }

    public SignatureVerifyException() {
        super("Can,t verify signature");
    }
}