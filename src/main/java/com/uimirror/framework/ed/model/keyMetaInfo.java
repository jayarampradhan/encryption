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
 *         3/8/16.
 */
@XmlRootElement
public class keyMetaInfo {

    @XmlElement(name = "key_alias")
    private String keyAlias;
    @XmlElement(name = "fixed_salt")
    private String fixedSalt;
    @XmlElement(name = "initial_vector_length")
    //TODO defaulting to 16
    private int initialVectorLength=16;
    @XmlElement(name = "current_version")
    private String currentVersion;
    @XmlElement(name = "reserved_version_length")
    //TODO defaulting to 3, which needs to be worked on
    private int reservedPrefixLength=3;
    @XmlElement(name = "keys")
    private Set<IndividualKeyMetaInfo> individualKeyMetaInfos;

    public String getKeyAlias() {
        return keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public String getFixedSalt() {
        return fixedSalt;
    }

    public void setFixedSalt(String fixedSalt) {
        this.fixedSalt = fixedSalt;
    }

    public int getInitialVectorLength() {
        return initialVectorLength;
    }

    public void setInitialVectorLength(int initialVectorLength) {
        this.initialVectorLength = initialVectorLength;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public Set<IndividualKeyMetaInfo> getIndividualKeyMetaInfos() {
        return CollectionUtils.isEmpty(individualKeyMetaInfos) ? Collections.emptySet() : this.individualKeyMetaInfos;
    }

    public void setIndividualKeyMetaInfos(Set<IndividualKeyMetaInfo> individualKeyMetaInfos) {
        this.individualKeyMetaInfos = individualKeyMetaInfos;
    }

    public int getReservedPrefixLength() {
        return reservedPrefixLength;
    }

    public void setReservedPrefixLength(int reservedPrefixLength) {
        this.reservedPrefixLength = reservedPrefixLength;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .appendSuper (super.toString())
                .append("keyAlias", keyAlias)
                .append("fixedSalt", fixedSalt)
                .append("initialVectorLength", initialVectorLength)
                .append("currentVersion", currentVersion)
                .append("individualKeyMetaInfos", individualKeyMetaInfos)
                .toString();
    }
}
