package uk.co.sromo.blister;

import org.apache.commons.lang.StringUtils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 13-Aug-2010
 * Time: 22:15:00
 * To change this template use File | Settings | File Templates.
 */
public class BPString extends BPItem {
    private static final Charset ASCII = Charset.forName("ASCII");
    private static final Charset UTF16 = Charset.forName("UTF16");

    enum EncodingType {ASCII, UTF16};

    private final String value;
    private final EncodingType encodingType;

    static BPString ascii(byte[] bytes) {
        return get(ASCII.decode(ByteBuffer.wrap(bytes)).toString(), EncodingType.ASCII);
    }

    static BPString unicode(byte[] bytes) {
        return get(UTF16.decode(ByteBuffer.wrap(bytes)).toString(), EncodingType.UTF16);
    }

    static BPString get(String string, EncodingType encodingType) {
        return new BPString(string, encodingType);
    }

    // externally created strings are assumed to be unicode
    // TODO - we could be cleverer and check to see if a string can be encoded in ASCII
    public static BPString get(String string) {
//        String roundTripped = ASCII.decode(ASCII.encode(string)).toString();
        for (int i=0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (Character.getNumericValue(c) > 127) {
                return get(string, EncodingType.UTF16);
            }
        }
        return get(string, EncodingType.ASCII);
    }
    
    private BPString(String value, EncodingType encodingType) {
        this.value = value;
        this.encodingType = encodingType;
    }

    // convenience method to enable BPDict to get things, without caching the resulting BPStrings
    BPString(String value) {
        this(value, EncodingType.ASCII);
    }

    byte[] asBytes() {
        if (encodingType == EncodingType.ASCII) {
            return ASCII.encode(value).array();
        } else {
            // Need to strip out the 2-byte BOM at the start
            byte[] converted = UTF16.encode(value).array();
            return Arrays.copyOfRange(converted, 2, converted.length);
        }
    }

    int length() {
       return value.length(); 
    }

    short bpType() {
        if (encodingType == EncodingType.ASCII) {
            return BinaryPlist.STRING_ASCII;
        } else {
            return BinaryPlist.STRING_UNICODE;
        }
    }

    public EncodingType getEncodingType() {
         return encodingType;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BPString bpString = (BPString) o;

        if (value != null ? !value.equals(bpString.value) : bpString.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public Type getType() {
        return Type.String;
    }

    @Override
    public void accept(BPVisitor visitor) {
        visitor.visit(this);
    }
}
