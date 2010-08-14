package uk.co.sromo.blister;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 20:49:01
 * To change this template use File | Settings | File Templates.
 */
public class BinaryPlistDecoder {

    private final static Formatter formatter = new Formatter(Locale.UK);


    private final BinaryPlistHeader header;
    private final BinaryPlistOffsetReader offsetReader;
    private final BinaryPlistTrailer trailer;
    private final ByteArrayWrapper data;
    private final BinaryPlistOffsetTable offsetTable;

    private final static Logger log = Logger.getLogger(BinaryPlistDecoder.class);

    private final Map<Integer, BPItem> items = new HashMap<Integer, BPItem>(); // offset, item

    private final Queue<Integer> offsetsToExpand = new LinkedList<Integer>();

    BinaryPlistDecoder(BinaryPlistHeader header, BinaryPlistTrailer trailer, byte[] data, BinaryPlistOffsetTable offsetTable, BinaryPlistOffsetReader offsetReader) {
        this.header = header;
        this.data = new ByteArrayWrapper(data);
        this.offsetTable = offsetTable;
        this.trailer = trailer;
        this.offsetReader = offsetReader;
    }

    public void dump() {
        log.info("Data:");
        data.dump();
        log.info("Offset table:");
        offsetTable.dump();
    }

    BPItem decode() throws BinaryPlistException {

        log.info("sortVersion: " + trailer.getSortVersion());
        log.info("offsetIntSize: " + trailer.getOffsetIntSize());
        log.info("objectRefSize: " + trailer.getObjectRefSize());
        log.info("numObjects: " + trailer.getNumObjects());
        log.info("topObject: " + trailer.getTopObject());
        log.info("offsetTableOffset: " + trailer.getOffsetTableOffset());
        log.info("Data length: " + data.getLength());
        log.info("Offset table length: " + offsetTable.getSize());


        BPItem item = getItemAtIndex((int)(trailer.getTopObject()));

        while (offsetsToExpand.peek() != null) {
            int offset = offsetsToExpand.poll();
            BPItem itemToExpand = items.get(offset);
            itemToExpand.expand(this);
        }

        return item;
    }

    BPItem getItemAtIndex(int index) {
        return getItem(offsetTable.get(index));
    }

