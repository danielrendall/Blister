package uk.co.sromo.blister;

import java.nio.ByteBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 12-Aug-2010
 * Time: 23:41:46
 * To change this template use File | Settings | File Templates.
 */
abstract class BinaryPlistOffsetReader {

    static BinaryPlistOffsetReader create(int byteSize) throws BinaryPlistException {
        switch (byteSize) {
            case 1:
                return new BinaryPlistOffsetReader1();
            case 2:
                return new BinaryPlistOffsetReader2();
            case 4:
                return new BinaryPlistOffsetReader4();
            default:
                throw new BinaryPlistException("Can't cope with " + byteSize + " size ints");
        }
    }

    abstract int getOffset(ByteBuffer bytes);


    private static class BinaryPlistOffsetReader1 extends BinaryPlistOffsetReader {
        @Override
        int getOffset(ByteBuffer bytes) {
            return 0x000000ff & bytes.get();
        }
    }

    private static class BinaryPlistOffsetReader2 extends BinaryPlistOffsetReader {
        @Override
        int getOffset(ByteBuffer bytes) {
            return 0x0000ffff & bytes.getShort();
        }
    }

    private static class BinaryPlistOffsetReader4 extends BinaryPlistOffsetReader {
        @Override
        int getOffset(ByteBuffer bytes) {
            return (int) bytes.getInt();
        }
    }
}
