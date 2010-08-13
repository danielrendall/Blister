package uk.co.sromo.blister;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.HashMap;
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
    // take advantage of interning of strings to get unique BPString objects
    private static final Map<String, BPString> cache = new ConcurrentHashMap<String, BPString>(512, 0.75f, 16);

    private final String data;

    static BPString ascii(byte[] bytes) {
        return get(ASCII.decode(ByteBuffer.wrap(bytes)).toString());
            }

    static BPString unicode(byte[] bytes) {
        return get(UTF16.decode(ByteBuffer.wrap(bytes)).toString());
    }

    static BPString get(String string) {
        if (!cache.containsKey(string)) {
            BPString bpString = new BPString(string);
            cache.put(string, bpString);
            return bpString;
        } else {
            return cache.get(string);
        }
    }
    
    private BPString(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    @Override
    public Type type() {
        return Type.String;
    }

    @Override
    public void accept(BPVisitor visitor) {
        visitor.visit(this);
    }
}
