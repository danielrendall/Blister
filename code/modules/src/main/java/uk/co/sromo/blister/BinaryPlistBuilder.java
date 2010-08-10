package uk.co.sromo.blister;

import org.apache.log4j.Logger;
import uk.co.sromo.blister.objects.*;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 20:49:01
 * To change this template use File | Settings | File Templates.
 */
public class BinaryPlistBuilder {

    public final static int MAGIC_1 = 0x62706c69; // bpli
    public final static int MAGIC_2 = 0x73743030; // st00

    private final static Formatter formatter = new Formatter(Locale.UK);


    private final byte[] header;
    private final byte[] data;
    private final byte[] trailer;

    private final int length;
    private int headerPos;
    private int dataPos;
    private int trailerPos;
    private final static Logger log = Logger.getLogger(BinaryPlistBuilder.class);

    private final static short NULL =               (short)0x00;
    private final static short BOOL_FALSE =         (short)0x08;
    private final static short BOOL_TRUE =          (short)0x09;
    private final static short FILL =               (short)0x0f;
    private final static short INT =                (short)0x10; // mask
    private final static short REAL =               (short)0x20; // mask
    private final static short DATE =               (short)0x33;
    private final static short DATA =               (short)0x40; // mask
    private final static short STRING_ASCII =       (short)0x50; // mask
    private final static short STRING_UNICODE =     (short)0x60; // mask
    private final static short UNUSED_1 =           (short)0x70; // mask
    private final static short UID =                (short)0x80; // mask
    private final static short UNUSED_2 =           (short)0x90; // mask
    private final static short ARRAY =              (short)0xa0; // mask
    private final static short UNUSED_3 =           (short)0xb0; // mask
    private final static short SET =                (short)0xc0; // mask
    private final static short DICT =               (short)0xd0; // mask
    private final static short UNUSED_4 =           (short)0xe0; // mask
    private final static short UNUSED_5 =           (short)0xf0; // mask

    private final List<BPItem> items = new ArrayList<BPItem>();


    public BinaryPlistBuilder(byte[] plist) throws Exception {
        if (plist.length < 40) {
            throw new Exception("PList not long enough");
        }
        this.length = plist.length - 40;

        this.header = new byte[8];
        this.data = new byte[this.length];
        this.trailer = new byte[32];

        System.arraycopy(plist, 0, this.header, 0, 8);
        System.arraycopy(plist, 8, this.data, 0, this.length);
        System.arraycopy(plist, 8 + this.length, this.trailer, 0, 32);
        headerPos = 0;
        dataPos = 0;
        trailerPos = 0;
    }

