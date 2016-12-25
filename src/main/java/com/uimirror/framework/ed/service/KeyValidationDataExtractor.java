package com.uimirror.framework.ed.service;


import com.uimirror.framework.ed.model.KeyValidationContext;

/**
 * @author Jayaram
 *         3/9/16.
 */
public interface KeyValidationDataExtractor {
    KeyValidationContext getData(String keyFamily, String version);
}
