package uk.co.sromo.blister.objects;

import uk.co.sromo.blister.BPItem;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:44:46
 * To change this template use File | Settings | File Templates.
 */
public class BPUid implements BPItem {
    private final byte[] data;

    public BPUid(byte[] data) {
        //To change body of created methods use File | Settings | File Templates.
        this.data = data;
    }
}
