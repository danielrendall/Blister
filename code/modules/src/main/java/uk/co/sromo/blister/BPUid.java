package uk.co.sromo.blister;

import uk.co.sromo.blister.BPItem;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:44:46
 * To change this template use File | Settings | File Templates.
 */
public class BPUid extends BPItem {
    private final byte[] data;

    public BPUid(byte[] data) {
        log.warn("BPUid not implemented");
        this.data = data;
    }

    @Override
    public String toString() {
        return "BPUid{" +
                "data=" + data +
                '}';
    }

    @Override
    public Type getType() {
        return Type.Uid;
    }

    @Override
    public void accept(BPVisitor visitor) {
        visitor.visit(this);
    }
}
