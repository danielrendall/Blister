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

    public final static long MAGIC_1 = 0x62706c69; // bpli
    public final static long MAGIC_2 = 0x73743030; // st00

    private final static Formatter formatter = new Formatter(Locale.UK);


    private final ByteArrayWrapper header;
    private final ByteArrayWrapper data;
    private final ByteArrayWrapper trailer;

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
    private short _sortVersion;
    private short _offsetIntSize;
    private short _objectRefSize;
    private long _numObjects;
    private long _topObject;
    private long _offsetTableOffset;


    public BinaryPlistBuilder(byte[] plist) throws Exception {
        if (plist.length < 40) {
            throw new Exception("PList not long enough");
        }
        int length = plist.length - 40;

        byte[] headerBytes = new byte[8];
        byte[] dataBytes = new byte[length];
        byte[] trailerBytes = new byte[32];

        System.arraycopy(plist, 0, headerBytes, 0, 8);
        System.arraycopy(plist, 8, dataBytes, 0, length);
        System.arraycopy(plist, 8 + length, trailerBytes, 0, 32);

        this.header = new ByteArrayWrapper(headerBytes);
        this.data = new ByteArrayWrapper(dataBytes);
        this.trailer = new ByteArrayWrapper(trailerBytes);
    }

    public void dump() {
        log.info("Header:");
        header.dump();
        log.info("Data:");
        data.dump();
        log.info("Trailer:");
        trailer.dump();
    }

    public boolean decode() {
        long first = header.readInt();
        long second = header.readInt();
        if ((first != MAGIC_1) || (second != MAGIC_2)) {
            log.warn("Magic numbers wrong - were " + formatter.format("%1$2x %2$2x", first, second));
            log.warn("Magic numbers wrong - were " + first + " " + second);
            return false;
        }

        readTrailer();
        log.info("_sortVersion: " + _sortVersion);
        log.info("_offsetIntSize: " + _offsetIntSize);
        log.info("_objectRefSize: " + _objectRefSize);
        log.info("_numObjects: " + _numObjects);
        log.info("_topObject: " + _topObject);
        log.info("_offsetTableOffset: " + _offsetTableOffset);

        while (data.hasMore()) {
            short next = data.readByte();
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
                    byte[] dateData = data.get(8);
                    items.add(new BPDate(dateData));
                    break;
                default:
                    final short littleNibble = (short)(next & 0x000f);
                    final short bigNibble = (short)(next & 0x00f0);
                    switch (bigNibble) {
                        case INT:
                            int numIntBytes = 2^littleNibble;
                           log.debug(String.format("Int %d bytes", numIntBytes));
                            byte[] intData = data.get(numIntBytes);
                            items.add(new BPInt(intData));
                            break;
                        case REAL:
                            int numRealBytes = 2^littleNibble;
                            log.debug(String.format("Real %d bytes", numRealBytes));
                            byte[] realData = data.get(numRealBytes);
                            items.add(new BPInt(realData));
                            break;
                        case DATA:
                            int numDataBytes = (littleNibble < 0x0f) ? littleNibble : data.readByte();
                            log.debug(String.format("Data %d bytes", numDataBytes));
                            byte[] dataData = data.get(numDataBytes);
                            items.add(new BPData(dataData));
                            break;
                        case STRING_ASCII:
                            int numStringAsciiChars = (littleNibble < 0x0f) ? littleNibble : data.readByte();
                            log.debug(String.format("String_Ascii %d chars", numStringAsciiChars));
                            byte[] stringAsciiData = data.get(numStringAsciiChars);
                            final BPStringAscii bpStringAscii = new BPStringAscii(stringAsciiData);
                            log.debug("String: " + bpStringAscii.getData());
                            items.add(bpStringAscii);

                            break;
                        case STRING_UNICODE:
                            int numStringUnicodeChars = (littleNibble < 0x0f) ? littleNibble : data.readByte();
                            log.debug(String.format("String_Unicode %d chars", numStringUnicodeChars));
                            byte[] stringUnicodeData = data.get(numStringUnicodeChars << 1);
                            final BPStringUnicode bpStringUnicode = new BPStringUnicode(stringUnicodeData);
                            log.debug("String: " + bpStringUnicode.getData());
                            items.add(bpStringUnicode);
                            break;
                        case UID:
                            int numUidBytes = littleNibble + 1;
                            log.debug(String.format("UID %d bytes", numUidBytes));
                            byte[] uidData = data.get(numUidBytes);
                            items.add(new BPUid(uidData));
                           break;
                        case ARRAY:
                            int numArrayItems = (littleNibble < 0x0f) ? littleNibble : data.readByte();
                            log.debug(String.format("Array %d items", numArrayItems));
                            items.add(new BPArray());
                            data.skip(numArrayItems * 2);
                            break;
                        case SET:
                            int numSetItems = (littleNibble < 0x0f) ? littleNibble : data.readByte();
                            log.debug(String.format("Set %d items", numSetItems));
                            items.add(new BPSet());
                            data.skip(numSetItems * 2);
                            break;
                        case DICT:
                            int numDictItems = (littleNibble < 0x0f) ? littleNibble : data.readByte();
                            log.debug(String.format("Dict %d items", numDictItems));
                            items.add(new BPDict());
                            data.skip(numDictItems * 2);
                            break;
                        default:
                            log.debug("Unused");
                            break;

                    }
            }
        }
        

        return true;
    }



    /**
     * http://www.opensource.apple.com/source/CF/CF-550/ForFoundationOnly.h
     *
     * typedef struct {
     * uint8_t	_unused[5];
     * uint8_t     _sortVersion;
     * uint8_t	_offsetIntSize;
     * uint8_t	_objectRefSize;
     * uint64_t	_numObjects;
     * uint64_t	_topObject;
     * uint64_t	_offsetTableOffset;
     * } CFBinaryPlistTrailer;
     */
    private void readTrailer() {
        trailer.skip(5);
        _sortVersion = trailer.readByte();
        _offsetIntSize = trailer.readByte();
        _objectRefSize = trailer.readByte();
        _numObjects = trailer.readLong();
        _topObject = trailer.readLong();
        _offsetTableOffset = trailer.readLong();
    }
}
