package uk.co.sromo.blister;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 13-Aug-2010
 * Time: 19:12:03
 * To change this template use File | Settings | File Templates.
 */
public class BPBoolean extends BPItem {

    public final static BPBoolean TRUE = new BPBoolean(true);
    public final static BPBoolean FALSE = new BPBoolean(false);

    private final boolean value;

    private BPBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public Type type() {
        return Type.Boolean;
    }

    @Override
    public void accept(BPVisitor visitor) {
        visitor.visit(this);
    }
}
