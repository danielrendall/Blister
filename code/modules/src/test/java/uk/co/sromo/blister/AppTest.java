package uk.co.sromo.blister;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Unit test for simple App.
 */
public class AppTest {
    public static String BINARY_PLIST = "/home/daniel/Development/Blister/resources/samples/BinaryInfo.plist";
    private final static Logger log = Logger.getLogger(AppTest.class);


    public static void main(String[] args) throws Exception {
        File f = new File(BINARY_PLIST);
        byte[] bytes = FileUtils.readFileToByteArray(f);
        log.debug("There were " + bytes.length + " bytes");
        BPItem bp = BinaryPlistDecoder.decode(bytes);
        BPVisitor dump = new DumpVisitor();
        bp.accept(dump);
    }
}
