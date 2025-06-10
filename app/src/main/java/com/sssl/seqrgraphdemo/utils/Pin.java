package com.sssl.seqrgraphdemo.utils;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Pin {

    private final byte[] aesKey;
    private final byte[] iv;
    private final String keyId;

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private static byte[] calcMD5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");  //NOSONAR
        return md.digest(input.getBytes());
    }

    private static byte[] calcSHA256(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes());
    }

    private static String toHex(byte[] input) {
        char[] hexChars = new char[input.length * 2];
        for (int j = 0; j < input.length; j++) {
            int v = input[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public Pin(String pin, String salt) throws NoSuchAlgorithmException, IllegalArgumentException {

        if (pin == null) {
            throw new IllegalArgumentException("pin is null");
        }

        if (pin.length() < 4) {
            throw new IllegalArgumentException("pin.length() is less than 4");
        }

        if (salt == null) {
            throw new IllegalArgumentException("salt is null");
        }

        if (salt.length() == 0) {
            throw new IllegalArgumentException("salt.length() is 0");
        }

        keyId = toHex(calcSHA256(pin));
        aesKey = calcSHA256(keyId + salt);
        iv = calcMD5(keyId);
    }

    public Pin(String pin) throws NoSuchAlgorithmException, IllegalArgumentException {

        if (pin == null) {
            throw new IllegalArgumentException("pin is null");
        }

        if (pin.length() < 4) {
            throw new IllegalArgumentException("pin.length() is less than 4");
        }

        aesKey = calcSHA256(pin);
        iv = calcMD5(toHex(aesKey));
        keyId = toHex(aesKey);
    }

    public byte[] decrypt(byte[] input) throws GeneralSecurityException, IllegalArgumentException {
        if (input == null) {
            throw new IllegalArgumentException("input is null");
        }
        if (input.length == 0) {
            throw new IllegalArgumentException("input.length is 0");
        }

        Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ci.init(
                Cipher.DECRYPT_MODE,
                new SecretKeySpec(aesKey, 0, aesKey.length, "AES"),
                new IvParameterSpec(iv) // NOSONAR
        );
        return ci.doFinal(input);
    }


    public String getKeyId() {
        return keyId;
    }
}
