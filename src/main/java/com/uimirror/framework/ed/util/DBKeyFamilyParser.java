package com.uimirror.framework.ed.util;


import com.uimirror.framework.ed.model.KeyContextHolder;
import com.uimirror.framework.ed.model.KeyFamilyMetaInfo;
import com.uimirror.framework.ed.model.KeyWrapper;
import com.uimirror.framework.ed.provider.CipherContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import java.security.Provider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jayaram
 *         3/7/16.
 */
public class DBKeyFamilyParser {

    private static Logger LOG = LoggerFactory.getLogger(DBKeyFamilyParser.class);

    private DBKeyFamilyParser(){
        throw new UnsupportedOperationException();
    }
    public static final String CURRENT = "current";
    public static final String RESERVED_KEY_PREFIX_LENGTH = "reserved_version_length";
    public static final String KEY_SEPARATOR = ".";

    public static KeyContextHolder parse(KeyFamilyMetaInfo keyFamily, Provider provider) {
        LOG.debug("[START]- Parsing the keyInfo for configuration.");
        Assert.notNull(keyFamily, "Invalid Key config file.");

        Map<String, KeyWrapper> dbKeyMap = new ConcurrentHashMap<>();
        Map<String, Integer> dbKeyMetaMap = new ConcurrentHashMap<>();
        Map<String, ThreadLocal<Cipher>> cipherInstanceMap = new ConcurrentHashMap<>();

        keyFamily.getKeyMetaInfos().forEach( keyMetaInfo -> {
            String dbKeyMapAlias = keyMetaInfo.getKeyAlias();
            keyMetaInfo.getIndividualKeyMetaInfos().forEach(individualKeyMetaInfo -> {

                String fixedSalt = StringUtils.hasText(individualKeyMetaInfo.getFixedSalt()) ? individualKeyMetaInfo.getFixedSalt() : keyMetaInfo.getFixedSalt();
                KeyWrapper key = new KeyWrapper.KeyWrapperBuilder(individualKeyMetaInfo.getVersion())
                        .withCipher(individualKeyMetaInfo.getCipherName())
                        .withInitialVectorLength(keyMetaInfo.getInitialVectorLength())
                        .withKey(individualKeyMetaInfo.getKey())
                        .withFixedSalt(fixedSalt)
                        .build();
                //Put the cipher instance also
                cipherInstanceMap.putIfAbsent(key.getCipherName(), ThreadLocal.withInitial(() -> CipherContextBuilder.getCipher(key.getCipherName(), provider)));
                dbKeyMap.put(dbKeyMapAlias + KEY_SEPARATOR + key.getKeyVersion(), key);
                if (keyMetaInfo.getCurrentVersion().equals(key.getKeyVersion())) {
                    dbKeyMap.put(dbKeyMapAlias + KEY_SEPARATOR + CURRENT, key);
                }
            });
            dbKeyMetaMap.put(dbKeyMapAlias + KEY_SEPARATOR + RESERVED_KEY_PREFIX_LENGTH, keyMetaInfo.getReservedPrefixLength());
        });
        LOG.debug("[END]- Parsing the keyInfo for configuration.");
        return new KeyContextHolder(dbKeyMap, dbKeyMetaMap, cipherInstanceMap);
    }

}
