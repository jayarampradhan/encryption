package com.uimirror.framework.ed.util;


import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author Jayaram
 *         3/8/16.
 */
public class SampleKeyFormation {

    @Test
    public void testLoadKey() throws Exception {
        File file = new File("/deploy/data/keys/APP_SHARD1_DEV_0704");
        FileInputStream keyfis = new FileInputStream(file);
        int len = (int)file.length();
        BufferedReader br = new BufferedReader(new InputStreamReader(keyfis, Charset.forName("UTF-8")));
        String encodedKey = null;
        if ((encodedKey = br.readLine()) == null) {
            throw new IOException("Cannot read the key from " + file.getPath());
        }
        if (encodedKey.length() != len) {
            throw new IOException("Read " + encodedKey.length() + " != " + len + " bytes from " + file.getPath());
        }
        System.out.println(encodedKey);
        char[] chars = org.apache.commons.codec.binary.Hex.encodeHex(encodedKey.getBytes());
        System.out.println(chars);
        String s = new String(org.apache.commons.codec.binary.Hex.decodeHex(chars), "UTF-8");
        System.out.println(s);
        byte[] decodedKey = Base64.decode(s.getBytes());
        byte[] decodedEncKey = Base64.decode(encodedKey.getBytes());
        System.out.println(new String(decodedKey, "UTF-8"));
        boolean equals = Arrays.equals(Hex.decodeHex(Hex.encodeHex(Base64.decode("Ty66chaeN1BVwSnvGzb6lg=="))), Base64.decode("Ty66chaeN1BVwSnvGzb6lg=="));
        System.out.println(equals);

    }
}
