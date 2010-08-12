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
public class BinaryPlistDecoder {

    public final static long MAGIC_1 = 0x62706c69; // bpli
    public final static long MAGIC_2 = 0x73743030; // st00

    private final static Formatter formatter = new Formatter(Locale.UK);


    private final BinaryPlistHeader header;
    private final BinaryPlistTrailer trailer;
    private final ByteArrayWrapper data;
    private final BinaryPlistOffsetTable offsetTable;

    private final static Logger log = Logger.getLogger(BinaryPlistDecoder.class);

    private final static short NULL = (short) 0x00;
    private final static short BOOL_FALSE = (short) 0x08;
    private final static short BOOL_TRUE = (short) 0x09;
    private final static short FILL = (short) 0x0f;
    private final static short INT = (short) 0x10; // mask
    private final static short REAL = (short) 0x20; // mask
    private final static short DATE = (short) 0x33;
    private final static short DATA = (short) 0x40; // mask
    private final static short STRING_ASCII = (short) 0x50; // mask
    private final static short STRING_UNICODE = (short) 0x60; // mask
    private final static short UNUSED_1 = (short) 0x70; // mask
    private final static short UID = (short) 0x80; // mask
    private final static short UNUSED_2 = (short) 0x90; // mask
    private final static short ARRAY = (short) 0xa0; // mask
    private final static short UNUSED_3 = (short) 0xb0; // mask
    private final static short SET = (short) 0xc0; // mask
    private final static short DICT = (short) 0xd0; // mask
    private final static short UNUSED_4 = (short) 0xe0; // mask
    private final static short UNUSED_5 = (short) 0xf0; // mask

    private final List<BPItem> items = new ArrayList<BPItem>();

    public static BinaryPlistDecoder create(byte[] plist) throws Exception {
        if (plist.length < 40) {
            throw new Exception("PList not long enough");
        }

        byte[] headerBytes = new byte[8];
        byte[] trailerBytes = new byte[32];

        System.arraycopy(plist, 0, headerBytes, 0, 8);
        System.arraycopy(plist, plist.length - 32, trailerBytes, 0, 32);

        BinaryPlistHeader header = BinaryPlistHeader.build(headerBytes);
        BinaryPlistTrailer trailer = BinaryPlistTrailer.build(trailerBytes);

        int offset = (int) trailer.getOffsetTableOffset();

        int dataLength = offset - 8;
        int offsetTableLength = plist.length - 32 - offset;

        byte[] dataBytes = new byte[dataLength];
        System.arraycopy(plist, 8, dataBytes, 0, dataLength);

        byte[] offsetTableBytes = new byte[offsetTableLength];
        System.arraycopy(plist, offset, offsetTableBytes, 0, offsetTableLength);
        BinaryPlistOffsetTable offsetTable = BinaryPlistOffsetTable.build(offsetTableBytes, trailer.getOffsetIntSize());

        return new BinaryPlistDecoder(header, trailer, dataBytes, offsetTable);

    }

    public BinaryPlistDecoder(BinaryPlistHeader header, BinaryPlistTrailer trailer, byte[] data, BinaryPlistOffsetTable offsetTable) throws Exception {


        this.header = header;
        this.data = new ByteArrayWrapper(data);
        this.offsetTable = offsetTable;
        this.trailer = trailer;
    }

    public void dump() {
        log.info("Data:");
        data.dump();
        log.info("Offset table:");
        offsetTable.dump();
    }

    public boolean decode() {

        log.info("sortVersion: " + trailer.getSortVersion());
        log.info("offsetIntSize: " + trailer.getOffsetIntSize());
        log.info("objectRefSize: " + trailer.getObjectRefSize());
        log.info("numObjects: " + trailer.getNumObjects());
        log.info("topObject: " + trailer.getTopObject());
        log.info("offsetTableOffset: " + trailer.getOffsetTableOffset());
        log.info("Data length: " + data.getLength());
        log.info("Offset table length: " + offsetTable.getSize());

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
                    final short littleNibble = (short) (next & 0x000f);
                    final short bigNibble = (short) (next & 0x00f0);
                    switch (bigNibble) {
                        case INT:
                            int numIntBytes = twoToThe(littleNibble);
                            log.debug(String.format("Int %d bytes", numIntBytes));
                            byte[] intData = data.get(numIntBytes);
                            items.add(new BPInt(intData));
                            break;
                        case REAL:
                            int numRealBytes = twoToThe(littleNibble);
                            log.debug(String.format("Real %d bytes", numRealBytes));
                            byte[] realData = data.get(numRealBytes);
                            items.add(new BPInt(realData));
                            break;
                        case DATA:
                            int numDataBytes = (littleNibble < 0x0f) ? littleNibble : readAnInt();
                            log.debug(String.format("Data %d bytes", numDataBytes));
                            byte[] dataData = data.get(numDataBytes);
                            items.add(new BPData(dataData));
                            break;
                        case STRING_ASCII:
                            int numStringAsciiChars = (littleNibble < 0x0f) ? littleNibble : readAnInt();
                            log.debug(String.format("String_Ascii %d chars", numStringAsciiChars));
                            byte[] stringAsciiData = data.get(numStringAsciiChars);
                            final BPStringAscii bpStringAscii = new BPStringAscii(stringAsciiData);
                            log.debug("String: " + bpStringAscii.getData());
                            items.add(bpStringAscii);

                            break;
                        case STRING_UNICODE:
                            int numStringUnicodeChars = (littleNibble < 0x0f) ? littleNibble : readAnInt();
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
                            int numArrayItems = (littleNibble < 0x0f) ? littleNibble : readAnInt();
                            log.debug(String.format("Array %d items", numArrayItems));
                            items.add(new BPArray());
                            data.skip(numArrayItems * 2);
                            break;
                        case SET:
                            int numSetItems = (littleNibble < 0x0f) ? littleNibble : readAnInt();
                            log.debug(String.format("Set %d items", numSetItems));
                            items.add(new BPSet());
                            data.skip(numSetItems * 2);
                            break;
                        case DICT:
                            int numDictItems = (littleNibble < 0x0f) ? littleNibble : readAnInt();
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
     * This is called whenever we're expecting the next thing in the stream to be an int giving the
     * size of something.
     *
     * @return
     */
    private int readAnInt() {
        short next = data.readByte();
        final short littleNibble = (short) (next & 0x000f);
        final short bigNibble = (short) (next & 0x00f0);
        if (bigNibble != INT) {
            throw new RuntimeException("Asked to read an int, but next thing in stream wasn't one");
        }
        int numIntBytes = twoToThe(littleNibble);
        byte[] intData = data.get(numIntBytes);
        BPInt ret = new BPInt(intData);
        return ret.getData();
    }

    private int twoToThe(short exponent) {
        switch (exponent) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 4;
            case 3:
                return 8;
            case 4:
                return 16;
            case 5:
                return 32;
            case 6:
                return 64;
            case 7:
                return 128;
            case 8:
                return 256;
            case 9:
                return 512;
            case 10:
                return 1024;
            case 11:
                return 2048;
            case 12:
                return 4096;
            case 13:
                return 8192;
            case 14:
                return 16384;
            case 15:
                return 32768;
            default:
                return 65536;
        }
    }

}
