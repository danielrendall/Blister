package uk.co.sromo.blister;

import uk.co.sromo.blister.BPExpandableItem;
import uk.co.sromo.blister.BPItem;
import uk.co.sromo.blister.BinaryPlistDecoder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:45:45
 * To change this template use File | Settings | File Templates.
 */
public class BPDict extends BPExpandableItem {
    private final int[] keyOffsets;
    private final int[] valueOffsets;

    private final Map<BPItem, BPItem> map;

    public BPDict(int[] keyOffsets, int[] valueOffsets) {
        //To change body of created methods use File | Settings | File Templates.
        this.keyOffsets = keyOffsets;
        this.valueOffsets = valueOffsets;
        this.map = new LinkedHashMap<BPItem, BPItem>();
    }

    @Override
    public void doExpand(BinaryPlistDecoder decoder) {
        for (int i=0; i< keyOffsets.length; i++) {
            int keyOffset = keyOffsets[i];
            int valueOffset = valueOffsets[i];
            BPItem key = decoder.getItemAtIndex(keyOffset);
            BPItem value = decoder.getItemAtIndex(valueOffset);
            map.put(key, value);
        }
    }
}
