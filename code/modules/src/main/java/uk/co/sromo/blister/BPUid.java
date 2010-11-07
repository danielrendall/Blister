package uk.co.sromo.blister;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:44:46
 * To change this template use File | Settings | File Templates.
 */
public class BPUid extends BPItem {
    private final byte[] value;

    public BPUid(byte[] value) {
        log.warning("BPUid not implemented");
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "BPUid{" +
                "value=" + value +
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