    BPItem getItem(int offset) {
        if (items.containsKey(offset)) {
            log.debug("Already have item at offset " + offset);
            return items.get(offset);
        } else {
            // mark whatever we return for expansion
            log.debug("Marking item at offset " + offset + " for expansion");
            offsetsToExpand.offer(offset);
            BPItem toReturn = BPNull.Instance;

            data.setPosition(offset);
            short next = data.readByte();
            switch (next) {
                case BinaryPlist.NULL:
                    log.debug("Null");
                    toReturn = BPNull.Instance;
                    break;
                case BinaryPlist.BOOL_FALSE:
                    log.debug("Bool_False");
                    toReturn = BPBoolean.FALSE;
                    break;
                case BinaryPlist.BOOL_TRUE:
                    log.debug("Bool_True");
                    toReturn = BPBoolean.TRUE;
                    break;
                case BinaryPlist.FILL:
                    log.debug("Fill");
                    toReturn = BPNull.Instance;
                    break;
                case BinaryPlist.DATE:
                    log.debug("Date");
                    byte[] dateData = data.get(8);
                    toReturn = new BPDate(dateData);
                    break;
                default:
                    final short littleNibble = (short) (next & 0x000f);
                    final short bigNibble = (short) (next & 0x00f0);
                    switch (bigNibble) {
                        case BinaryPlist.INT:
                            int numIntBytes = twoToThe(littleNibble);
                            log.debug(String.format("Int %d bytes", numIntBytes));
                            byte[] intData = data.get(numIntBytes);
                            toReturn = new BPInt(intData);
                            break;
                        case BinaryPlist.REAL:
                            int numRealBytes = twoToThe(littleNibble);
                            log.debug(String.format("Real %d bytes", numRealBytes));
                            byte[] realData = data.get(numRealBytes);
                            toReturn = new BPReal(realData);
                            break;
                        case BinaryPlist.DATA:
                            int numDataBytes = (littleNibble < 0x0f) ? littleNibble : readAnInt();
                            log.debug(String.format("Data %d bytes", numDataBytes));
                            byte[] dataData = data.get(numDataBytes);
                            toReturn = new BPData(dataData);
                            break;
                        case BinaryPlist.STRING_ASCII:
                            int numStringAsciiChars = (littleNibble < 0x0f) ? littleNibble : readAnInt();
                            log.debug(String.format("String_Ascii %d chars", numStringAsciiChars));
                            byte[] stringAsciiData = data.get(numStringAsciiChars);
                            final BPString bpStringAscii = BPString.ascii(stringAsciiData);
                            log.debug("String: " + bpStringAscii.getValue());
                            toReturn = bpStringAscii;
                            break;
                        case BinaryPlist.STRING_UNICODE:
                            int numStringUnicodeChars = (littleNibble < 0x0f) ? littleNibble : readAnInt();
                            log.debug(String.format("String_Unicode %d chars", numStringUnicodeChars));
                            byte[] stringUnicodeData = data.get(numStringUnicodeChars << 1);
                            final BPString bpStringUnicode = BPString.unicode(stringUnicodeData);
                            log.debug("String: " + bpStringUnicode.getValue());
                            toReturn = bpStringUnicode;
                            break;
                        case BinaryPlist.UID:
                            int numUidBytes = littleNibble + 1;
                            log.debug(String.format("UID %d bytes", numUidBytes));
                            byte[] uidData = data.get(numUidBytes);
                            toReturn = new BPUid(uidData);
                            break;
                        case BinaryPlist.ARRAY:
                            int numArrayItems = (littleNibble < 0x0f) ? littleNibble : readAnInt();
                            log.debug(String.format("Array %d items", numArrayItems));
                            int[] arrayItemOffsets = new int[numArrayItems];
                            for (int i=0; i< numArrayItems; i++) {
                                arrayItemOffsets[i] = offsetReader.getOffset(data);
                            }
                            toReturn = new BPArray(arrayItemOffsets);
                            break;
                        case BinaryPlist.SET:
                            int numSetItems = (littleNibble < 0x0f) ? littleNibble : readAnInt();
                            log.debug(String.format("Set %d items", numSetItems));
                            int[] setItemOffsets = new int[numSetItems];
                            for (int i=0; i< numSetItems; i++) {
                                setItemOffsets[i] = offsetReader.getOffset(data);
                            }
                            toReturn = new BPSet(setItemOffsets);
                            break;
                        case BinaryPlist.DICT:
                            int numDictItems = (littleNibble < 0x0f) ? littleNibble : readAnInt();
                            log.debug(String.format("Dict %d items", numDictItems));
                            int[] keyOffsets = new int[numDictItems];
                            int[] valueOffsets = new int[numDictItems];
                            for (int i=0; i< numDictItems; i++) {
                                keyOffsets[i] = offsetReader.getOffset(data);
                            }
                            for (int i=0; i< numDictItems; i++) {
                                valueOffsets[i] = offsetReader.getOffset(data);
                            }
                            toReturn = new BPDict(keyOffsets, valueOffsets);
                            break;
                        default:
                            log.debug("Unused");
                            toReturn = BPNull.Instance;
                            break;

                    }
            }
            items.put(offset, toReturn);
            return toReturn;
        }
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
        if (bigNibble != BinaryPlist.INT) {
            throw new RuntimeException("Asked to read an int, but next thing in stream wasn't one");
        }
        int numIntBytes = twoToThe(littleNibble);
        byte[] intData = data.get(numIntBytes);
        BPInt ret = new BPInt(intData);
        return ret.getValue();
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
