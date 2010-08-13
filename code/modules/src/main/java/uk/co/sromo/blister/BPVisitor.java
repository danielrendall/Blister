package uk.co.sromo.blister;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 13-Aug-2010
 * Time: 22:39:04
 * To change this template use File | Settings | File Templates.
 */
public interface BPVisitor {

    public void visit(BPArray item);
    public void visit(BPBoolean item);
    public void visit(BPData item);
    public void visit(BPDate item);
    public void visit(BPDict item);
    public void visit(BPInt item);
    public void visit(BPNull item);
    public void visit(BPReal item);
    public void visit(BPSet item);
    public void visit(BPString item);
    public void visit(BPUid item);

}
