package com.uimirror.framework.ed.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * @author Jayaram
 *         2/25/16.
 */
public class KeySupplement implements Serializable {
    private String requestName;
    Serializable[] data;

    public KeySupplement(String requestName, Serializable... data) {
        this.requestName = requestName;
        this.data = data;
    }

    @Override
    public String toString() {  //$NON-NLS-L$
        return requestName + Arrays.asList(data);
    }


    public byte[] toBytes() {  //$NON-NLS-L$
        try {
            return toString().getBytes("UTF-8");  //$NON-NLS-L$
        } catch (UnsupportedEncodingException uee) {
            LOG.warn("",uee);
            throw new IllegalArgumentException(uee);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeySupplement that = (KeySupplement)o;
        if (!requestName.equals(that.requestName))
            return false;
        if (data.length != that.data.length)
            return false;
        for (int i = 0; i < data.length; i++) {
            Serializable myItem = data[i];
            Serializable thatItem = that.data[i];
            if ((myItem == null) != (thatItem == null))
                return false;
            if (myItem != null && !myItem.equals(thatItem))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = requestName.hashCode();
        for (int i = 0; i < data.length; i++)
            if (data[i] != null)
                result ^= data[i].hashCode();
            else
                result ^= i;
        return result;
    }

    private static final Logger LOG = LoggerFactory.getLogger(KeySupplement.class);
}
