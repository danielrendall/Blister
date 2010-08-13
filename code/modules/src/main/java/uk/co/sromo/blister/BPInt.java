package uk.co.sromo.blister;

import uk.co.sromo.blister.BPItem;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:42:57
 * To change this template use File | Settings | File Templates.
 */
public class BPInt extends BPItem {
    private final int data;

    public BPInt(byte[] bytes) {
        int size = bytes.length;
        int _data = 0;
        if (size == 1) {
            _data |= (0xff & bytes[0]);
        } else if (size == 2) {
            _data |= ((0xff & bytes[0]) << 8) | (0xff & bytes[1]);
        } else if (size == 4) {
            _data |= ((0xff & bytes[0]) << 24) | ((0xff & bytes[1]) << 16) | ((0xff & bytes[2]) << 8) | (0xff & bytes[3]);
        } else {
            throw new RuntimeException("Wasn't expecting an int to be " + size + " bytes");
        }
        this.data = _data;
    }

    public int getData() {
        return data;
    }

    @Override
    public Type type() {
        return Type.Int;
    }

    @Override
    public void accept(BPVisitor visitor) {
        visitor.visit(this);
    }
}
