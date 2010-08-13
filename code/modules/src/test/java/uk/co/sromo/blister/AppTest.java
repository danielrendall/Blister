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
    public void TestDump() throws Exception {
        File f = new File(BINARY_PLIST);
        byte[] bytes = FileUtils.readFileToByteArray(f);
        log.debug("There were " + bytes.length + " bytes");
        BPItem bp = BinaryPlistDecoder.decode(bytes);
        BPVisitor dump = new DumpVisitor();
        bp.accept(dump);
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
}
