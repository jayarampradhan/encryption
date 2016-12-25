package com.uimirror.framework.ed.model;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.Set;

/**
 * @author Jayaram
 *         3/7/16.
 */
@XmlRootElement
public class KeyFamilyMetaInfo {
    @XmlElement(name = "keys")
    private Set<keyMetaInfo> keyMetaInfos;

    public Set<keyMetaInfo> getKeyMetaInfos() {
        return CollectionUtils.isEmpty(keyMetaInfos) ? Collections.emptySet() : this.keyMetaInfos;
    }

    public void setKeyMetaInfos(Set<keyMetaInfo> keyMetaInfos) {
        this.keyMetaInfos = keyMetaInfos;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .appendSuper (super.toString())
                .append("keyMetaInfos", keyMetaInfos)
                .toString();
    }
}
