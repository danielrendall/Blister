package uk.co.sromo.blister;

import uk.co.sromo.blister.BPItem;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:43:11
 * To change this template use File | Settings | File Templates.
 */
public class BPReal extends BPItem {
    private final double data;

    public BPReal(byte[] bytes) {
        // TODO - fixme
        data = 0.0d;
    }

    @Override
    public Type type() {
        return Type.Real;
    }

    @Override
    public void accept(BPVisitor visitor) {
        visitor.visit(this);
    }
}

