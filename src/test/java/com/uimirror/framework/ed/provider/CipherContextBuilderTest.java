package com.uimirror.framework.ed.provider;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import javax.crypto.Cipher;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jayaram
 *         6/18/16.
 */
public class CipherContextBuilderTest {
    @Test(expected = UnsupportedOperationException.class)
    public void testPrivateConstructors() throws Exception{
        final Constructor<CipherContextBuilder> constructor = CipherContextBuilder.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch (InvocationTargetException e) {
            throw (UnsupportedOperationException) e.getTargetException();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetCipherWhenCipherNameIsNull() throws Exception {
        CipherContextBuilder.getCipher(null, new BouncyCastleProvider());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetCipherWhenProviderIsNull() throws Exception {
        CipherContextBuilder.getCipher("AES", null);
    }


    @Test
    public void testGetCipherWhenInvalidCipherName() throws Exception {
        final Cipher cipher = CipherContextBuilder.getCipher("AES2", new BouncyCastleProvider());
        assertThat(cipher).isNull();
    }

    @Test
    public void testGetCipherWhenValidCipherName() throws Exception {
        final Cipher cipher = CipherContextBuilder.getCipher("AES", new BouncyCastleProvider());
        assertThat(cipher).isNotNull();
    }


}
