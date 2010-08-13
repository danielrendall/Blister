package uk.co.sromo.blister;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:45:07
 * To change this template use File | Settings | File Templates.
 */
public class BPArray extends BPExpandableItem {
    private final int[] arrayItemOffsets;
    private final BPItem[] items;

    BPArray(int[] arrayItemOffsets) {
        //To change body of created methods use File | Settings | File Templates.
        this.arrayItemOffsets = arrayItemOffsets;
        items = new BPItem[arrayItemOffsets.length];
        for (int i=0; i< items.length; i++) {
            items[i] = BPNull.Instance;
        }

    }

    @Override
    protected void doExpand(BinaryPlistDecoder decoder) {
        for (int i=0; i< arrayItemOffsets.length; i++) {
            int itemOffset = arrayItemOffsets[i];
            BPItem item = decoder.getItemAtIndex(itemOffset);
            items[i] = item;
        }
    }

    @Override
    public Type type() {
        return Type.Array;
    }

    @Override
    public void accept(BPVisitor visitor) {
        visitor.visit(this);
    }
}
