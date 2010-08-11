package uk.co.sromo.blister.objects;

import uk.co.sromo.blister.BPItem;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 10-Aug-2010
 * Time: 21:44:21
 * To change this template use File | Settings | File Templates.
 */
public class BPStringUnicode implements BPItem {
    private final Charset UTF8 = Charset.forName("UTF8");

    private final String data;

    public BPStringUnicode(byte[] data) {

        this.data = UTF8.decode(ByteBuffer.wrap(data)).toString();
    }

    public String getData() {
        return data;
    }
}
