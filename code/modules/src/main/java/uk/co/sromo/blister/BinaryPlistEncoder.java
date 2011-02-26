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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * TODO: Documentation
 */
public class BinaryPlistEncoder implements BPVisitor {

    private final static Logger log = Logger.getLogger(BinaryPlistEncoder.class.getSimpleName());
    private final static byte[] EMPTY = new byte[0];

    private final Map<Integer, byte[]> objectData = new LinkedHashMap<Integer, byte[]>(); // object ref -> bytes for that object
    private final Set<Integer> collectionTypes = new HashSet<Integer>(); // object refs of collection types which need special treatment
    private final Map<BPItem, Integer> objectRefs = new LinkedHashMap<BPItem, Integer>(); // BPItem -> object ref
    int currentObjectRef = 0;

    public void visit(BPArray item) {
        if (!objectRefs.containsKey(item)) {
            // store this with an empty representation so that it gets a reference number
            int myRef = store(item, EMPTY);
            // ensure all the children are stored
            for(BPItem child : item) {
                child.accept(this);
            }
            // so now we have object references for all children.
            // we can store the array (temporarily) with 4-byte int offsets.
            ByteBuffer bb = ByteBuffer.allocate(item.size() * 4 + 1);
            // identify the object type for later...
            bb.put((byte) BinaryPlist.ARRAY);
            for(BPItem child : item) {
                if (!objectRefs.containsKey(child)) {
                    throw new EncodingException("Expected an object ref for " + child + " after visiting array");
                }
                bb.putInt(objectRefs.get(child));
            }
            // now we store the list of item references - note that this is not the true representation of the array
            objectData.put(myRef, bb.array());
            collectionTypes.add(myRef);
        }
    }

    public void visit(BPBoolean item) {
        if (!objectRefs.containsKey(item)) {
            byte[] bytes = new byte[] { item.getValue() ? (byte) BinaryPlist.BOOL_TRUE : (byte) BinaryPlist.BOOL_FALSE};
            store(item, bytes);
        }
    }

    public void visit(BPData item) {
        if (!objectRefs.containsKey(item)) {
            byte[] itemBytes = item.getData();
            byte[] itemHeader = getObjectHeader(BinaryPlist.DATA, itemBytes.length);
            store(item, itemHeader, itemBytes);
        }
    }

    public void visit(BPDate item) {
        if (!objectRefs.containsKey(item)) {
            // TODO - implement this properly
            byte[] bytes = new byte[] {(byte)BinaryPlist.DATE, 0,0,0,0,0,0,0,0};
            store(item, bytes);
        }
    }

    public void visit(BPDict item) {
        if (!objectRefs.containsKey(item)) {
            // store this with an empty representation so that it gets a reference number
            int myRef = store(item, EMPTY);
            // ensure all the children are stored
            for(BPString key : item.keySet()) {
                key.accept(this);
                BPItem value = item.get(key);
                value.accept(this);
            }
            // so now we have object references for all children.
            // we can store the array (temporarily) with 4-byte int offsets.
            ByteBuffer bb = ByteBuffer.allocate(item.size() * 8 + 1);
            // identify the object type for later...
            bb.put((byte) BinaryPlist.DICT);
            for(BPString key : item.keySet()) {
                if (!objectRefs.containsKey(key)) {
                    throw new EncodingException("Expected an object ref for key " + key + " after visiting dict");
                }
                bb.putInt(objectRefs.get(key));
            }

            for(BPString key : item.keySet()) {
                BPItem value = item.get(key);
                if (!objectRefs.containsKey(value)) {
                    throw new EncodingException("Expected an object ref for value " + value + " after visiting dict");
                }
                bb.putInt(objectRefs.get(value));
            }
            // now we store the list of item references - note that this is not the true representation of the array
            objectData.put(myRef, bb.array());
            collectionTypes.add(myRef);
        }
    }

    public void visit(BPInt item) {
        if (!objectRefs.containsKey(item)) {
            long lValue = item.getLongValue();
            if (lValue < 0 || item.getSize() == BPInt.Size.Long) {
                store(item, new byte[] {(byte) (BinaryPlist.INT | 0x03),
                        (byte) ((lValue >> 56) & 0xff),
                        (byte) ((lValue >> 48) & 0xff),
                        (byte) ((lValue >> 40) & 0xff),
                        (byte) ((lValue >> 32) & 0xff),
                        (byte) ((lValue >> 24) & 0xff),
                        (byte) ((lValue >> 16) & 0xff),
                        (byte) ((lValue >> 8) & 0xff),
                        (byte) (lValue & 0xff) });

            } else {
                int value = item.getValue();
                if (value < 256) {
                     store(item, new byte[] {(byte) (BinaryPlist.INT | 0x00),
                            (byte) value});
                } else if (value < 65536) {
                     store(item, new byte[] {(byte) (BinaryPlist.INT | 0x01),
                            (byte) ((value >> 8) & 0xff),
                            (byte) (value & 0xff) });
                } else {
                     store(item, new byte[] {(byte) (BinaryPlist.INT | 0x02),
                            (byte) ((value >> 24) & 0xff),
                            (byte) ((value >> 16) & 0xff),
                            (byte) ((value >> 8) & 0xff),
                            (byte) (value & 0xff) });
                }
            }
        }
    }

