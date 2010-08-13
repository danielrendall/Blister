package uk.co.sromo.blister;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:42:57
 * To change this template use File | Settings | File Templates.
 */
public class BPInt extends BPItem {
    private final int value;
    private static final Map<String, BPString> cache = new ConcurrentHashMap<String, BPString>(512, 0.75f, 16);

    public static BPInt get(int value) {
        return new BPInt(value);
    }

    BPInt(byte[] bytes) {
        int size = bytes.length;
        int _data = 0;
        if (size == 1) {
            _data |= (0xff & bytes[0]);
        } else if (size == 2) {
            _data |= ((0xff & bytes[0]) << 8) | (0xff & bytes[1]);
        } else if (size == 4) {
            _data |= ((0xff & bytes[0]) << 24) | ((0xff & bytes[1]) << 16) | ((0xff & bytes[2]) << 8) | (0xff & bytes[3]);
        } else {
            throw new RuntimeException("Wasn't expecting an int to be " + size + " bytes");
        }
        this.value = _data;
    }

    private BPInt(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BPInt bpInt = (BPInt) o;

        if (value != bpInt.value) return false;

        return true;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public Type getType() {
        return Type.Int;
    }

    @Override
    public void accept(BPVisitor visitor) {
        visitor.visit(this);
    }
}
