package com.uimirror.framework.ed.util;


import com.uimirror.framework.ed.model.KeyContextHolder;
import com.uimirror.framework.ed.model.KeyFamilyMetaInfo;
import org.assertj.core.api.Assertions;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Jayaram
 *         3/7/16.
 */
public class DBKeyFamilyParserTest {

    @Test
    public void testJsonConfigParser() throws Exception {
        KeyFamilyMetaInfo dbKeyFamily = ObjectMapperFactory.getMapper().readValue(this.getClass().getResourceAsStream("/sample_key_info.json"), KeyFamilyMetaInfo.class);
        KeyContextHolder keyContextHolder = DBKeyFamilyParser.parse(dbKeyFamily, new BouncyCastleProvider());
        Assertions.assertThat(keyContextHolder).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJsonConfigParserWhenNull() throws Exception {
        DBKeyFamilyParser.parse(null, new BouncyCastleProvider());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPrivateConstructors() throws Exception{
        final Constructor<DBKeyFamilyParser> constructor = DBKeyFamilyParser.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch (InvocationTargetException e) {
            throw (UnsupportedOperationException) e.getTargetException();
        }
    }
}