    public void visit(BPNull item) {
        if (!objectRefs.containsKey(item)) {
            store(item, new byte[] {(byte) BinaryPlist.NULL});
        }
    }

    public void visit(BPReal item) {
        if (!objectRefs.containsKey(item)) {
            // TODO - implement this properly
            store(item, new byte[] {(byte) (BinaryPlist.REAL | 0x00), 0});
        }
    }

    public void visit(BPSet item) {
        if (!objectRefs.containsKey(item)) {
            // store this with an empty representation so that it gets a reference number
            int myRef = store(item, EMPTY);
            // ensure all the children are stored
            for(BPItem child : item) {
                child.accept(this);
            }
            // so now we have object references for all children.
            // we can store the array (temporarily) with 4-byte int offsets.
            ByteBuffer bb = ByteBuffer.allocate(item.size() * 4 + 1);
            // identify the object type for later...
            bb.put((byte) BinaryPlist.SET);
            for(BPItem child : item) {
                if (!objectRefs.containsKey(child)) {
                    throw new EncodingException("Expected an object ref for " + child + " after visiting set");
                }
                bb.putInt(objectRefs.get(child));
            }
            // now we store the list of item references - note that this is not the true representation of the array
            objectData.put(myRef, bb.array());
            collectionTypes.add(myRef);
        }
    }

    public void visit(BPString item) {
        if (!objectRefs.containsKey(item)) {
            int length = item.length();
            byte[] bytes = item.asBytes(); // can't use length of this - length needs to be number of chars
            byte[] itemHeader = getObjectHeader(item.bpType(), length);

            store(item, itemHeader, bytes);
        }
    }

    public void visit(BPUid item) {
        if (!objectRefs.containsKey(item)) {
            byte[] bytes = item.getValue();
            if (bytes.length > 16) {
                throw new EncodingException("UID too long - " + bytes.length);
            }
            byte lengthMinus1 = (byte) (bytes.length - 1);
            byte[] header = new byte[] {(byte) (BinaryPlist.UID | lengthMinus1)};
            store(item, header, bytes);
        }
    }

    private int store(BPItem item, byte[] representation) {
        int ref = currentObjectRef;
        objectRefs.put(item, ref);
        objectData.put(ref, representation);
        currentObjectRef++;
        return ref;
    }

    private void store(BPItem item, byte[] itemHeader, byte[] itemBytes) {
        byte[] bytes = new byte[itemHeader.length + itemBytes.length];
        System.arraycopy(itemHeader, 0, bytes, 0, itemHeader.length);
        System.arraycopy(itemBytes, 0, bytes, itemHeader.length, itemBytes.length);
        store(item, bytes);
    }


    private byte[] getObjectHeader(short objectType, int length) {
        if (length < 15) {
            short sLength = (short) length;
            return new byte[] {(byte) (objectType | sLength)};
        } else {
            byte objectHeaderByte = (byte) (objectType | 0x0f);
            if (length < 256) {
                return new byte[] {objectHeaderByte,
                        (byte) (BinaryPlist.INT | 0x00),
                        (byte) length};
            } else if (length < 65536) {
                return new byte[] {objectHeaderByte,
                        (byte) (BinaryPlist.INT | 0x01),
                        (byte) ((length >> 8) & 0xff),
                        (byte) (length & 0xff) };
            } else {
                return new byte[] {objectHeaderByte,
                        (byte) (BinaryPlist.INT | 0x02),
                        (byte) ((length >> 24) & 0xff),
                        (byte) ((length >> 16) & 0xff),
                        (byte) ((length >> 8) & 0xff),
                        (byte) (length & 0xff) };
            }
        }
    }

