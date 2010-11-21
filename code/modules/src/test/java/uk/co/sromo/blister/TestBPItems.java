package uk.co.sromo.blister;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import uk.co.sromo.blister.util.DumpVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 14-Aug-2010
 * Time: 19:48:40
 * To change this template use File | Settings | File Templates.
 */
public class TestBPItems {


    private final static Logger log = Logger.getLogger(TestBPItems.class.getSimpleName());

    public final static String ASCII_STRING_1 = "Quite Interesting";
    public final static String ASCII_STRING_2 = "RatherDull";
    public final static String ASCII_STRING_3 = "Normal ASCII string 1234567890";

    public final static String UNICODE_STRING_1 = "Ỡuitὲ InƬerestіng";
    public final static String UNICODE_STRING_2 = "ṜaŦherḒull";
    public final static String UNICODE_STRING_3 = "Non-exotic non-ASCII string ©®ÀÈÌÒÙ";

    @Test
    public void TestBPStringTypeIdentification() {
        BPString ascii1 = BPString.get(ASCII_STRING_1);
        Assert.assertEquals("String wasn't ASCII", BPString.EncodingType.ASCII, ascii1.getEncodingType());
        BPString unicode1 = BPString.get(UNICODE_STRING_1);
        Assert.assertEquals("String wasn't Unicode", BPString.EncodingType.UTF16, unicode1.getEncodingType());
        BPString ascii2 = BPString.get(ASCII_STRING_2);
        Assert.assertEquals("String wasn't ASCII", BPString.EncodingType.ASCII, ascii2.getEncodingType());
        BPString unicode2 = BPString.get(UNICODE_STRING_2);
        Assert.assertEquals("String wasn't Unicode", BPString.EncodingType.UTF16, unicode2.getEncodingType());
        BPString ascii3 = BPString.get(ASCII_STRING_3);
        Assert.assertEquals("String wasn't ASCII", BPString.EncodingType.ASCII, ascii3.getEncodingType());
        BPString unicode3 = BPString.get(UNICODE_STRING_3);
        Assert.assertEquals("String wasn't Unicode", BPString.EncodingType.UTF16, unicode3.getEncodingType());
    }

    @Test
    public void TestReadingBinaryUnicode() throws IOException, BinaryPlistException {
        InputStream stream = TestBPItems.class.getResourceAsStream("/BinaryUnicode.plist");
        byte[] bytes = IOUtils.toByteArray(stream);
        BPItem root = BinaryPlist.decode(bytes);
        Assert.assertEquals("Not a dictionary", BPItem.Type.Dict, root.getType());

        BPDict newDict = (BPDict) root;

        Assert.assertEquals("New ascii lookup failed", ASCII_STRING_1, newDict.get(ASCII_STRING_2, "FAIL"));
        Assert.assertEquals("New unicode lookup failed", UNICODE_STRING_1, newDict.get(UNICODE_STRING_2, "FAIL"));
    }

    @Test
    public void TestPrimitiveTypesInDictionary() throws BinaryPlistException {
        BPDict dict = new BPDict()
                .with(ASCII_STRING_2, ASCII_STRING_1)
                .with(UNICODE_STRING_2, UNICODE_STRING_1);
        Assert.assertEquals("Ascii lookup failed", ASCII_STRING_1, dict.get(ASCII_STRING_2, "FAIL"));
        Assert.assertEquals("Unicode lookup failed", UNICODE_STRING_1, dict.get(UNICODE_STRING_2, "FAIL"));

        byte[] bytes = BinaryPlist.encode(dict);

        BPItem root = BinaryPlist.decode(bytes);
        Assert.assertEquals("Not a dictionary", BPItem.Type.Dict, root.getType());

        BPDict newDict = (BPDict) root;

        Assert.assertEquals("New ascii lookup failed", ASCII_STRING_1, newDict.get(ASCII_STRING_2, "FAIL"));
        Assert.assertEquals("New unicode lookup failed", UNICODE_STRING_1, newDict.get(UNICODE_STRING_2, "FAIL"));
    }

