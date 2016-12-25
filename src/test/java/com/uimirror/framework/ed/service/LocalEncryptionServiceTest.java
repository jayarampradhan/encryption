package com.uimirror.framework.ed.service;


import org.bouncycastle.util.encoders.Base64;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jayaram
 *         3/8/16.
 */
public class LocalEncryptionServiceTest {

    private static LocalEncryptionService localEncryptionService;

    @BeforeClass
    public static void setUp() throws Exception {
        localEncryptionService = new DefaultLocalEncryptionServiceImpl();
        localEncryptionService.setConfigLoc("classpath:/sample_key_info.json");
        localEncryptionService.init();
    }

    @Test
    public void testEncryption() throws Exception {
        String clearValue = "HelloWorld";
        byte[] encryptByteArray = localEncryptionService.encryptByteArray("common.key_family_1.shard1", clearValue.getBytes());
        byte[] decryptByteArray = localEncryptionService.decryptByteArray("common.key_family_1.shard1", encryptByteArray);
        assertThat(new String(decryptByteArray, "UTF-8")).isEqualTo(clearValue);
    }

    @Test
    public void testEncryptionWhenNullInput() throws Exception {
        byte[] encryptByteArray = localEncryptionService.encryptByteArray("common.key_family_1.shard1", null);
        assertThat(encryptByteArray).isNull();
        final byte[] decryptByteArray = localEncryptionService.decryptByteArray("common.key_family_1.shard1", null);
        assertThat(decryptByteArray).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEncryptionWhenInvalidInput() throws Exception {
        String clearValue = "Some_sample_key_test";
        localEncryptionService.encryptByteArray("common.key_family_1.shard_not_found", clearValue.getBytes());
    }

    @Test
    public void testDecryptionWithBase64() throws Exception {

        byte[] decryptByteArray = localEncryptionService.decryptByteArray("common.key_family_3.shard1", Base64.decode("MDAyxNdZPXTwKGzV5hbgA2W9XoLydyd/NkAOu04Ppz4do4cCMiMHCNQ54Hlma1nbZ18N"));
        System.out.println(new String(decryptByteArray, "UTF-8"));
        assertThat(new String(decryptByteArray, "UTF-8")).isEqualTo("Key Validator Clear Test Value");
    }

    @Test
    public void testEncryptionWithDifferentVersion() throws Exception {
        String clearValue = "HelloWorld";
        byte[] encryptByteArray = localEncryptionService.encryptByteArray("common.key_family_1.shard1.002", clearValue.getBytes(), Boolean.FALSE, Boolean.FALSE);
        byte[] decryptByteArray = localEncryptionService.decryptByteArray("common.key_family_1.shard1", encryptByteArray);
        assertThat(new String(decryptByteArray, "UTF-8")).isEqualTo(clearValue);
    }

    @Test
    public void testEncryptionWithRandomSalt() throws Exception {
        Long clearValue = 1234l;
        byte[] bytes = localEncryptionService.hashString(String.valueOf(clearValue));
        byte[] encryptByteArray = localEncryptionService.encryptByteArray("common.key_family_1.shard1", bytes, Boolean.TRUE);
        byte[] decryptByteArray = localEncryptionService.decryptByteArray("common.key_family_1.shard1", encryptByteArray);
        String s = DatatypeConverter.printHexBinary(decryptByteArray);
        assertThat(DatatypeConverter.printHexBinary(bytes)).isEqualTo(s);
    }

}
