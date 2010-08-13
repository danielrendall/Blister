package uk.co.sromo.blister;

import uk.co.sromo.blister.BPItem;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:44:01
 * To change this template use File | Settings | File Templates.
 */
public class BPStringAscii extends BPItem {
    private final Charset ASCII = Charset.forName("ASCII");

    private final String data;

    public BPStringAscii(byte[] bytes) {
        this.data = ASCII.decode(ByteBuffer.wrap(bytes)).toString();
    }

    public String getData() {
        return data;
    }
}
