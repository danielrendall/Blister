package uk.co.sromo.blister;

import java.nio.ByteBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 12-Aug-2010
 * Time: 22:41:27
 * To change this template use File | Settings | File Templates.
 */
class BinaryPlistTrailer {

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
    static BinaryPlistTrailer build(byte[] data) {
        ByteBuffer bytes = ByteBuffer.wrap(data);
        bytes.position(5);
        short _sortVersion = bytes.get();
        short _offsetIntSize = bytes.get();
        short _objectRefSize = bytes.get();
        long _numObjects = bytes.getLong();
        long _topObject = bytes.getLong();
        long _offsetTableOffset = bytes.getLong();
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

    long getNumObjects() {
        return numObjects;
    }

    short getObjectRefSize() {
        return objectRefSize;
    }

    short getOffsetIntSize() {
        return offsetIntSize;
    }

    long getOffsetTableOffset() {
        return offsetTableOffset;
    }

    short getSortVersion() {
        return sortVersion;
    }

    long getTopObject() {
        return topObject;
    }
}
