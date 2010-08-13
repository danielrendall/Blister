package uk.co.sromo.blister;

import uk.co.sromo.blister.BPItem;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:43:42
 * To change this template use File | Settings | File Templates.
 */
public class BPData extends BPItem {
    private final byte[] data;

    public BPData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "BPData{" +
                "data=" + data +
                '}';
    }

    @Override
    public Type getType() {
        return Type.Data;
    }

    @Override
    public void accept(BPVisitor visitor) {
        visitor.visit(this);
    }
}
