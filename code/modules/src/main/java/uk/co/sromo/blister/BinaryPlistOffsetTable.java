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

import java.util.logging.Logger;

/**
 * Represents the table of object offsets in a binary plist.
 *
 * TODO: Documentation
 */
class BinaryPlistOffsetTable {

    private final static Logger log = Logger.getLogger(BinaryPlistOffsetTable.class.getSimpleName());

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
            offsetArray[i] = BPInt.from(buffer).getValue() - 8; // offsets relative to start of data
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
            log.fine(String.format("%d: %d", i, offsets[i]));
        }
    }
}
