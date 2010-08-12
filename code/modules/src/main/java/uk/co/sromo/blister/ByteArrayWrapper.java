package uk.co.sromo.blister;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 11-Aug-2010
 * Time: 20:39:25
 * To change this template use File | Settings | File Templates.
 */
public class ByteArrayWrapper {

    private final static Logger log = Logger.getLogger(ByteArrayWrapper.class);


    private final byte[] bytes;
    private final int length;
    private int pos;

    public ByteArrayWrapper(byte[] bytes) {
        this.bytes = bytes;
        this.length = bytes.length;
        this.pos = 0;
    }

    public short readByte() {
        return (short) (0x000000FF &  (int)bytes[pos++]);
    }

    public int readShort() {
        return ((0x000000FF & (int)bytes[pos++]) << 8) |
                (0x000000FF & (int)bytes[pos++]);
    }

    public long readInt() {
        return (((long)(0x000000FF & (int)bytes[pos++])) << 24) |
                (((long)(0x000000FF & (int)bytes[pos++])) << 16) |
                (((long)(0x000000FF & (int)bytes[pos++])) << 8) |
                (long)(0x000000FF & (int)bytes[pos++]);
    }

    public long readLong() {
        long msb = readInt();
        long lsb = readInt();
        return (msb << 32) | lsb;
    }

    public void skip(int count) {
        pos += count;
    }

    public void reset() {
        pos = 0;
    }

    public void dump() {
        StringBuilder sb = new StringBuilder();
        while (pos < length) {
            sb.append(Short.toString(readByte())).append(",");
            if (pos %16 == 0) {
                log.debug(sb.toString());
                sb = new StringBuilder();
            }
        }
        log.debug(sb.toString());
        pos = 0;
    }

    public byte[] get(int count) {
        byte[] ret = new byte[count];
        System.arraycopy(bytes, pos, ret, 0, count);
        pos += count;
        return ret;
    }

    public boolean hasMore() {
        return pos < length;
    }

    public int getLength() {
        return length;
    }
}
