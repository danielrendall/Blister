package uk.co.sromo.blister;

import uk.co.sromo.blister.BPExpandableItem;
import uk.co.sromo.blister.BPItem;
import uk.co.sromo.blister.BinaryPlistDecoder;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:45:25
 * To change this template use File | Settings | File Templates.
 */
public class BPSet extends BPExpandableItem {
    private final int[] setItemOffsets;
    private final Set<BPItem> items;

    public BPSet(int[] setItemOffsets) {
        //To change body of created methods use File | Settings | File Templates.
        this.setItemOffsets = setItemOffsets;
        items = new HashSet<BPItem>();
    }

    @Override
    protected void doExpand(BinaryPlistDecoder decoder) {
        for (int i=0; i< setItemOffsets.length; i++) {
            int itemOffset = setItemOffsets[i];
            BPItem item = decoder.getItemAtIndex(itemOffset);
            items.add(item);
        }
    }
}
