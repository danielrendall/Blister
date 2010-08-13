package uk.co.sromo.blister;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 13-Aug-2010
 * Time: 00:25:50
 * To change this template use File | Settings | File Templates.
 */
abstract class BPExpandableItem extends BPItem {

    private boolean expanded = false;

    @Override
    final void expand(BinaryPlistDecoder decoder) {
        if (!expanded) {
            doExpand(decoder);
            expanded = true;
        }
    }

    protected abstract void doExpand(BinaryPlistDecoder decoder);

    @Override
    final boolean isExpanded() {
        return expanded;
    }
}
