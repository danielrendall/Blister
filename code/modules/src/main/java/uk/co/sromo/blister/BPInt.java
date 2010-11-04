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
    private final boolean isUnsigned;
    private static final Map<String, BPString> cache = new ConcurrentHashMap<String, BPString>(512, 0.75f, 16);

    public static BPInt get(int value) {
        return new BPInt(value);
    }

    public static BPInt get(long value) {
        return new BPInt(value, false);
    }

    public static BPInt from(byte[] bytes) {
        // note - all negative numbers are represented by 8 bytes.
        int size = bytes.length;
        boolean highestBitIsSet = ((0x80 & bytes[0]) == 128);
        if (size == 1) {
            return new BPInt(0xff & bytes[0]);
        } else if (size == 2) {
            return new BPInt(((0xff & bytes[0]) << 8) | (0xff & bytes[1]));
        } else if (size == 4) {
            if (highestBitIsSet) {
                // in this case, we have a number between 2^31 and 2^32 - 1. This won't fit in an int, so we have to force it to be a long
                return new BPInt(((0xff & (long)bytes[0]) << 24) | ((0xff & (long)bytes[1]) << 16) | ((0xff & (long)bytes[2]) << 8) | (0xff & (long)bytes[3]), false);
            } else {
                return new BPInt(((0xff & bytes[0]) << 24) | ((0xff & bytes[1]) << 16) | ((0xff & bytes[2]) << 8) | (0xff & bytes[3]));
            }
        } else if (size == 8) {
            if (highestBitIsSet) {
                long msb = (long) (((0xff & (long)~bytes[0]) << 24) | ((0xff & (long)~bytes[1]) << 16) | ((0xff & (long)~bytes[2]) << 8) | (0xff & (long)~bytes[3]));
                long lsb = (long) (((0xff & (long)~bytes[4]) << 24) | ((0xff & (long)~bytes[5]) << 16) | ((0xff & (long)~bytes[6]) << 8) | (0xff & (long)~bytes[7]));
                long value = (-((msb << 32) + lsb))-1;
                return (value < Integer.MIN_VALUE) ? new BPInt(value, false) : new BPInt((int)value);
            } else {
                long msb = (long) (((0xff & (long)bytes[0]) << 24) | ((0xff & (long)bytes[1]) << 16) | ((0xff & (long)bytes[2]) << 8) | (0xff & (long)bytes[3]));
                long lsb = (long) (((0xff & (long)bytes[4]) << 24) | ((0xff & (long)bytes[5]) << 16) | ((0xff & (long)bytes[6]) << 8) | (0xff & (long)bytes[7]));
                return new BPInt((msb << 32) + lsb, false);
            }
        } else {
            long msb = (long) (((0xff & (long)bytes[8]) << 24) | ((0xff & (long)bytes[9]) << 16) | ((0xff & (long)bytes[10]) << 8) | (0xff & (long)bytes[11]));
            long lsb = (long) (((0xff & (long)bytes[12]) << 24) | ((0xff & (long)bytes[13]) << 16) | ((0xff & (long)bytes[14]) << 8) | (0xff & (long)bytes[15]));
            return new BPInt((msb << 32) + lsb, true);
        }
    }

    private BPInt(int iValue) {
        this.iValue = iValue;
        this.lValue = iValue;
        this.isUnsigned = false;
        this.size = Size.Int;
    }

    private BPInt(long lValue, boolean isUnsigned) {
        this.iValue = (int) lValue;
        this.lValue = lValue;
        this.isUnsigned = isUnsigned;
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
