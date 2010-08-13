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

    public boolean value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BPBoolean bpBoolean = (BPBoolean) o;

        if (value != bpBoolean.value) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (value ? 1 : 0);
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    @Override
    public Type getType() {
        return Type.Boolean;
    }

    @Override
    public void accept(BPVisitor visitor) {
        visitor.visit(this);
    }
}
