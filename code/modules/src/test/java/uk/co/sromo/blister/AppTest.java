package uk.co.sromo.blister;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

/**
 * Unit test for simple App.
 */

public class AppTest {
    public static String BINARY_PLIST = "/home/daniel/Development/Blister/resources/samples/BinaryInfo.plist";
    private final static Logger log = Logger.getLogger(AppTest.class);

    @Test
    public void TestDJ1800() throws Exception {
        File f = new File(BINARY_PLIST);
        byte[] bytes = FileUtils.readFileToByteArray(f);
        log.debug("There were " + bytes.length + " bytes");
        BPItem bp = BinaryPlist.decode(bytes);
        BPVisitor dump = new DumpVisitor();
        log.info("Before: ");
        bp.accept(dump);
        byte[] newBytes = BinaryPlist.encode(bp);

        BPItem newRoot = BinaryPlist.decode(newBytes);
        log.info("After: ");
        newRoot.accept(dump);
    }

    @Test
    public void TestBuild() throws Exception {
        BPDict root = new BPDict()
                .with("key1", "value1")
                .with("key2", 14)
                .with("key3", true)
                .with("key4", new BPArray()
                    .with("another value")
                    .with(56)
                    .with(false))
                .with("key5", "finished");
        BPVisitor dump = new DumpVisitor();
        root.accept(dump);

    }

    @Test
    public void TestSimpleRoundTrip() throws Exception {
        BPDict root = new BPDict()
                .with("key1", "value1")
                .with("key2", 14)
                .with("key3", true)
                .with("key4", new BPArray()
                    .with("another value")
                    .with(56)
                    .with(false))
                .with("key5", "finished");
        BPVisitor dump = new DumpVisitor();
        log.info("Before: ");
        root.accept(dump);
        byte[] bytes = BinaryPlist.encode(root);

        dump(bytes);

        BPItem newRoot = BinaryPlist.decode(bytes);
        log.info("After");
        newRoot.accept(dump);
    }

    private void dump(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i< bytes.length; i++) {
            if (i > 0 && i % 16 == 0) {
                log.debug(sb.toString());
                sb = new StringBuilder();
            }
            byte b = bytes[i];
            sb.append(Integer.toHexString((0xff & b))).append(",");
        }
        log.debug(sb.toString());
    }

}
