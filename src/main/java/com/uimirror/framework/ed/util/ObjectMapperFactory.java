package com.uimirror.framework.ed.util;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * @author Jayaram
 *         3/7/16.
 */
public class ObjectMapperFactory {

    private ObjectMapperFactory() {
        throw new UnsupportedOperationException();
    }

    public static ObjectMapper getMapper(){
        return createCustomMapper(new ObjectMapper());
    }

    public static ObjectMapper getYMLMapper(){
        return createCustomMapper(new ObjectMapper(new YAMLFactory()));
    }

    private static ObjectMapper createCustomMapper(ObjectMapper mapper) {
        mapper
            .configure(SerializationFeature.WRAP_ROOT_VALUE, false)
            .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .setAnnotationIntrospector(createJaxbJacksonAnnotationIntrospector());
        return mapper;
    }

    private static AnnotationIntrospector createJaxbJacksonAnnotationIntrospector() {

        final AnnotationIntrospector jaxbIntrospector = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
        final AnnotationIntrospector jacksonIntrospector = new JacksonAnnotationIntrospector();

        return AnnotationIntrospector.pair(jacksonIntrospector, jaxbIntrospector);
    }

}
