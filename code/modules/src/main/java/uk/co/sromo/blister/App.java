package uk.co.sromo.blister;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Hello world!
 *
 */
public class App {
    public static String BINARY_PLIST = "/home/daniel/Development/Blister/resources/samples/BinaryInfo.plist";
    private final static Logger log = Logger.getLogger(App.class);


    public static void main(String[] args) throws Exception {
        File f = new File(BINARY_PLIST);
        byte[] bytes = FileUtils.readFileToByteArray(f);
        log.debug("There were " + bytes.length + " bytes");
        BinaryPlistDecoder bp = new BinaryPlistDecoder(bytes);
        bp.dump();
        bp.decode();
    }
}
