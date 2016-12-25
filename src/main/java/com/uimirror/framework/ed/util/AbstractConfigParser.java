package com.uimirror.framework.ed.util;


import com.uimirror.framework.ed.model.KeyFamilyMetaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Helps to initialize the library from the configuration location specified.
 * Currently supports only json and yml as part of configuration parsing.
 *
 * @author Jayaram
 *         3/7/16.
 */
public abstract class AbstractConfigParser implements ConfigParser {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractConfigParser.class);

    /**
     * <pre>
     * Unix
     *
     * file://localhost/file_nme
     * file:///file_nme
     * Windows
     * file://localhost/drive|/file_path
     * file:///drive|/file_path
     * file://localhost/drive|/file_path
     * file:///drive:/file_path
     * </pre>
     * https://en.wikipedia.org/wiki/File_URI_scheme
     * else specify as classpath:/
     *
     * @param configLoc location either in uri format or classpath format
     * @return {@link KeyFamilyMetaInfo}
     */
    public KeyFamilyMetaInfo parseConfig(String configLoc){
        LOG.debug("[START]- Processing Configuration details from the path {}", configLoc);
        Assert.hasText(configLoc, "Invalid Config Location.");
        if(isJson(configLoc)){
            try {
                InputStream inputStream = getInputStream(configLoc);
                return ObjectMapperFactory.getMapper().readValue(inputStream, KeyFamilyMetaInfo.class);
            }catch (IOException e){
                LOG.error("[FAILED]- Accessing the Configuration file {}, has some issue.", configLoc, e);
                throw new IllegalArgumentException("Invalid Config Location.");
            }
        }else if(isYAML(configLoc)){
            try {
                return ObjectMapperFactory.getMapper().readValue(new DefaultResourceLoader().getResource(configLoc).getInputStream(), KeyFamilyMetaInfo.class);
            }catch (IOException e){
                LOG.error("[FAILED]- Accessing the Configuration file {}, has some issue.", configLoc, e);
                throw new IllegalArgumentException("Invalid Config Location.");
            }
        }else{
            LOG.warn("[FAILED]- The Configuration file {}, format is not supported, consider to give a json/yml config file.", configLoc);
            throw new IllegalArgumentException("Invalid Config File Format.");
        }
    }

    private InputStream getInputStream(String configLoc) throws IOException {
        InputStream inputStream;
        if(configLoc.contains("classpath:")){
            inputStream = new DefaultResourceLoader().getResource(configLoc).getInputStream();
        }else {
            URI uri = URI.create(configLoc);
            inputStream = new FileInputStream(new File(uri));
        }
        return inputStream;
    }

    private boolean isJson(String configLoc){
        String extension = configLoc.substring(configLoc.lastIndexOf(".")+1, configLoc.length());
        if("json".equalsIgnoreCase(extension)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    private boolean isYAML(String configLoc){
        String extension = configLoc.substring(configLoc.lastIndexOf(".")+1, configLoc.length());
        if("YAML".equalsIgnoreCase(extension)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
