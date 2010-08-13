package uk.co.sromo.blister;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 12-Aug-2010
 * Time: 23:10:01
 * To change this template use File | Settings | File Templates.
 */
class BinaryPlistOffsetTable {

    private final static Logger log = Logger.getLogger(BinaryPlistOffsetTable.class);

    private final int[] offsets;
    private final int size;

    static BinaryPlistOffsetTable build(byte[] data, int offsetSize) throws BinaryPlistException {
        if (data.length % offsetSize != 0) {
            throw new BinaryPlistException("Length of data not commensurate with offset size");
        }
        final int numberOfOffsets = data.length / offsetSize;
        int[] offsetArray = new int[numberOfOffsets];
        byte[] buffer = new byte[offsetSize];

        for (int i=0; i < numberOfOffsets; i++) {
            System.arraycopy(data, i * offsetSize, buffer, 0, offsetSize);
            offsetArray[i] = new BPInt(buffer).getValue() - 8; // offsets relative to start of data
        }
        return new BinaryPlistOffsetTable(offsetArray);
    }

    BinaryPlistOffsetTable(int[] offsets) {
        this.offsets = offsets;
        this.size = offsets.length;
    }

    int get(int offset) {
        return offsets[offset];
    }

    int get(long offset) {
        return offsets[(int)offset];
    }

    int getSize() {
        return size;
    }

    void dump() {
        for (int i=0; i< size; i++) {
            log.debug(String.format("%d: %d", i, offsets[i]));
        }
    }
}
