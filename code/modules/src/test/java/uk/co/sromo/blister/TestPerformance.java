package uk.co.sromo.blister;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests which aren't "real" tests but which measure performance and do other non-test things. This
 * class should be marked as ignored.
 * @author Daniel Rendall
 */
@Ignore
public class TestPerformance {


    @Test
    public void TestBPStringTypeIdentificationTime() {
        // takes about 3200 with the encode / decode
        long start = System.currentTimeMillis();
        for (int i=0; i< 1000000; i++) {
            BPString s1 = BPString.get(TestBPItems.ASCII_STRING_1 + i);
            BPString s2 = BPString.get(TestBPItems.UNICODE_STRING_1 + i);
        }
        long end = System.currentTimeMillis();
        System.out.println("Took " + (end - start) + " ms" );
    }


}
