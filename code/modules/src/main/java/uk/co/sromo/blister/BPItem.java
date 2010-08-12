package uk.co.sromo.blister;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:40:06
 * To change this template use File | Settings | File Templates.
 */
public abstract class BPItem {

    public void expand(BinaryPlistDecoder decoder) {
        // nothing to do
    }

    public boolean isExpanded() {
        return true;
    }
}
