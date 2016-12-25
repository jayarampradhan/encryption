package com.uimirror.framework.ed.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jayaram
 *         6/18/16.
 */
public class ObjectMapperFactoryTest {



    @Test(expected = UnsupportedOperationException.class)
    public void testPrivateConstructors() throws Exception{
        final Constructor<ObjectMapperFactory> constructor = ObjectMapperFactory.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch (InvocationTargetException e) {
            throw (UnsupportedOperationException) e.getTargetException();
        }
    }

    @Test
    public void testValidMapper() throws Exception {
        final ObjectMapper mapper = ObjectMapperFactory.getMapper();
        assertThat(mapper.getSerializationConfig().isEnabled(SerializationFeature.INDENT_OUTPUT)).isTrue();
        assertThat(mapper.getSerializationConfig().isEnabled(SerializationFeature.WRAP_ROOT_VALUE)).isFalse();
        final ObjectMapper ymlMapper = ObjectMapperFactory.getYMLMapper();
        assertThat(ymlMapper).isNotNull();
    }
}
