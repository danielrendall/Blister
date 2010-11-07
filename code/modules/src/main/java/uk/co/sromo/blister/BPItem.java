package uk.co.sromo.blister;


import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:40:06
 * To change this template use File | Settings | File Templates.
 */
public abstract class BPItem {
    protected final static Logger log = Logger.getLogger(BPItem.class.getSimpleName());

    public enum Type {Array, Boolean, Data, Date, Dict, Int, Null, Real, Set, String, Uid}

    void expand(BinaryPlistDecoder decoder) throws BinaryPlistException {
        // nothing to do
    }

    boolean canBeRoot() {
        return false;
    }

    boolean isExpanded() {
        return true;
    }

    public abstract Type getType();
    public abstract void accept(BPVisitor visitor);
}