    @Test
    public void TestRoundTripping() throws BinaryPlistException {
        BPDict dict = new BPDict()
                .with("key1", "value1")
                .with("key2", 14)
                .with("key3", true)
                .with("key4", new BPArray()
                    .with("another value")
                    .with(56)
                    .with(false))
                .with("key5", "finished");
        byte[] bytes = BinaryPlist.encode(dict);

        BPItem root = BinaryPlist.decode(bytes);
        Assert.assertEquals("Not a dictionary", BPItem.Type.Dict, root.getType());
        BPDict newDict = (BPDict) root;

        Assert.assertEquals(dict.get("key1", ""), newDict.get("key1", "FAIL"));
        Assert.assertEquals(dict.get("key2", 0), newDict.get("key2", -1));
        Assert.assertEquals(dict.get("key3", true), newDict.get("key3", false));
        Assert.assertEquals(dict.get("key5", ""), newDict.get("key5", "FAIL"));
        BPItem obj1 = dict.get("key4");
        BPItem obj2 = newDict.get("key4");
        Assert.assertEquals(BPItem.Type.Array, obj1.getType());
        Assert.assertEquals(BPItem.Type.Array, obj2.getType());
        BPArray array1 = (BPArray) obj1;
        BPArray array2 = (BPArray) obj2;

        Assert.assertEquals(array1.get(0), array2.get(0));
        Assert.assertEquals(array1.get(1), array2.get(1));
        Assert.assertEquals(array1.get(2), array2.get(2));
    }

    @Test
    public void TestEnumeratedConstants() throws BinaryPlistException {
        BPDict dict = new BPDict()
                .with("key1", Numerals.FIRST)
                .with("key2", Numerals.SECOND)
                .with("key3", Numerals.THIRD);
        byte[] bytes = BinaryPlist.encode(dict);

        BPItem root = BinaryPlist.decode(bytes);
        Assert.assertEquals("Not a dictionary", BPItem.Type.Dict, root.getType());
        BPDict newDict = (BPDict) root;

        Assert.assertEquals(dict.get("key1", Numerals.DIFFERENT_FAIL), newDict.get("key1", Numerals.FAIL));
        Assert.assertEquals(dict.get("key2", Numerals.DIFFERENT_FAIL), newDict.get("key2", Numerals.FAIL));
        Assert.assertEquals(dict.get("key3", Numerals.DIFFERENT_FAIL), newDict.get("key3", Numerals.FAIL));
        Assert.assertEquals(Numerals.FAIL, newDict.get("key4", Numerals.FAIL));

    }

    @Test
    public void TestEnumeratedKeys() throws BinaryPlistException {
        BPDict dict = new BPDict()
                .with(Keys.ALPHA, "value1")
                .with(Keys.BETA, 14)
                .with(Keys.GAMMA, true)
                .with(Keys.DELTA, Numerals.FIRST)
                .with("key5", "finished");
        byte[] bytes = BinaryPlist.encode(dict);

        BPItem root = BinaryPlist.decode(bytes);
        Assert.assertEquals("Not a dictionary", BPItem.Type.Dict, root.getType());
        BPDict newDict = (BPDict) root;

        Assert.assertTrue(newDict.containsKey(Keys.ALPHA));
        Assert.assertTrue(newDict.containsKey(Keys.BETA));
        Assert.assertTrue(newDict.containsKey(Keys.GAMMA));
        Assert.assertTrue(newDict.containsKey(Keys.DELTA));

        Assert.assertEquals(dict.get(Keys.ALPHA, ""), newDict.get(Keys.ALPHA, "FAIL"));
        Assert.assertEquals(dict.get(Keys.BETA, 0), newDict.get(Keys.BETA, -1));
        Assert.assertEquals(dict.get(Keys.GAMMA, true), newDict.get(Keys.GAMMA, false));
        Assert.assertEquals(dict.get(Keys.DELTA, Numerals.DIFFERENT_FAIL), newDict.get(Keys.DELTA, Numerals.FAIL));
        Assert.assertEquals(dict.get("key5", ""), newDict.get("key5", "FAIL"));
    }

    @Test
    public void TestStringEquivalentKeys() throws BinaryPlistException {
        BPDict dict = new BPDict()
                .with(Keys.ALPHA, "value1")
                .with("BETA", 14)
                .with(Keys.GAMMA, true)
                .with("DELTA", Numerals.FIRST)
                .with("key5", "finished");
        byte[] bytes = BinaryPlist.encode(dict);

        BPItem root = BinaryPlist.decode(bytes);
        Assert.assertEquals("Not a dictionary", BPItem.Type.Dict, root.getType());
        BPDict newDict = (BPDict) root;

        Assert.assertTrue(newDict.containsKey(Keys.ALPHA));
        Assert.assertTrue(newDict.containsKey(Keys.BETA));
        Assert.assertTrue(newDict.containsKey(Keys.GAMMA));
        Assert.assertTrue(newDict.containsKey(Keys.DELTA));

        Assert.assertEquals(dict.get(Keys.ALPHA, ""), newDict.get("ALPHA", "FAIL"));
        Assert.assertEquals(dict.get(Keys.BETA, 0), newDict.get(Keys.BETA, -1));
        Assert.assertEquals(dict.get(Keys.GAMMA, true), newDict.get(BPString.get("GAMMA"), false));
        Assert.assertEquals(dict.get(Keys.DELTA, Numerals.DIFFERENT_FAIL), newDict.get(Keys.DELTA, Numerals.FAIL));
        Assert.assertEquals(dict.get("key5", ""), newDict.get("key5", "FAIL"));
    }

