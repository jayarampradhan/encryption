package com.uimirror.framework.ed.service;


/**
 * Singleton implementation to avoid hibernate type injection
 * @author Jayaram
 *         3/16/16.
 */
public class LocalEncryptionServiceUtil {

    private static LocalEncryptionService LocalEncryptionService;

    public LocalEncryptionServiceUtil(LocalEncryptionService LocalEncryptionService){
        LocalEncryptionServiceUtil.LocalEncryptionService = LocalEncryptionService;
    }

    public static byte[] encryptByteArray(String keyName, byte[] clearValue, boolean useCurrentKey, boolean useRandomSalt){
        return LocalEncryptionServiceUtil.LocalEncryptionService.encryptByteArray(keyName, clearValue, useCurrentKey, useRandomSalt);
    }

    public static byte[] encryptByteArray(String keyName, byte[] clearValue, boolean useRandomSalt){
        return LocalEncryptionServiceUtil.LocalEncryptionService.encryptByteArray(keyName, clearValue, useRandomSalt);
    }

    public static byte[] encryptByteArray(String keyName, byte[] clearValue){
        return LocalEncryptionServiceUtil.LocalEncryptionService.encryptByteArray(keyName, clearValue);
    }

    public static byte[] decryptByteArray(String keyName, byte[] cipherValue){
        return LocalEncryptionServiceUtil.LocalEncryptionService.decryptByteArray(keyName, cipherValue);
    }

    public static byte[] hashString(String clearValue){
        return LocalEncryptionServiceUtil.LocalEncryptionService.hashString(clearValue);
    }


}
