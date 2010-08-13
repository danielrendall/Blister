package uk.co.sromo.blister;

import org.apache.log4j.Logger;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 12-Aug-2010
 * Time: 22:35:55
 * To change this template use File | Settings | File Templates.
 */
class BinaryPlistHeader {

    private final static Logger log = Logger.getLogger(BinaryPlistHeader.class);
    private final static Formatter formatter = new Formatter(Locale.UK);

    public final static long MAGIC_1 = 0x62706c69; // bpli
    public final static long MAGIC_2 = 0x73740000; // st..

    private final int fileFormatVersion;

    static BinaryPlistHeader build(byte[] data) throws BinaryPlistException {
        ByteArrayWrapper bytes = new ByteArrayWrapper(data);
        long first = bytes.readInt();
        long second = bytes.readInt();
        long secondMasked = second & 0xffff0000;
        if ((first != MAGIC_1) || (secondMasked != MAGIC_2)) {
            log.warn("Magic numbers wrong - were " + formatter.format("%1$2x %2$2x", first, second));
            throw new BinaryPlistException("Bad magic number");
        }
        return new BinaryPlistHeader((int)(second & 0x0000ffff));
    }

    private BinaryPlistHeader(int fileFormatVersion) {
        this.fileFormatVersion = fileFormatVersion;
    }


    int getFileFormatVersion() {
        return fileFormatVersion;
    }
}
