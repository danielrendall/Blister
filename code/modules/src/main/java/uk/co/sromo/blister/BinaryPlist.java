/*
 * Copyright 2011 Daniel Rendall
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.sromo.blister;

import uk.co.sromo.blister.util.DumpVisitor;
/**
 * This class is the main entrypoint for using Blister to encode a BPlist
 * as a byte array, or to decode a byte array into a BPlist.
 * The convenience method to dump a BPlist as XML may be useful.
 */
public class BinaryPlist {
    final static short NULL = (short) 0x00;
    final static short BOOL_FALSE = (short) 0x08;
    final static short BOOL_TRUE = (short) 0x09;
    final static short FILL = (short) 0x0f;
    final static short INT = (short) 0x10; // mask
    final static short REAL = (short) 0x20; // mask
    final static short DATE = (short) 0x33;
    final static short DATA = (short) 0x40; // mask
    final static short STRING_ASCII = (short) 0x50; // mask
    final static short STRING_UNICODE = (short) 0x60; // mask
    final static short UNUSED_1 = (short) 0x70; // mask
    final static short UID = (short) 0x80; // mask
    final static short UNUSED_2 = (short) 0x90; // mask
    final static short ARRAY = (short) 0xa0; // mask
    final static short UNUSED_3 = (short) 0xb0; // mask
    final static short SET = (short) 0xc0; // mask
    final static short DICT = (short) 0xd0; // mask
    final static short UNUSED_4 = (short) 0xe0; // mask
    final static short UNUSED_5 = (short) 0xf0; // mask

    /**
     * Decodes the supplied binary plist data and returns a BPItem representing the
     * root item of the plist, which you'll need to cast to the appropriate
     * type (most likely a BPDict)
     * TODO: Parameters
     */
    public static BPItem decode(byte[] rawData) throws BinaryPlistException {
        if (rawData.length < 40) {
            throw new BinaryPlistException("Byte array not long enough");
        }

        byte[] headerBytes = new byte[8];
        byte[] trailerBytes = new byte[32];

        System.arraycopy(rawData, 0, headerBytes, 0, 8);
        System.arraycopy(rawData, rawData.length - 32, trailerBytes, 0, 32);

        BinaryPlistHeader header = BinaryPlistHeader.build(headerBytes);
        BinaryPlistTrailer trailer = BinaryPlistTrailer.build(trailerBytes);

        int offset = (int) trailer.getOffsetTableOffset();

        int dataLength = offset - 8;
        int offsetTableLength = rawData.length - 32 - offset;

        byte[] dataBytes = new byte[dataLength];
        System.arraycopy(rawData, 8, dataBytes, 0, dataLength);

        byte[] offsetTableBytes = new byte[offsetTableLength];
        System.arraycopy(rawData, offset, offsetTableBytes, 0, offsetTableLength);
        BinaryPlistOffsetTable offsetTable = BinaryPlistOffsetTable.build(offsetTableBytes, trailer.getOffsetIntSize());

        BinaryPlistOffsetReader offsetReader = BinaryPlistOffsetReader.create(trailer.getObjectRefSize());


        BinaryPlistDecoder decoder = new BinaryPlistDecoder(header, trailer, dataBytes, offsetTable, offsetReader);

        return decoder.decode();

    }

    /**
     * Encodes the supplied plist (represented by its root BPItem) as binary
     * plist data.
     * TODO: Params
     */
    public static byte[] encode(BPItem root) throws BinaryPlistException {
        if (!root.canBeRoot()) {
            throw new BinaryPlistException("BPItem of type " + root.getType() + " can't be the root of a Binary PList");
        }

        BinaryPlistEncoder encoder = new BinaryPlistEncoder();
        root.accept(encoder);
        return encoder.getBytes();
    }

    /**
     * Dumps the supplied BPItem as XML which is then returned. Useful for debugging.
     * TODO: Params
     */
    public static String dump(BPItem root) {
        DumpVisitor dv = new DumpVisitor();
        root.accept(dv);
        return dv.getXml();
    }
}