    byte[] getBytes() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            writeInt(baos, (int)BinaryPlistHeader.MAGIC_1);
            writeInt(baos, (int)BinaryPlistHeader.MAGIC_2);
            int offset = 8;
            int numberOfObjects = objectData.size();
            int[] offsets = new int[objectData.size()];
            int objectRefSize = numberOfObjects < 256 ? 1 : (numberOfObjects < 65536 ? 2 : 4);
            int i=0;
            for (Map.Entry<Integer, byte[]> item : objectData.entrySet()) {
                int objectRef = item.getKey();
                if (objectRef != i) {
                    throw new EncodingException("Out of sync - got object " + objectRef + " but expected " + i);
                }
                offsets[i] = offset;
                log.fine("Writing object " + objectRef + " at offset " + offset);
                byte[] objectData = item.getValue();
                if (collectionTypes.contains(objectRef)) {
                    objectData = resizeCollection(objectData, objectRefSize);
                }
                baos.write(objectData);
                offset += objectData.length;
                i++;
            }
            int offsetTableOffset = offset;
            int offsetIntSize = offsetTableOffset < 256 ? 1 : ((offsetTableOffset < 65536) ? 2 : 4);
            switch (offsetIntSize) {
                case 1:
                    for (int j=0; j< numberOfObjects; j++) {
                        int anOffset = offsets[j];
                        if (anOffset > 255) throw new EncodingException("Can't encode " + anOffset + " in one byte");
                        baos.write((byte)(anOffset & 0xff));
                    }
                    break;
                case 2:
                    for (int j=0; j< numberOfObjects; j++) {
                        int anOffset = offsets[j];
                        if (anOffset > 65535) throw new EncodingException("Can't encode " + anOffset + " in one short");
                        writeShort(baos, (short) (anOffset & 0xffff));
                    }
                    break;
                default:
                    for (int j=0; j< numberOfObjects; j++) {
                        int anOffset = offsets[j];
                        writeInt(baos, anOffset);
                    }
                    break;
            }
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
            baos.write(0);
            baos.write(0);
            baos.write(0);
            baos.write(0);
            baos.write(0);
            baos.write(0); // sort version
            baos.write((byte) offsetIntSize);
            baos.write((byte) objectRefSize);
            writeLong(baos, (long) numberOfObjects);
            writeLong(baos, 0L);
            writeLong(baos, (long) offsetTableOffset);


            return baos.toByteArray();
        } catch (IOException e) {
            throw new EncodingException("Couldn't write bytes - " + e.getMessage());
        }

    }

    byte[] resizeCollection(byte[] collectionData, int objectRefSize) throws IOException {
        ByteBuffer input = ByteBuffer.wrap(collectionData);
        ByteArrayOutputStream output = new ByteArrayOutputStream(collectionData.length);
        short type = (short)(0xff & input.get());
        int numberOfItems = (collectionData.length - 1) / 4; // number of object references to read
        int collectionSize = (type == BinaryPlist.DICT) ?  numberOfItems / 2 : numberOfItems;
        log.fine("Resizing collection of type " + type + " size " + collectionSize + " with " + numberOfItems + " pointers");
        output.write(getObjectHeader(type, collectionSize));
        switch (objectRefSize) {
            case 1:
                for (int i=0; i< numberOfItems; i++) {
                    int objectRef = input.getInt();
                    if (objectRef > 255) throw new EncodingException("Can't encode " + objectRef + " in one byte");
                    output.write((byte)(objectRef & 0xff));
                }
                break;
            case 2:
                for (int i=0; i< numberOfItems; i++) {
                    int objectRef = input.getInt();
                    if (objectRef > 65535) throw new EncodingException("Can't encode " + objectRef + " in one short");
                    writeShort(output, (short) (objectRef & 0xffff));
                }
                break;
            default:
                for (int i=0; i< numberOfItems; i++) {
                    int objectRef = input.getInt();
                    writeInt(output, objectRef);
                }
                break;
        }
        return output.toByteArray();
    }

    void writeShort(ByteArrayOutputStream baos, short s) {
        baos.write((byte) ((s >> 8) & 0xff));
        baos.write((byte) (s & 0xff));
    }

    void writeInt(ByteArrayOutputStream baos, int i) {
        baos.write((byte) ((i >> 24) & 0xff));
        baos.write((byte) ((i >> 16) & 0xff));
        baos.write((byte) ((i >> 8) & 0xff));
        baos.write((byte) (i & 0xff));
    }

    void writeLong(ByteArrayOutputStream baos, long l) {
        baos.write((byte) ((l >> 56) & 0xff));
        baos.write((byte) ((l >> 48) & 0xff));
        baos.write((byte) ((l >> 40) & 0xff));
        baos.write((byte) ((l >> 32) & 0xff));
        baos.write((byte) ((l >> 24) & 0xff));
        baos.write((byte) ((l >> 16) & 0xff));
        baos.write((byte) ((l >> 8) & 0xff));
        baos.write((byte) (l & 0xff));
    }

    public static class EncodingException extends RuntimeException {
        public EncodingException(String message) {
            super(message);    //To change body of overridden methods use File | Settings | File Templates.
        }

        public EncodingException() {
            super();    //To change body of overridden methods use File | Settings | File Templates.
        }
    }
}
