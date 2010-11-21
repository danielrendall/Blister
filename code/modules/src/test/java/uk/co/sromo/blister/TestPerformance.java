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


    /*
    This takes about 24s to process 10 million strings
    */
    /*
    @Test
    public void TestBPStringTypeIdentificationTimeOld() {
        // takes about 3200 with the encode / decode
        long start = System.currentTimeMillis();
        for (int i=0; i< 10000000; i++) {
            BPString s1 = BPString.get2(TestBPItems.ASCII_STRING_1 + i);
            BPString s2 = BPString.get2(TestBPItems.UNICODE_STRING_1 + i);
        }
        long end = System.currentTimeMillis();
        System.out.println("Old took " + (end - start) + " ms" );
    }
    public static BPString get2(String string) {
         String roundTripped = ASCII.decode(ASCII.encode(string)).toString();
         return get(string, roundTripped.equals(string) ? EncodingType.ASCII : EncodingType.UTF16);
     }
    */


    /*
    This takes about 11s to process 10 million strings
    */
    @Test
    public void TestBPStringTypeIdentificationTimeNew() {
        // takes about 3200 with the encode / decode
        long start = System.currentTimeMillis();
        for (int i=0; i< 10000000; i++) {
            BPString s1 = BPString.get(TestBPItems.ASCII_STRING_1 + i);
            BPString s2 = BPString.get(TestBPItems.UNICODE_STRING_1 + i);
        }
        long end = System.currentTimeMillis();
        System.out.println("New took " + (end - start) + " ms" );
    }

}
