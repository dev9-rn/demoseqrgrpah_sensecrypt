package com.sssl.seqrgraphdemo.models;

public class Data {

    private byte[] content;
    private boolean isEncrypted;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public Data(byte[] data) {
        this.content = data;
    }
}
