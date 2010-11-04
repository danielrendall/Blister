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
    public enum Size {Int, Long}

    private final int iValue;
    private final long lValue;
    private final Size size;
    private static final Map<String, BPString> cache = new ConcurrentHashMap<String, BPString>(512, 0.75f, 16);

    public static BPInt get(int value) {
        return new BPInt(value);
    }

    public static BPInt get(long value) {
        return new BPInt(value);
    }

    public static BPInt from(byte[] bytes) {
        int size = bytes.length;
        if (size == 1) {
            return new BPInt(0xff & bytes[0]);
        } else if (size == 2) {
            return new BPInt(((0xff & bytes[0]) << 8) | (0xff & bytes[1]));
        } else if (size == 4) {
            if (Integer.highestOneBit(0xff & bytes[0]) != 128) {
                return new BPInt(((0xff & bytes[0]) << 24) | ((0xff & bytes[1]) << 16) | ((0xff & bytes[2]) << 8) | (0xff & bytes[3]));
            } else {
                return new BPInt(((0xff & (long)bytes[0]) << 24) | ((0xff & (long)bytes[1]) << 16) | ((0xff & (long)bytes[2]) << 8) | (0xff & (long)bytes[3]));
            }
        } else {
            long msb = (long) (((0xff & (long)bytes[0]) << 24) | ((0xff & (long)bytes[1]) << 16) | ((0xff & (long)bytes[2]) << 8) | (0xff & (long)bytes[3]));
            long lsb = (long) (((0xff & (long)bytes[4]) << 24) | ((0xff & (long)bytes[5]) << 16) | ((0xff & (long)bytes[6]) << 8) | (0xff & (long)bytes[7]));
            log.debug("msb: " + msb + " lsb: " + lsb);
            log.debug("msb <<32: " + (msb<<32) + " lsb: " + lsb);
            return new BPInt((msb << 32) + lsb);
        }
    }

    private BPInt(int iValue) {
        this.iValue = iValue;
        this.lValue = iValue;
        this.size = Size.Int;
    }

    private BPInt(long lValue) {
        this.iValue = (int) lValue;
        this.lValue = lValue;
        this.size = Size.Long;
    }

    public int getValue() {
        return iValue;
    }

    public long getLongValue() {
        return lValue;
    }

    public Size getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BPInt bpInt = (BPInt) o;

        if (lValue != bpInt.lValue) return false;

        return true;
    }

    @Override
    public String toString() {
        return (size == Size.Long) ? Long.toString(lValue) : Integer.toString(iValue);
    }

    @Override
    public int hashCode() {
        return (size == Size.Long) ? (int)(lValue ^ (lValue >>> 32 )) : iValue;
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
