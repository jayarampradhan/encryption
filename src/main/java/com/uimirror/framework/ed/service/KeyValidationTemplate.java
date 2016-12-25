package com.uimirror.framework.ed.service;


import com.uimirror.framework.ed.model.KeyContextHolder;
import com.uimirror.framework.ed.model.KeyValidationContext;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;

import static com.uimirror.framework.ed.util.DBKeyFamilyParser.CURRENT;
import static com.uimirror.framework.ed.util.DBKeyFamilyParser.KEY_SEPARATOR;

/**
 * Helps to validate the keys before library starts using it.
 * This is a Template Desgin pattern to make sure, user should provide a implemntation of the {@link KeyValidationDataExtractor}
 * This does the below functionality:
 * <ol>
 *     <li>Gets the extracted data from DB</li>
 *     <li>Decrypt the retrieved value</li>
 *     <li>Tries to encrypt the clear text with the key family and version, in case of current it uses only current key</li>
 *     <li>Checks if the encrypted text and DB extracted values are same</li>
 * </ol>
 *
 * This has assumption that user is saving the Data using {@link Base64#encode(byte[])}
 *
 * In case marked only validation for the current key, it does validation of the current key only, ignoring all other available keys.
 *
 * @author Jayaram
 *         3/8/16.
 */
public abstract class KeyValidationTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(KeyValidationTemplate.class);

    //template method, final so subclasses can't override
    public final void validateKeys(KeyContextHolder keyContextHolder, boolean validateOnlyCurrent){
        LOG.debug("[START]- Validating the Keys");
        keyContextHolder.getDbKeyMap().forEach((keyName, keyWrapper) -> {
            KeyFamilyHolder keyFamilyHolder = parseKeyFamily(keyName, validateOnlyCurrent);
            if(validateOnlyCurrent){
                if(keyFamilyHolder.isCurrent){
                    validateKey(keyFamilyHolder);
                }
            }else if(!CURRENT.equalsIgnoreCase(keyFamilyHolder.version)){
                validateKey(keyFamilyHolder);
            }
        });
        LOG.debug("[END]- Key Validation Completed");
    }

    private KeyFamilyHolder parseKeyFamily(String keyFamily, boolean withCurrentVersion) {
        String version = keyFamily.substring(keyFamily.lastIndexOf(KEY_SEPARATOR) + 1, keyFamily.length());
        String keyFamilyName = keyFamily.substring(0,keyFamily.lastIndexOf(KEY_SEPARATOR));
        KeyFamilyHolder keyFamilyHolder = new KeyFamilyHolder();
        keyFamilyHolder.keyFamilyName = keyFamilyName;
        if(withCurrentVersion && CURRENT.equalsIgnoreCase(version)){
            keyFamilyHolder.isCurrent = Boolean.TRUE;
        }
        keyFamilyHolder.version = version;
        return keyFamilyHolder;
    }

    public void validateKey(KeyFamilyHolder keyFamilyHolder){
        LOG.debug("[INTERIM]- Extracting Data for key family {}", keyFamilyHolder.keyFamilyName);
        KeyValidationContext data = getExtractor().getData(keyFamilyHolder.keyFamilyName, keyFamilyHolder.version);
        String decryptedValue = getDecryptedValue(keyFamilyHolder.keyFamilyName, data.getEncrypted());
        LOG.debug("[INTERIM]- Matching the data for the key family {}", keyFamilyHolder.keyFamilyName);
        Assert.isTrue( data.getRaw().equals(decryptedValue), "Key :"+keyFamilyHolder.keyFamilyName+ ", Version: "+keyFamilyHolder.version+" Is Invalid.");
        LOG.info("[INTERIM]- Decryption Key is valid for {} with version {}", keyFamilyHolder.keyFamilyName, keyFamilyHolder.version);
        try {
            LOG.debug("[INTERIM]- Doing Encryption Key validation for {} with version {}", keyFamilyHolder.keyFamilyName, keyFamilyHolder.version);
            String encryptedValue;
            String encryptKeyFamily = keyFamilyHolder.keyFamilyName;
            if(!keyFamilyHolder.isCurrent){
                encryptKeyFamily = keyFamilyHolder.keyFamilyName+KEY_SEPARATOR+keyFamilyHolder.version;
            }
            encryptedValue = getEncryptedValue(encryptKeyFamily, data.getRaw(), keyFamilyHolder.isCurrent);
            final String pseduoDecryption = getDecryptedValue(keyFamilyHolder.keyFamilyName, Base64.decode(encryptedValue));
            Assert.isTrue(pseduoDecryption.equals(data.getRaw()), "Not a Valid key for encryption");
            LOG.info("[INTERIM]- Encryption Key is valid for {} with version {}", keyFamilyHolder.keyFamilyName, keyFamilyHolder.version);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Not a valid Key", e);
        }
    }

    protected abstract String getDecryptedValue(String keyName, byte[] encrypted);

    protected abstract String getEncryptedValue(String keyName, String clearValue, boolean useCurrentKey) throws UnsupportedEncodingException;

    private static class KeyFamilyHolder {
        private String keyFamilyName;
        private String version;
        private boolean isCurrent;
    }

    protected abstract KeyValidationDataExtractor getExtractor();

}
