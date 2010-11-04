package uk.co.sromo.blister;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daniel Rendall
 */
public class TestNumbers {

    @Test
    public void testByteMinAndMax() {
        byte bMin = Byte.MIN_VALUE;
        byte bMax = Byte.MAX_VALUE;

        int iMin = -128;
        int iMax = 127;

        Assert.assertEquals(bMin, iMin);
        Assert.assertEquals(bMax, iMax);
    }

    @Test
    public void testIntMinAndMax() {
        int iMin = Integer.MIN_VALUE;
        int iMax = Integer.MAX_VALUE;

        long lMin = -2147483648L;
        long lMax = 2147483647L;

        Assert.assertEquals(iMin, lMin);
        Assert.assertEquals(iMax, lMax);
    }

    @Test
    public void testByteArray() {

        long bMin = -128L;
        long bMax = 127L;

        Assert.assertArrayEquals(new byte[] {Byte.MIN_VALUE, Byte.MAX_VALUE}, new byte[] {(byte) bMin, (byte) bMax});
    }

    @Test
    public void testPositiveAndNegative() {

        byte b1 = (byte)-128;
        byte b2 = (byte)-127;
        byte b3 = (byte)-1;
        byte b4 = (byte)0;
        byte b5 = (byte)1;
        byte b6 = (byte)126;
        byte b7 = (byte)127;

        Assert.assertTrue(((0x80 & b1) == 128));
        Assert.assertTrue(((0x80 & b2) == 128));
        Assert.assertTrue(((0x80 & b3) == 128));
        Assert.assertFalse(((0x80 & b4) == 128));
        Assert.assertFalse(((0x80 & b5) == 128));
        Assert.assertFalse(((0x80 & b6) == 128));
        Assert.assertFalse(((0x80 & b7) == 128));
    }
}
