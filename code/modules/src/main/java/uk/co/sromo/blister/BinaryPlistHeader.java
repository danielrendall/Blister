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

import java.nio.ByteBuffer;
import java.util.Formatter;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Represents the header of a binary plist.
 *
 * TODO: Documentation
 */
class BinaryPlistHeader {

    private final static Logger log = Logger.getLogger(BinaryPlistHeader.class.getSimpleName());
    private final static Formatter formatter = new Formatter(Locale.UK);

    public final static long MAGIC_1 = 0x62706c69; // bpli
    public final static long MAGIC_2 = 0x73743030; // st..

    private final int fileFormatVersion;

    static BinaryPlistHeader build(byte[] data) throws BinaryPlistException {
        ByteBuffer bytes = ByteBuffer.wrap(data);
        long first = bytes.getInt();
        long second = bytes.getInt();
        if ((first != MAGIC_1) || (second != MAGIC_2)) {
            log.warning("Magic numbers wrong - were " + formatter.format("%1$2x %2$2x", first, second));
            throw new BinaryPlistException("Bad magic number");
        }
        return new BinaryPlistHeader((int)(second & 0x0000ffff));
    }

    private BinaryPlistHeader(int fileFormatVersion) {
        this.fileFormatVersion = fileFormatVersion;
    }


    int getFileFormatVersion() {
        return fileFormatVersion;
    }
}
