package uk.co.sromo.blister;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 13-Aug-2010
 * Time: 23:38:59
 * To change this template use File | Settings | File Templates.
 */
public class DumpVisitor implements BPVisitor {
    private final static Logger log = Logger.getLogger(DumpVisitor.class);

    int depth = 0;
    String spaces = "                                         ";

    private void print(String aString) {
        log.debug(spaces.substring(0, depth) + aString);
    }

    public void visit(BPArray item) {
        print("<array>");
        depth++;
        for(BPItem child : item) {
            child.accept(this);
        }
        depth--;
        print("</array>");
    }

    public void visit(BPBoolean item) {
        print("<boolean>" + item + "</boolean>");
    }

    public void visit(BPData item) {
        print("<data>" + item + "</data>");
    }

    public void visit(BPDate item) {
        print("<date>" + item + "</date>");
    }

    public void visit(BPDict item) {
        print("<dict>");
        depth++;
        for(BPString child : item.keySet()) {
            print("<key>" + child + "</key>");
            item.get(child).accept(this);
        }
        depth--;
        print("</dict>");
    }

    public void visit(BPInt item) {
        print("<int>" + item + "</int>");
    }

    public void visit(BPNull item) {
        print("<null/>");
    }

    public void visit(BPReal item) {
        print("<real>" + item + "</real>");
    }

    public void visit(BPSet item) {
        print("<set>");
        depth++;
        for(BPItem child : item) {
            child.accept(this);
        }
        depth--;
        print("</set>");
    }

    public void visit(BPString item) {
        print("<string>" + item + "</string>");
    }

    public void visit(BPUid item) {
        print("<uid>" + item + "</uid>");
    }
}
