package com.uimirror.framework.ed.service;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jayaram
 *         6/18/16.
 */
public class LocalEncryptionServiceUtilTest {
    private static LocalEncryptionService localEncryptionService;

    @BeforeClass
    public static void setUpOnce() throws Exception {
        localEncryptionService = new DefaultLocalEncryptionServiceImpl();
        localEncryptionService.setConfigLoc("classpath:/sample_key_info.json");
        localEncryptionService.init();
    }

    @Before
    public void setUp() throws Exception {
        new LocalEncryptionServiceUtil(localEncryptionService);
    }

    @Test
    public void testEncryption() throws Exception {
        String clearValue = "HelloWorld";
        byte[] encryptByteArray = LocalEncryptionServiceUtil.encryptByteArray("common.key_family_1.shard1", clearValue.getBytes());
        byte[] decryptByteArray = LocalEncryptionServiceUtil.decryptByteArray("common.key_family_1.shard1", encryptByteArray);
        assertThat(new String(decryptByteArray, "UTF-8")).isEqualTo(clearValue);
    }


    @Test
    public void testEncryptionWithDifferentVersion() throws Exception {
        String clearValue = "HelloWorld";
        byte[] encryptByteArray = LocalEncryptionServiceUtil.encryptByteArray("common.key_family_1.shard1.002", clearValue.getBytes(), Boolean.FALSE, Boolean.FALSE);
        byte[] decryptByteArray = LocalEncryptionServiceUtil.decryptByteArray("common.key_family_1.shard1", encryptByteArray);
        assertThat(new String(decryptByteArray, "UTF-8")).isEqualTo(clearValue);
    }

    @Test
    public void testEncryptionWithRandomSalt() throws Exception {
        Long clearValue = 1234l;
        byte[] bytes = LocalEncryptionServiceUtil.hashString(String.valueOf(clearValue));
        byte[] encryptByteArray = LocalEncryptionServiceUtil.encryptByteArray("common.key_family_1.shard1", bytes, Boolean.TRUE);
        byte[] decryptByteArray = LocalEncryptionServiceUtil.decryptByteArray("common.key_family_1.shard1", encryptByteArray);
        String s = DatatypeConverter.printHexBinary(decryptByteArray);
        assertThat(DatatypeConverter.printHexBinary(bytes)).isEqualTo(s);
    }
}
