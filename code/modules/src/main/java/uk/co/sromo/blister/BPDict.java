package uk.co.sromo.blister;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:45:45
 * To change this template use File | Settings | File Templates.
 */
public class BPDict extends BPExpandableItem implements Map<BPString, BPItem> {
    private final int[] keyOffsets;
    private final int[] valueOffsets;

    private final Map<BPString, BPItem> map = new LinkedHashMap<BPString, BPItem>();

    BPDict(int[] keyOffsets, int[] valueOffsets) {
        this.keyOffsets = keyOffsets;
        this.valueOffsets = valueOffsets;
    }

    public BPDict() {
        keyOffsets = EMPTY;
        valueOffsets = EMPTY;
    }

    @Override
    protected void doExpand(BinaryPlistDecoder decoder) throws BinaryPlistException {
        for (int i=0; i< keyOffsets.length; i++) {
            int keyOffset = keyOffsets[i];
            int valueOffset = valueOffsets[i];
            BPItem key = decoder.getItemAtIndex(keyOffset);
            if (key.getType() != Type.String) {
                throw new BinaryPlistException("Dictionary key wasn't a string, was " + key.getType());
            }
            BPItem value = decoder.getItemAtIndex(valueOffset);
            map.put((BPString)key, value);
            log.debug(key.toString() + " = " + value.toString());
        }
    }

    public BPDict with(BPString key, BPItem value) {
        put(key, value);
        return this;
    }

    public BPDict with(String key, BPItem value) {
        return with(BPString.get(key), value);
    }

    public BPDict with(String key, String value) {
        return with(key, BPString.get(value));
    }

    public BPDict with(String key, int value) {
        return with(key, BPInt.get(value));
    }

    public BPDict with(String key, boolean value) {
        return with(key, BPBoolean.get(value));
    }

    public BPDict with(BPString key, String value) {
        return with(key, BPString.get(value));
    }

    public BPDict with(BPString key, int value) {
        return with(key, BPInt.get(value));
    }

    public BPDict with(BPString key, boolean value) {
        return with(key, BPBoolean.get(value));
    }

    public String getString(String key, String def) throws BinaryPlistException {
        BPString bpKey = new BPString(key);
        if (!containsKey(bpKey)) return def;
        BPItem value = get(bpKey);
        if (value.getType() != BPItem.Type.String) {
            throw new BinaryPlistException("Not a string");
        }
        return ((BPString) value).getValue();
    }

    public int getString(String key, int def) throws BinaryPlistException {
        BPString bpKey = new BPString(key);
        if (!containsKey(bpKey)) return def;
        BPItem value = get(bpKey);
        if (value.getType() != BPItem.Type.Int) {
            throw new BinaryPlistException("Not an int");
        }
        return ((BPInt) value).getValue();
    }

    public boolean getBoolean(String key, boolean def) throws BinaryPlistException {
        BPString bpKey = new BPString(key);
        if (!containsKey(bpKey)) return def;
        BPItem value = get(bpKey);
        if (value.getType() != BPItem.Type.Boolean) {
            throw new BinaryPlistException("Not a boolean");
        }
        return ((BPBoolean) value).getValue();
    }

    public void clear() {
        map.clear();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public Set<Entry<BPString, BPItem>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return map.equals(o);
    }

    public BPItem get(Object key) {
        return map.get(key);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Set<BPString> keySet() {
        return map.keySet();
    }

    public BPItem put(BPString key, BPItem value) {
        return map.put(key, value);
    }

    public void putAll(Map<? extends BPString, ? extends BPItem> m) {
        map.putAll(m);
    }

    public BPItem remove(Object key) {
        return map.remove(key);
    }

    public int size() {
        return map.size();
    }

    public Collection<BPItem> values() {
        return map.values();
    }

    @Override
    public String toString() {
        return "BPDict{" +
                "map=" + map +
                '}';
    }

    @Override
    public Type getType() {
        return Type.Dict;
    }

    @Override
    public void accept(BPVisitor visitor) {
        visitor.visit(this);
    }
}
