package uk.co.sromo.blister;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: daniel
 * Date: 14-Aug-2010
 * Time: 19:48:40
 * To change this template use File | Settings | File Templates.
 */
public class BPItemTests {

    public final static String ASCII_STRING_1 = "Quite Interesting";
    public final static String ASCII_STRING_2 = "RatherDull";

    public final static String UNICODE_STRING_1 = "Ỡuitὲ InƬerestіng";
    public final static String UNICODE_STRING_2 = "ṜaŦherḒull";

    @Test
    public void TestBPStringTypeIdentification() {
        BPString s1 = BPString.get(ASCII_STRING_1);
        Assert.assertEquals("String wasn't ASCII", BPString.EncodingType.ASCII, s1.getEncodingType());
        BPString s2 = BPString.get(UNICODE_STRING_1);
        Assert.assertEquals("String wasn't Unicode", BPString.EncodingType.UTF16, s2.getEncodingType());
    }

    @Test
    public void TestPrimitiveTypesInDictionary() throws BinaryPlistException {
        BPDict dict = new BPDict()
                .with(ASCII_STRING_2, ASCII_STRING_1)
                .with(UNICODE_STRING_2, UNICODE_STRING_1);
        Assert.assertEquals("Ascii lookup failed", ASCII_STRING_1, dict.getString(ASCII_STRING_2, "FAIL"));
        Assert.assertEquals("Unicode lookup failed", UNICODE_STRING_1, dict.getString(UNICODE_STRING_2, "FAIL"));

        byte[] bytes = BinaryPlist.encode(dict);

        BPItem root = BinaryPlist.decode(bytes);
        Assert.assertEquals("Not a dictioary", BPItem.Type.Dict, root.getType());

        BPDict newDict = (BPDict) root;

        Assert.assertEquals("New ascii lookup failed", ASCII_STRING_1, newDict.getString(ASCII_STRING_2, "FAIL"));
        Assert.assertEquals("New unicode lookup failed", UNICODE_STRING_1, newDict.getString(UNICODE_STRING_2, "FAIL"));

    }
}
