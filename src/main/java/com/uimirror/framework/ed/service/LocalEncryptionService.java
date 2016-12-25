package com.uimirror.framework.ed.service;


import com.uimirror.framework.ed.model.KeySupplement;
import com.uimirror.framework.ed.model.KeyWrapper;

import javax.crypto.SecretKey;
import java.security.Provider;
import java.security.SecureRandom;

/**
 *
 * @author Jayaram
 *         2/25/16.
 */

public interface LocalEncryptionService {

    /**
     * Encrypts the given byte information using the Security provider and {@link KeyWrapper}.
     *
     * <p>If <code>salt == null</code> then it will fall back to the {@link SecureRandom} to generate the key</p>
     *
     * @param keyName which will help to find {@link KeyWrapper}
     * @param clearValue text to be encrypted
     * @param useCurrentKey suggests to use current key or not
     * @param useRandomSalt suggests to use random salt or fixed salt
     * @return encrypted bytes
     */
    byte[] encryptByteArray(String keyName, byte[] clearValue, boolean useCurrentKey, boolean useRandomSalt);

    /**
     * Encrypts the given byte information using the Security provider and {@link KeyWrapper}.
     *
     * <p>If <code>salt == null</code> then it will fall back to the {@link SecureRandom} to generate the key</p>
     * This will use the current key to encrypt
     *
     * @param keyName which will help to find {@link KeyWrapper}
     * @param clearValue text to be encrypted
     * @param useRandomSalt suggests to use random salt or fixed salt
     * @return encrypted bytes
     */
    byte[] encryptByteArray(String keyName, byte[] clearValue, boolean useRandomSalt);

    /**
     * Encrypts the given byte information using the Security provider and {@link KeyWrapper}.
     *
     * <p>If <code>salt == null</code> then it will fall back to the {@link SecureRandom} to generate the key</p>
     * This will use the current key and random salt to encrypt
     *
     * @param keyName which will help to find {@link KeyWrapper}
     * @param clearValue text to be encrypted
     * @return encrypted bytes
     */
    byte[] encryptByteArray(String keyName, byte[] clearValue);

    /**
     * Encrypts the given byte information using the security context and secondary key.
     * @param keyName which will help to find {@link KeyWrapper}
     * @param strongKey {@link SecretKey} will be used for additional security
     * @param clearValue text to be encrypted
     * @param useCurrentKey suggests to use current key or not
     * @return encrypted bytes
     */
    byte[] strongEncryptByteArray(String keyName, SecretKey strongKey, byte[] clearValue, boolean useCurrentKey);

    /**
     * Encrypts the given byte information using the security context and secondary key.
     * It Uses the current Key
     *
     * @param keyName which will help to find {@link KeyWrapper}
     * @param strongKey {@link SecretKey} will be used for additional security
     * @param clearValue text to be encrypted
     * @return encrypted bytes
     */
    byte[] strongEncryptByteArray(String keyName, SecretKey strongKey, byte[] clearValue);


    /**
     * As Encrypted stream has the version prefix at first hence its always safe to determine the key version and based on that
     * choose the secret key for decryption.
     * @param cipherValue encrypted text from where version will be extracted.
     * @param keyPrefixLength length of the version.
     * @return version of the key.
     */
    String determineKeyVersion(byte[] cipherValue, int keyPrefixLength);

    /**
     * Decrypts the given byte array using the given key
     * @param keyName which will help to find {@link KeyWrapper}
     * @param cipherValue will be decrypted
     * @return Decrypted byte information
     */
    byte[] decryptByteArray(String keyName, byte[] cipherValue);

    /**
     * Decrypts the given byte array using the given key and a supplement key
     * @param keyName which will help to find {@link KeyWrapper}
     * @param strongKey {@link KeySupplement} will be used for additional security
     * @param cipherValue will be decrypted
     * @return Decrypted byte information
     */
    byte[] strongDecryptByteArray(String keyName, SecretKey strongKey, byte[] cipherValue);

    /**
     * Hash the given text.
     * @param clearValue will be hashed.
     * @return hashed byte information.
     */
    byte[] hashString(String clearValue);

    Provider getProvider();

    void setProvider(Provider provider);

    /**
     * Configuration location for the key building
     * @param configLoc location of the configuration file.
     */
    void setConfigLoc(String configLoc);

    /**
     * While key validation is marked true, then extractor definition is required to retrieve data.
     * @param extractor which will be used for the data retrieval.
     */
    void setExtractor(KeyValidationDataExtractor extractor);

    void setKeyValidationRequired(boolean keyValidationRequired);

    void init();
}