    @Test
    public void TestSelectedIntegers() throws IOException, BinaryPlistException {
        InputStream stream = TestBPItems.class.getResourceAsStream("/IntArray.plist");
        byte[] bytes = IOUtils.toByteArray(stream);
        BPItem root = BinaryPlist.decode(bytes);
        Assert.assertEquals("Not an array", BPItem.Type.Array, root.getType());
        byte[] newBytes = BinaryPlist.encode(root);
        BPItem newRoot = BinaryPlist.decode(newBytes);
        BPArray originalArray = (BPArray)root;
        BPArray newArray = (BPArray)newRoot;
        Assert.assertEquals(originalArray.size(), newArray.size());
        for (int i=0; i< originalArray.size(); i++) {
            BPInt originalInt = (BPInt) originalArray.get(i);
            BPInt newInt = (BPInt) newArray.get(i);
            Assert.assertEquals(originalInt.getLongValue(), newInt.getLongValue());
            Assert.assertEquals(originalInt.getValue(), newInt.getValue());
        }
    }

    @Test
    public void TestAllLongs() throws IOException, BinaryPlistException {
        InputStream stream = TestBPItems.class.getResourceAsStream("/Longs.plist");
        byte[] bytes = IOUtils.toByteArray(stream);
        BPItem root = BinaryPlist.decode(bytes);
        Assert.assertEquals("Not a dictionary", BPItem.Type.Dict, root.getType());
        log.fine(BinaryPlist.dump(root));
        Assert.assertEquals(1, (((BPDict) root).get("Int0", 0)));
        for (int i=0; i<64; i++) {
            BPInt theInt = (BPInt)(((BPDict) root).get("Int" + i));
            if (i < 31) {
                Assert.assertEquals("Testing 1 << " + i, ((long)1 << i), theInt.getValue());
            } else {
                Assert.assertEquals("Testing 1 << " + i, ((long)1 << i), theInt.getLongValue());
            }
        }

        Assert.assertEquals(1 << 1, (((BPDict) root).get("Int1", 0)));
        Assert.assertEquals(1 << 15, (((BPDict) root).get("Int15", 0)));
        Assert.assertEquals(1 << 16, (((BPDict) root).get("Int16", 0)));
        Assert.assertEquals(1 << 30, (((BPDict) root).get("Int30", 0)));
        Assert.assertEquals(1 << 31, (((BPDict) root).get("Int31", 0)));
        Assert.assertEquals(-(1 << 1), (((BPDict) root).get("Int-1", 0)));
        Assert.assertEquals(-(1 << 15), (((BPDict) root).get("Int-15", 0)));
        Assert.assertEquals(-(1 << 16), (((BPDict) root).get("Int-16", 0)));
        Assert.assertEquals(-(1 << 30), (((BPDict) root).get("Int-30", 0)));
        Assert.assertEquals(-(1 << 31), (((BPDict) root).get("Int-31", 0)));
    }

    @Test
    public void TestNumbers() throws IOException, BinaryPlistException {
        InputStream stream = TestBPItems.class.getResourceAsStream("/Numbers.plist");
        byte[] bytes = IOUtils.toByteArray(stream);
        BPItem root = BinaryPlist.decode(bytes);
        Assert.assertEquals("Not a dictionary", BPItem.Type.Dict, root.getType());
        log.fine(BinaryPlist.dump(root));
    }

    @Test
    public void TestBPArrayWithPointerSize1() throws IOException, BinaryPlistException {
        BPArray array = new BPArray();
        for (int i=0; i< 130; i++) {
            array.with(i);
        }
        byte[] bytes = BinaryPlist.encode(array);
        BPItem root = BinaryPlist.decode(bytes);
        Assert.assertEquals("Not an array", BPItem.Type.Array, root.getType());
        int count=0;
        for (Iterator it = array.iterator(); it.hasNext();) {
            Assert.assertEquals(count, ((BPInt) it.next()).getValue());
            count++;
        }
        Assert.assertEquals(130, count);

    }

    @Test
    public void TestBPArrayWithPointerSize2() throws IOException, BinaryPlistException {
        BPArray array = new BPArray();
        for (int i=0; i< 32770; i++) {
            array.with(i);
        }
        byte[] bytes = BinaryPlist.encode(array);
        BPItem root = BinaryPlist.decode(bytes);
        Assert.assertEquals("Not an array", BPItem.Type.Array, root.getType());
        int count=0;
        for (Iterator it = array.iterator(); it.hasNext();) {
            Assert.assertEquals(count, ((BPInt) it.next()).getValue());
            count++;
        }
        Assert.assertEquals(32770, count);

    }
}
