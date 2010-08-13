package uk.co.sromo.blister;

import uk.co.sromo.blister.BPExpandableItem;
import uk.co.sromo.blister.BPItem;
import uk.co.sromo.blister.BinaryPlistDecoder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:45:25
 * To change this template use File | Settings | File Templates.
 */
public class BPSet extends BPExpandableItem implements Set<BPItem> {
    private final int[] setItemOffsets;
    private final Set<BPItem> items = new HashSet<BPItem>();

    BPSet(int[] setItemOffsets) {
        //To change body of created methods use File | Settings | File Templates.
        this.setItemOffsets = setItemOffsets;
    }

    public BPSet() {
        setItemOffsets = EMPTY;
    }

    @Override
    protected void doExpand(BinaryPlistDecoder decoder) {
        for (int i=0; i< setItemOffsets.length; i++) {
            int itemOffset = setItemOffsets[i];
            BPItem item = decoder.getItemAtIndex(itemOffset);
            items.add(item);
        }
    }

    public BPSet with(BPItem item) {
        add(item);
        return this;
    }

    public BPSet with(String value) {
        return with(BPString.get(value));
    }

    public BPSet with(int value) {
        return with(BPInt.get(value));
    }

    public BPSet with(boolean value) {
        return with(BPBoolean.get(value));
    }

    public boolean add(BPItem bpItem) {
        return items.add(bpItem);
    }

    public boolean addAll(Collection<? extends BPItem> c) {
        return items.addAll(c);
    }

    public void clear() {
        items.clear();
    }

    public boolean contains(Object o) {
        return items.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return items.containsAll(c);
    }

    @Override
    public boolean equals(Object o) {
        return items.equals(o);
    }

    @Override
    public int hashCode() {
        return items.hashCode();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Iterator<BPItem> iterator() {
        return items.iterator();
    }

    public boolean remove(Object o) {
        return items.remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return items.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return items.retainAll(c);
    }

    public int size() {
        return items.size();
    }

    public Object[] toArray() {
        return items.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return items.toArray(a);
    }

    @Override
    public String toString() {
        return "BPSet{" +
                "items=" + items +
                '}';
    }

    @Override
    public Type getType() {
        return Type.Set;
    }

    @Override
    public void accept(BPVisitor visitor) {
        visitor.visit(this);
    }
}
