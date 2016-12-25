package com.uimirror.framework.ed.service;


import net.jodah.concurrentunit.Waiter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * @author Jayaram
 *         3/8/16.
 */
public class LocalEncryptionMultiThreadServiceTest {

    private static LocalEncryptionService localEncryptionService;
    private final Waiter waiter = new Waiter();

    @BeforeClass
    public static void setUp() throws Exception {
        localEncryptionService = new DefaultLocalEncryptionServiceImpl();
        localEncryptionService.setConfigLoc("classpath:/sample_key_info.json");
        localEncryptionService.init();
    }

    @Test
    public void testSingleConfMultiEncryption() throws Exception {

        for (int i = 0; i < 150; i++) {
            SingleConfMultiClientRunnable singleClientMultiThreadRunnable = new SingleConfMultiClientRunnable(localEncryptionService);
            new Thread(singleClientMultiThreadRunnable).start();

        }
        waiter.await(0, 150);
    }

    private class SingleConfMultiClientRunnable implements Runnable {
        private LocalEncryptionService localEncryptionService;

        public SingleConfMultiClientRunnable(LocalEncryptionService localEncryptionService) {
            this.localEncryptionService = localEncryptionService;
        }

        @Override
        public void run() {
            boolean useFirst = Boolean.TRUE;

            for (int i = 0; i < 1000; i++) {
                String raw = UUID.randomUUID().toString();
                String keyName;
                if(useFirst){
                    keyName = "1";
                    useFirst = Boolean.FALSE;
                }else {
                    keyName = "2";
                    useFirst = Boolean.TRUE;
                }
                SecureRandom random = new SecureRandom();
                byte clearValueBytes[] = new byte[1024];
                random.nextBytes(clearValueBytes);
                byte[] encryptByteArray = localEncryptionService.encryptByteArray("common.key_family_"+keyName+".shard1", raw.getBytes());
                byte[] decryptByteArray = localEncryptionService.decryptByteArray("common.key_family_"+keyName+".shard1", encryptByteArray);
                // Assert that the encryption and decryption are matching.
                try {
                    waiter.assertEquals(new String(decryptByteArray, "UTF-8"), raw);
                } catch (UnsupportedEncodingException e) {
                    waiter.fail(e);
                }
            }
            waiter.resume();
        }
    }

}
