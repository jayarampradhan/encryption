package com.uimirror.framework.ed.model;


/**
 * @author Jayaram
 *         3/9/16.
 */
public class KeyValidationContext {
    private String raw;
    private byte[] encrypted;

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public byte[] getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(byte[] encrypted) {
        this.encrypted = encrypted;
    }
}
