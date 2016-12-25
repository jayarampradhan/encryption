package com.uimirror.framework.ed.model;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import java.util.Collections;
import java.util.Map;

/**
 * Will hold the key meta data information for the various db and key family.
 * @see KeyWrapper for more details on the key
 * @author Jayaram
 *         3/8/16.
 */
public class KeyContextHolder {

    private final Map<String, KeyWrapper> dbKeyMap;
    private final Map<String, Integer> dbKeyMeta;
    private final Map<String, ThreadLocal<Cipher>> cipherInstances;


    public KeyContextHolder(Map<String, KeyWrapper> dbKeyMap, Map<String, Integer> dbKeyMeta, Map<String, ThreadLocal<Cipher>> cipherInstances) {
        this.dbKeyMap = Collections.unmodifiableMap(dbKeyMap);
        this.dbKeyMeta = Collections.unmodifiableMap(dbKeyMeta);
        this.cipherInstances = Collections.unmodifiableMap(cipherInstances);
    }

    public Map<String, KeyWrapper> getDbKeyMap() {
        return dbKeyMap;
    }

    public Map<String, Integer> getDbKeyMeta() {
        return dbKeyMeta;
    }

    public Cipher getCipher(String cipherName){
        ThreadLocal<Cipher> cipherThreadLocal = cipherInstances.get(cipherName);
        Assert.notNull(cipherThreadLocal, "No Cipher Found.");
        return cipherThreadLocal.get();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .appendSuper (super.toString())
                .append("dbKeyMap", dbKeyMap)
                .append("dbKeyMeta", dbKeyMeta)
                .toString();
    }
}
