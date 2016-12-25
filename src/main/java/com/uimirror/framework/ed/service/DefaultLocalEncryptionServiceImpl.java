package com.uimirror.framework.ed.service;


import com.uimirror.framework.ed.model.KeyContextHolder;
import com.uimirror.framework.ed.model.KeyWrapper;
import com.uimirror.framework.ed.util.DBKeyFamilyParser;
import com.uimirror.framework.ed.util.DefaultConfigParser;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.*;

/**
 * A local encryption service, to do the encryption and decryption of the given information.
 * It expects a config file either in json or yml to configure the key details, which can be used
 * for the encryption or decryption.
 * <p>It Uses {@link SecureRandom} for generating the random salt</p>
 *
 * When specifying the #keyValidationRequired to <code>true</code>, make sure to provide an instance of
 * {@link KeyValidationDataExtractor} which will help to get the encrypted bytes to validate if the keys are correct.
 *
 * Make sure to call the #init() method after initializing the object,
 * which will help to configure the keys for encryption and decryption service.
 *
 * @author Jayaram
 *         2/25/16.
 */
public class DefaultLocalEncryptionServiceImpl extends KeyValidationTemplate implements LocalEncryptionService{

    private static final Logger LOG = LoggerFactory.getLogger(DefaultLocalEncryptionServiceImpl.class);

    private Provider provider;
    private static final Charset UTF_CHARSET = Charset.forName("UTF-8");
    private static final SecureRandom RANDOM_GEN = new SecureRandom();
    private KeyContextHolder keys;
    private String configLoc;
    private KeyValidationDataExtractor extractor;
    private boolean keyValidationRequired;
    private boolean validateOnlyCurrentKey;

    public DefaultLocalEncryptionServiceImpl() {
        addProvider(new BouncyCastleProvider());
        LOG.info("Don't Forget to call init method to complete key configurations.");
    }

    @Override
    public byte[] encryptByteArray(String keyName, byte[] clearValue, boolean useCurrentKey, boolean useRandomSalt) {
        if(clearValue==null)
            return null;
        Assert.hasText(keyName, "Invalid Key Name!!!");
        String name = getKeyName(keyName, useCurrentKey);
        KeyWrapper keyToUse = getRightKey(name);
        String salt = useRandomSalt ? null : keyToUse.getFixedSalt();

        try {
            String keyVersion = keyToUse.getKeyVersion();
            byte[] keyVersionInBytes = keyVersion.getBytes();
            byte[] cipherText = encrypt(keyToUse, null, clearValue, salt, keyToUse.getInitialVectorLength());
            byte[] finalResultBytes = new byte[keyVersionInBytes.length + cipherText.length];
            System.arraycopy(keyVersionInBytes, 0, finalResultBytes, 0, keyVersionInBytes.length);
            System.arraycopy(cipherText, 0, finalResultBytes, keyVersionInBytes.length, cipherText.length);
            return finalResultBytes;
        } catch (Exception e) {
            LOG.error("Unexpected exception while encrypting data: ", e);
            throw new RuntimeException("Cannot encrypt data",e);
        }
    }

    @Override
    public byte[] encryptByteArray(String keyName, byte[] clearValue, boolean useRandomSalt) {
        return encryptByteArray(keyName, clearValue, Boolean.TRUE, useRandomSalt);
    }

    @Override
    public byte[] encryptByteArray(String keyName, byte[] clearValue) {
        return encryptByteArray(keyName, clearValue, Boolean.TRUE, Boolean.TRUE);
    }

    private String getKeyName(String keyName, boolean useCurrentKey) {
        return useCurrentKey ? keyName+ DBKeyFamilyParser.KEY_SEPARATOR+DBKeyFamilyParser.CURRENT : keyName;
    }

