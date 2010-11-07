package uk.co.sromo.blister;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:43:11
 * To change this template use File | Settings | File Templates.
 */
public class BPReal extends BPItem {
    private final double value;

    public BPReal(byte[] bytes) {
        log.warning("BPReal not implemented");
        value = 0.0d;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BPReal bpReal = (BPReal) o;

        if (Double.compare(bpReal.value, value) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        long temp = value != +0.0d ? Double.doubleToLongBits(value) : 0L;
        return (int) (temp ^ (temp >>> 32));
    }

    @Override
    public Type getType() {
        return Type.Real;
    }

    @Override
    public void accept(BPVisitor visitor) {
        visitor.visit(this);
    }
}

