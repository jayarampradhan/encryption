package com.uimirror.framework.ed.util;


import com.uimirror.framework.ed.model.KeyContextHolder;

import java.security.Provider;

/**
 * @author Jayaram
 *         3/7/16.
 */
public interface ConfigParser {

    KeyContextHolder parse(String configLoc, Provider provider);
}