    private byte[] encrypt(KeyWrapper keyToUse, SecretKey strongKey, byte[] clearValue, String salt, int initialVectorLength) throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] iv = new byte[initialVectorLength];
        if(null == salt) {
            RANDOM_GEN.nextBytes(iv);
        }else {
            iv = salt.getBytes(UTF_CHARSET);
        }
        SecretKey key = (strongKey == null ? keyToUse.getSecretKey() : strongKey);
        IvParameterSpec initVector = new IvParameterSpec(iv);
        Cipher cipher = getKeys().getCipher(keyToUse.getCipherName());
        cipher.init(Cipher.ENCRYPT_MODE, key, initVector);
        byte[] cipherText = cipher.doFinal(clearValue);
        return ArrayUtils.addAll(iv, cipherText);
    }

    @Override
    public byte[] strongEncryptByteArray(String keyName, SecretKey strongKey, byte[] clearValue, boolean useCurrentKey) {
        if(clearValue==null)
            return null;

        Assert.notNull(strongKey, "No Key/ Invalid Key has been provided for the encryption.");
        String name = getKeyName(keyName, useCurrentKey);
        KeyWrapper keyToUse = getRightKey(name);
        String keyVersion = keyToUse.getKeyVersion();

        try {
            byte[] cipherText = encrypt(keyToUse, strongKey, clearValue, null , keyToUse.getInitialVectorLength());
            byte[] finalResultBytes = new byte[keyVersion.getBytes().length + cipherText.length];
            System.arraycopy(keyVersion.getBytes(), 0, finalResultBytes, 0, keyVersion.getBytes().length);
            System.arraycopy(cipherText, 0, finalResultBytes, keyVersion.getBytes().length, cipherText.length);
            return finalResultBytes;
        } catch (Exception e) {
            LOG.error("Unexpected exception while encrypting data: ", e);
            throw new RuntimeException("Cannot encrypt data");
        }
    }

    @Override
    public byte[] strongEncryptByteArray(String keyName, SecretKey strongKey, byte[] clearValue) {
        return strongEncryptByteArray(keyName, strongKey, clearValue, Boolean.TRUE);
    }

    @Override
    public String determineKeyVersion(byte[] cipherValue, int keyPrefixLength) {
        if(cipherValue == null)
            return null; // we "decrypt" this special String into null
        if(cipherValue.length < keyPrefixLength)
            throw new IllegalArgumentException("Invalid size of encrypted text: "+cipherValue.length+"!");
        byte[] keyName = new byte[keyPrefixLength];
        System.arraycopy(cipherValue, 0, keyName, 0, keyPrefixLength);
        String keyVersion;
        try {
            keyVersion = new String(keyName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("Unable to determine the key version.", e);
            throw new IllegalArgumentException("Unable to determine Key version from the given cipher.", e);
        }
        return keyVersion;
    }

    @Override
    public byte[] decryptByteArray(String keyName, byte[] cipherValue) {
        if(cipherValue == null)
            return null; // we "decrypt" this special String into null
        int reservedKeyPrefixLength = getRightKeyReservedLength(keyName);
        String keyVersion = determineKeyVersion(cipherValue, reservedKeyPrefixLength);
        String name = getKeyName(keyName+ DBKeyFamilyParser.KEY_SEPARATOR + keyVersion, Boolean.FALSE);
        KeyWrapper keyToUse = getRightKey(name);

        // the 1st 3 characters contain the key name, in clear text; extract it:
        if(cipherValue.length < reservedKeyPrefixLength)
            throw new RuntimeException("Invalid size of encrypted text: "+cipherValue.length+"!");
        try {
            // Remove the key name from the cipherText:
            byte[] cipherBytes = new byte[cipherValue.length - reservedKeyPrefixLength];
            System.arraycopy(cipherValue, reservedKeyPrefixLength, cipherBytes, 0, cipherValue.length - reservedKeyPrefixLength);
            return decrypt(keyToUse, null, cipherBytes, keyToUse.getInitialVectorLength());
        } catch (Exception e) {
            LOG.error("Unexpected exception while decrypting data: ", e);
            throw new RuntimeException("Cannot decrypt data");
        }
    }

    private byte[] decrypt(KeyWrapper keyToUse, SecretKey strongKey, byte[] cipherBytes, int initialVectorLength) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] iv = new byte[initialVectorLength];
        if (cipherBytes.length <= initialVectorLength)
            throw new RuntimeException("Invalid size of encrypted text byte-array: "+cipherBytes.length+"!");

        SecretKey key = (strongKey == null ? keyToUse.getSecretKey() : strongKey);
        System.arraycopy(cipherBytes, 0, iv, 0, initialVectorLength);
        // No salt, add the salt length back.
        // Extract the iv bytes from the 1st 16 bytes:
        IvParameterSpec initVector = new IvParameterSpec(iv, 0, initialVectorLength);

        Cipher cipher = getKeys().getCipher(keyToUse.getCipherName());
        cipher.init(Cipher.DECRYPT_MODE, key, initVector);

        // Decrypt the bytes after the 16 first bytes (which are the iv bytes):
        return cipher.doFinal(cipherBytes, initialVectorLength, cipherBytes.length - initialVectorLength);
    }

    @Override
    public byte[] strongDecryptByteArray(String keyName, SecretKey strongKey, byte[] cipherValue) {
        if(cipherValue==null)
            return null;
        Assert.notNull(strongKey, "No Key/ Invalid Key has been provided for the Decryption.");
        int reservedKeyPrefixLength = getRightKeyReservedLength(keyName);
        String keyVersion = determineKeyVersion(cipherValue, reservedKeyPrefixLength);
        String name = getKeyName(keyName+ "." + keyVersion, Boolean.FALSE);
        KeyWrapper keyToUse = getRightKey(name);
        try {
            // the 1st 3 characters contain the key name, in clear text; extract it:
            if(cipherValue.length < reservedKeyPrefixLength)
                throw new RuntimeException("Invalid size of encrypted text: "+cipherValue.length+"!");
            // Remove the key name from the cipherText:
            byte[] cipherBytes = new byte[cipherValue.length - reservedKeyPrefixLength];
            System.arraycopy(cipherValue, reservedKeyPrefixLength, cipherBytes, 0, cipherValue.length - reservedKeyPrefixLength);
            return decrypt(keyToUse, strongKey, cipherBytes, keyToUse.getInitialVectorLength());
        } catch (Exception e) {
            LOG.error("Unexpected exception while decrypting data: ", e);
            throw new RuntimeException("Cannot decrypt data");
        }
    }

    @Override
    public byte[] hashString(String clearValue) {
        if(clearValue==null)
            return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            return digest.digest(clearValue.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOG.error("Unexpected exception while hashing data: ", e);
            throw new RuntimeException("Cannot hash data");
        }
    }

    private KeyWrapper getRightKey(String keyName){
        KeyWrapper keyWrapper = getKeys().getDbKeyMap().get(keyName);
        Assert.notNull(keyWrapper, "Invalid Key Name");
        return keyWrapper;
    }

    private int getRightKeyReservedLength(String keyName){
        Integer length = getKeys().getDbKeyMeta().get(keyName+DBKeyFamilyParser.KEY_SEPARATOR+DBKeyFamilyParser.RESERVED_KEY_PREFIX_LENGTH);
        return length == null ? 0: length;
    }

    /**
     * Adds The provider to the Current Security Context.
     * @param provider {@link Provider}
     */
    private void addProvider(Provider provider){
        this.provider = provider;
        Security.addProvider(provider);
    }
    @Override
    public Provider getProvider() {
        return provider;
    }

    @Override
    public void setProvider(Provider provider) {
        Security.removeProvider(this.provider.getName());
        addProvider(provider);
    }

    public KeyContextHolder getKeys() {
        return this.keys;
    }

    @Override
    protected String getDecryptedValue(String keyName, byte[] encrypted) {
        byte[] decryptBytes = decryptByteArray(keyName, encrypted);
        return new String(decryptBytes);
    }

    @Override
    protected String getEncryptedValue(String keyName, String clearValue, boolean useCurrentKey) throws UnsupportedEncodingException {
        return new String(Base64.encode(encryptByteArray(keyName, clearValue.getBytes(), useCurrentKey, Boolean.TRUE)), "UTF-8");
    }

    @Override
    protected KeyValidationDataExtractor getExtractor() {
        return this.extractor;
    }

    public String getConfigLoc() {
        return configLoc;
    }

    @Override
    public void setConfigLoc(String configLoc) {
        this.configLoc = configLoc;
    }

    @Override
    public void setExtractor(KeyValidationDataExtractor extractor) {
        this.extractor = extractor;
    }

    public boolean isKeyValidationRequired() {
        return keyValidationRequired;
    }

    @Override
    public void setKeyValidationRequired(boolean keyValidationRequired) {
        this.keyValidationRequired = keyValidationRequired;
    }

    public boolean isValidateOnlyCurrentKey() {
        return validateOnlyCurrentKey;
    }

    public void setValidateOnlyCurrentKey(boolean validateOnlyCurrentKey) {
        this.validateOnlyCurrentKey = validateOnlyCurrentKey;
    }

    public void init(){
        LOG.info("[START]- Initializing key configurations for encryption library.");
        Assert.hasText(getConfigLoc(), "No Configuration file specified for the key context.");
        this.keys = new DefaultConfigParser().parse(getConfigLoc(), getProvider());
        Assert.notNull(this.keys, "Keys are not Initialized properly, check your configurations");
        if(isKeyValidationRequired()){
            Assert.notNull(this.getExtractor(), "Key Data Extractor can't be null when key validation is required.");
            validateKeys(this.keys, isValidateOnlyCurrentKey());
        }
        LOG.info("[END]- Key Configuration has been completed.");
    }

}

