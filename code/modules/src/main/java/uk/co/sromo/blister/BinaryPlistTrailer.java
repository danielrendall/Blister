package uk.co.sromo.blister;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 12-Aug-2010
 * Time: 22:41:27
 * To change this template use File | Settings | File Templates.
 */
public class BinaryPlistTrailer {

    /**
     * http://www.opensource.apple.com/source/CF/CF-550/ForFoundationOnly.h
     * <p/>
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
    public static BinaryPlistTrailer build(byte[] data) {
        ByteArrayWrapper bytes = new ByteArrayWrapper(data);
        bytes.skip(5);
        short _sortVersion = bytes.readByte();
        short _offsetIntSize = bytes.readByte();
        short _objectRefSize = bytes.readByte();
        long _numObjects = bytes.readLong();
        long _topObject = bytes.readLong();
        long _offsetTableOffset = bytes.readLong();
        return new BinaryPlistTrailer(_sortVersion, _offsetIntSize, _objectRefSize, _numObjects, _topObject, _offsetTableOffset);
    }

    private final short sortVersion;
    private final short offsetIntSize;
    private final short objectRefSize;
    private final long numObjects;
    private final long topObject;
    private final long offsetTableOffset;

    private BinaryPlistTrailer(short sortVersion, short offsetIntSize, short objectRefSize, long numObjects, long topObject, long offsetTableOffset) {
        //To change body of created methods use File | Settings | File Templates.
        this.sortVersion = sortVersion;
        this.offsetIntSize = offsetIntSize;
        this.objectRefSize = objectRefSize;
        this.numObjects = numObjects;
        this.topObject = topObject;
        this.offsetTableOffset = offsetTableOffset;
    }

    public long getNumObjects() {
        return numObjects;
    }

    public short getObjectRefSize() {
        return objectRefSize;
    }

    public short getOffsetIntSize() {
        return offsetIntSize;
    }

    public long getOffsetTableOffset() {
        return offsetTableOffset;
    }

    public short getSortVersion() {
        return sortVersion;
    }

    public long getTopObject() {
        return topObject;
    }
}
