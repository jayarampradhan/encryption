package com.uimirror.framework.ed.service;


import com.uimirror.framework.ed.model.KeyContextHolder;
import com.uimirror.framework.ed.model.KeyValidationContext;
import com.uimirror.framework.ed.model.KeyWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

/**
 * @author Jayaram
 *         6/18/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class KeyValidationTemplateTest {

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private KeyValidationTemplate keyValidationTemplate;
    @Mock
    private KeyContextHolder keyContextHolder;
    @Mock
    private KeyWrapper keyWrapper;
    @Mock
    private KeyValidationDataExtractor extractor;
    @Mock
    private KeyValidationContext context;

    @Test
    public void testValidateKey() throws Exception {
        Map<String, KeyWrapper> keyWrapperMap = new HashMap<>();
        keyWrapperMap.put("common.key_family_1.shard1",keyWrapper);
        keyWrapperMap.put("common.key_family_1.shard1.current",keyWrapper);
        doReturn(keyWrapperMap).when(keyContextHolder).getDbKeyMap();
        doReturn(extractor).when(keyValidationTemplate).getExtractor();
        doReturn(context).when(extractor).getData(anyString(), anyString());
        doReturn("Hello").when(keyValidationTemplate).getDecryptedValue(anyString(), any());
        doReturn("Hello").when(keyValidationTemplate).getEncryptedValue(anyString(), any(), anyBoolean());
        doReturn("Hello").when(context).getRaw();
        keyValidationTemplate.validateKeys(keyContextHolder, Boolean.TRUE);

    }
}
