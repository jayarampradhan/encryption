package com.uimirror.framework.ed.model;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Stores the Key and Secret Key with version.
 * @author Jayaram
 *         2/25/16.
 */
public class KeyWrapper {

    private byte[] key;
    private SecretKey secretKey;
    private String keyVersion;
    private String cipherName;
    private String fixedSalt;
    private int initialVectorLength;

    private KeyWrapper(KeyWrapperBuilder builder) {
        this.key = builder.keyInBytes;
        this.secretKey = builder.secretKey;
        this.keyVersion = builder.keyVersion;
        this.cipherName = builder.cipherName;
        this.fixedSalt = builder.fixedSalt;
        this.initialVectorLength = builder.initialVectorLength;
    }

    public byte[] getKey() {
        return key;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public String getKeyVersion() {
        return keyVersion;
    }

    public String getCipherName() {
        return cipherName;
    }

    public String getFixedSalt() {
        return fixedSalt;
    }

    public int getInitialVectorLength() {
        return initialVectorLength;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .appendSuper (super.toString())
                .append("keyVersion", keyVersion)
                .append("cipherName", cipherName)
                .append("fixedSalt", fixedSalt)
                .append("initialVectorLength", initialVectorLength)
                .toString();
    }

    /**
     * Checks if all required information are present or not.
     * @return <code>true</code> if key is valid else <code>false</code>
     */
    public boolean isValid(){
        return key != null && secretKey != null && keyVersion != null;
    }

    public static class KeyWrapperBuilder{
        //Which is essentially key name
        private String keyVersion;
        private String key;
        private String cipherName;
        private byte[] keyInBytes;
        private SecretKey secretKey;
        private String fixedSalt;
        private int initialVectorLength;
        private static final Logger LOG = LoggerFactory.getLogger(KeyWrapperBuilder.class);

        public KeyWrapperBuilder(String keyVersion){
            this.keyVersion = keyVersion;
        }

        public KeyWrapperBuilder withKey(String key){
            Assert.hasText("There is no key info for To Build a database encryption/decryption key from the specified file loc.");
            this.key = key;
            return this;
        }

        public KeyWrapperBuilder withCipher(String cipherName){
            if(StringUtils.isBlank(cipherName)){
                throw new IllegalArgumentException("There is no key info for To Build a database encryption/decryption key from the specified Cipher Algo.");
            }
            this.cipherName = cipherName;
            return this;
        }

        public KeyWrapperBuilder withFixedSalt(String salt){
            this.fixedSalt = salt;
            return this;
        }

        public KeyWrapperBuilder withInitialVectorLength(int vectorLength){
            this.initialVectorLength = vectorLength;
            return this;
        }

        public KeyWrapper build(){
            this.keyInBytes = decodeKey();
            this.secretKey = buildKey();
            return new KeyWrapper(this);
        }

        private byte[] decodeKey(){
            return Base64.decode(this.key.getBytes());
        }

        private SecretKey buildKey() {
            if (this.keyInBytes == null) {
                return null;
            }
            try {
                SecretKey key = new SecretKeySpec(this.keyInBytes, cipherName);
                LOG.debug("Service successfully unlocked.");
                return key;
            } catch (Exception e) {
                LOG.error("unexpected exception", e);
                return null;
            }
        }
    }
}
