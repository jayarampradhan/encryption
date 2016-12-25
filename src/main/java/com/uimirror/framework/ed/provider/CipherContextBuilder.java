package com.uimirror.framework.ed.provider;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.Provider;

/**
 * Helps to build the {@link Cipher} based on {@link Provider}
 * @author Jayaram
 *         2/25/16.
 */
public class CipherContextBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(CipherContextBuilder.class);
    private CipherContextBuilder(){
        throw new UnsupportedOperationException();
    }

    public static Cipher getCipher(String cipherName, Provider provider){
        if(null == cipherName || null == provider){
            throw new IllegalArgumentException("Can't Build a Cipher");
        }
        Cipher cipher = null;
        try {
            LOG.debug("Creating a Cipher object");
            synchronized (CipherContextBuilder.class) {
                cipher = Cipher.getInstance(cipherName, provider);
            }
        } catch (final Exception e) {
            LOG.error("Cipher initialValue : failure : ", e);
        }
        return cipher;
    }

}
