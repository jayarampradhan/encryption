package com.uimirror.framework.ed;


import com.uimirror.framework.ed.model.KeyFamilyMetaInfo;
import com.uimirror.framework.ed.util.ObjectMapperFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jayaram
 *         3/7/16.
 */
public class DbKeyFamilyInputTest {

    @Test
    public void testParseJson() throws Exception {
        KeyFamilyMetaInfo dbKeyFamily = ObjectMapperFactory.getMapper().readValue(new DefaultResourceLoader().getResource("classpath:/sample_key_info.json").getInputStream(), KeyFamilyMetaInfo.class);
        assertThat(dbKeyFamily).isNotNull();
        assertThat(dbKeyFamily.getKeyMetaInfos()).hasSize(5);
    }
    @Test
    @Ignore
    public void testParseYML() throws Exception {
        KeyFamilyMetaInfo dbKeyFamily = ObjectMapperFactory.getMapper().readValue(this.getClass().getResourceAsStream("/sample_key_info.yml"), KeyFamilyMetaInfo.class);
        assertThat(dbKeyFamily).isNotNull();
    }
}