    public boolean decode() {
        int first = readHeaderInt();
        int second = readHeaderInt();
        if ((first != MAGIC_1) || (second != MAGIC_2)) {
            log.warn("Magic numbers wrong - were " + formatter.format("%1$2x %2$2x", first, second));
            log.warn("Magic numbers wrong - were " + first + " " + second);
            return false;
        }

        while (dataPos < length) {
            short next = readByte();
            switch (next) {
                case NULL:
                    log.debug("Null");
                    // ignore?
                    break;
                case BOOL_FALSE:
                    log.debug("Bool_False");
                    items.add(BPBoolFalse.Instance);
                    break;
                case BOOL_TRUE:
                    log.debug("Bool_True");
                    items.add(BPBoolTrue.Instance);
                    break;
                case FILL:
                    log.debug("Fill");
                    // ignore?
                    break;
                case DATE:
                    log.debug("Date");
                    byte[] dateData = new byte[8];
                    System.arraycopy(data, dataPos, dateData, 0, 8);
                    dataPos += 8;
                    items.add(new BPDate(dateData));
                    break;
                default:
                    final short littleNibble = (short)(next & 0x000f);
                    final short bigNibble = (short)(next & 0x00f0);
                    switch (bigNibble) {
                        case INT:
                            int numIntBytes = 2^littleNibble;
                           log.debug(String.format("Int %d bytes", numIntBytes));
                            byte[] intData = new byte[numIntBytes];
                            System.arraycopy(data, dataPos, intData, 0, numIntBytes);
                            dataPos += numIntBytes;
                            items.add(new BPInt(intData));
                            break;
                        case REAL:
                            int numRealBytes = 2^littleNibble;
                            log.debug(String.format("Real %d bytes", numRealBytes));
                            byte[] realData = new byte[numRealBytes];
                            System.arraycopy(data, dataPos, realData, 0, numRealBytes);
                            dataPos += numRealBytes;
                            items.add(new BPInt(realData));
                            break;
                        case DATA:
                            int numDataBytes = (littleNibble < 0x0f) ? littleNibble : readCount();
                            log.debug(String.format("Data %d bytes", numDataBytes));
                            byte[] dataData = new byte[numDataBytes];
                            System.arraycopy(data, dataPos, dataData, 0, numDataBytes);
                            dataPos += numDataBytes;
                            items.add(new BPData(dataData));
                            break;
                        case STRING_ASCII:
                            int numStringAsciiChars = (littleNibble < 0x0f) ? littleNibble : readCount();
                            log.debug(String.format("String_Ascii %d chars", numStringAsciiChars));
                            byte[] stringAsciiData = new byte[numStringAsciiChars];
                            System.arraycopy(data, dataPos, stringAsciiData, 0, numStringAsciiChars);
                            dataPos += numStringAsciiChars;
                            final BPStringAscii bpStringAscii = new BPStringAscii(stringAsciiData);
                            log.debug("String: " + bpStringAscii.getData());
                            items.add(bpStringAscii);

                            break;
                        case STRING_UNICODE:
                            int numStringUnicodeChars = (littleNibble < 0x0f) ? littleNibble : readCount();
                            log.debug(String.format("String_Unicode %d chars", numStringUnicodeChars));
                            byte[] stringUnicodeData = new byte[numStringUnicodeChars << 1];
                            System.arraycopy(data, dataPos, stringUnicodeData, 0, numStringUnicodeChars << 1);
                            dataPos += numStringUnicodeChars << 1;
                            final BPStringUnicode bpStringUnicode = new BPStringUnicode(stringUnicodeData);
                            log.debug("String: " + bpStringUnicode.getData());
                            items.add(bpStringUnicode);
                            break;
                        case UID:
                            int numUidBytes = littleNibble + 1;
                            log.debug(String.format("UID %d bytes", numUidBytes));
                            byte[] uidData = new byte[numUidBytes];
                            System.arraycopy(data, dataPos, uidData, 0, numUidBytes);
                            dataPos += numUidBytes;
                            items.add(new BPUid(uidData));
                           break;
                        case ARRAY:
                            int numArrayItems = (littleNibble < 0x0f) ? littleNibble : readCount();
                            log.debug(String.format("Array %d items", numArrayItems));
                            items.add(new BPArray());
                            dataPos += numArrayItems * 2;
                            break;
                        case SET:
                            int numSetItems = (littleNibble < 0x0f) ? littleNibble : readCount();
                            log.debug(String.format("Set %d items", numSetItems));
                            items.add(new BPSet());
                            dataPos += numSetItems * 2;
                            break;
                        case DICT:
                            int numDictItems = (littleNibble < 0x0f) ? littleNibble : readCount();
                            log.debug(String.format("Dict %d items", numDictItems));
                            items.add(new BPDict());
                            dataPos += numDictItems * 2;
                            break;
                        default:
                            log.debug("Unused");
                            break;

                    }
            }
        }
        

        return true;
    }

    public void dump() {
        StringBuilder sb = new StringBuilder();
        while (dataPos < length) {
            sb.append(Short.toString(readByte())).append(",");
            if (dataPos %16 == 0) {
                log.debug(sb.toString());
                sb = new StringBuilder();
            }
        }
        log.debug(sb.toString());
        dataPos = 0;
    }

    private short readByte() {
        return (short) (0x000000FF &  (int)data[dataPos++]);
    }

    private int readCount() {
        return readByte();
    }

    private int readHeaderInt() {
        return ((0x000000FF & (int)header[headerPos++]) << 24) |
                ((0x000000FF & (int)header[headerPos++]) << 16) |
                ((0x000000FF & (int)header[headerPos++]) << 8) |
                (0x000000FF & (int)header[headerPos++]);
    }

    private int readDataInt() {
        return ((0x000000FF & (int)data[dataPos++]) << 24) |
                ((0x000000FF & (int)data[dataPos++]) << 16) |
                ((0x000000FF & (int)data[dataPos++]) << 8) |
                (0x000000FF & (int)data[dataPos++]);
    }

}
