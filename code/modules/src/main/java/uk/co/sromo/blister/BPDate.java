package uk.co.sromo.blister;

import uk.co.sromo.blister.BPItem;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:43:27
 * To change this template use File | Settings | File Templates.
 */
public class BPDate extends BPItem {
    
    private final byte[] data;

    public BPDate(byte[] data) {
        this.data = data;
        log.warning("BPDate not yet implemented");
    }

    @Override
    public String toString() {
        return "BPDate{" +
                "data=" + data +
                '}';
    }

    @Override
    public Type getType() {
        return Type.Date;
    }

    @Override
    public void accept(BPVisitor visitor) {
        visitor.visit(this);
    }
}
