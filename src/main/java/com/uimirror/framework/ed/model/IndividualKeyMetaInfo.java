package com.uimirror.framework.ed.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;

/**
 * @author Jayaram
 *         3/26/16.
 */
@XmlRootElement
public class IndividualKeyMetaInfo {
    @XmlElement(name = "key")
//    @JsonSerialize(using = EncodeStringSerializer.class)
//    @JsonDeserialize(using = DecodeStringDeserializer.class)
    private String key;
    @XmlElement(name = "cipher_name")
    private String cipherName;
    @XmlElement(name = "version")
    private String version;
    @XmlElement(name = "fixed_salt")
    private String fixedSalt;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCipherName() {
        return cipherName;
    }

    public void setCipherName(String cipherName) {
        this.cipherName = cipherName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFixedSalt() {
        return fixedSalt;
    }

    public void setFixedSalt(String fixedSalt) {
        this.fixedSalt = fixedSalt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .appendSuper (super.toString())
                .append("key", key)
                .append("cipherName", cipherName)
                .append("version", version)
                .append("fixedSalt", fixedSalt)
                .toString();
    }
    public static class EncodeStringSerializer extends JsonSerializer<String> {

        @Override
        public void serialize(String tmpGuid,
                              JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider)
                throws IOException {
            if(tmpGuid != null)
                tmpGuid = new String(Hex.encodeHex(tmpGuid.getBytes()));
            jsonGenerator.writeObject(tmpGuid);
        }
    }

    public static class DecodeStringDeserializer extends JsonDeserializer<String> {

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

            String encodedText = p.getText();
            if(null != encodedText){
                try {
                    return new String(Hex.decodeHex(encodedText.toCharArray()));
                } catch (DecoderException e) {
                    throw new IllegalArgumentException("Not a valid encoded key");
                }
            }
            return null;
        }
    }
}
