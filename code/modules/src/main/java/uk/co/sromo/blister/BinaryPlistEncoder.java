package uk.co.sromo.blister;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 14-Aug-2010
 * Time: 08:16:31
 * To change this template use File | Settings | File Templates.
 */
public class BinaryPlistEncoder implements BPVisitor {

    private final static Logger log = Logger.getLogger(BinaryPlistEncoder.class);
    private final static byte[] EMPTY = new byte[0];

    private final Map<Integer, byte[]> objectData = new LinkedHashMap<Integer, byte[]>(); // object ref -> bytes for that object
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
        }
    }

    public void visit(BPInt item) {
        if (!objectRefs.containsKey(item)) {
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

    public byte[] getBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int[] offsets = new int[objectData.size()];


        return new byte[0];  //To change body of created methods use File | Settings | File Templates.
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
