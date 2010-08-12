package uk.co.sromo.blister;

import org.apache.log4j.Logger;
import uk.co.sromo.blister.objects.BPInt;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 12-Aug-2010
 * Time: 23:10:01
 * To change this template use File | Settings | File Templates.
 */
public class BinaryPlistOffsetTable {

    private final static Logger log = Logger.getLogger(BinaryPlistOffsetTable.class);

    private final int[] offsets;
    private final int size;

    public static BinaryPlistOffsetTable build(byte[] data, int offsetSize) throws Exception {
        if (data.length % offsetSize != 0) {
            throw new Exception("Length of data not commensurate with offset size");
        }
        final int numberOfOffsets = data.length / offsetSize;
        int[] offsetArray = new int[numberOfOffsets];
        byte[] buffer = new byte[offsetSize];

        for (int i=0; i < numberOfOffsets; i++) {
            System.arraycopy(data, i * offsetSize, buffer, 0, offsetSize);
            offsetArray[i] = new BPInt(buffer).getData() - 8; // offsets relative to start of data
        }
        return new BinaryPlistOffsetTable(offsetArray);
    }

    public BinaryPlistOffsetTable(int[] offsets) {
        this.offsets = offsets;
        this.size = offsets.length;
    }

    public int get(int offset) {
        return offsets[offset];
    }

    public int get(long offset) {
        return offsets[(int)offset];
    }

    public int getSize() {
        return size;
    }

    public void dump() {
        for (int i=0; i< size; i++) {
            log.debug(String.format("%d: %d", i, offsets[i]));
        }
    }
}
