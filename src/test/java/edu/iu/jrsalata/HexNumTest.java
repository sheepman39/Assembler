package edu.iu.jrsalata;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HexNumTest {

    @Test
    public void testHexNumConstructor() {

        // Test default input
        HexNum hexNum = new HexNum();
        assertEquals(0, hexNum.getDec());
        assertEquals("0", hexNum.toString());

        // Test decimal input
        HexNum hexNum2 = new HexNum(255);
        assertEquals(255, hexNum2.getDec());
        assertEquals("ff", hexNum2.toString());

        // Test hex input
        HexNum hexNum3 = new HexNum("a", NumSystem.HEX);
        assertEquals(10, hexNum3.getDec());
        assertEquals("a", hexNum3.toString());

        // Test binary input
        HexNum hexNum4 = new HexNum("1110", NumSystem.BIN);
        assertEquals(14, hexNum4.getDec());
    }

    @Test
    public void testHexNumAddInt() {

        // test adding an int to a HexNum
        HexNum hexNum = new HexNum(10);
        hexNum = hexNum.add(5);
        assertEquals(15, hexNum.getDec());
        assertEquals("f", hexNum.toString());

        // test adding a negative int
        HexNum hexNum2 = new HexNum(10);
        hexNum2 = hexNum2.add(-5);
        assertEquals(5, hexNum2.getDec());
        assertEquals("5", hexNum2.toString());
    }

    @Test
    public void testHexNumAddHexNum() {

        // test adding two HexNum objects
        HexNum hexNum1 = new HexNum(10);
        HexNum hexNum2 = new HexNum(6);
        HexNum result = hexNum1.add(hexNum2);
        assertEquals(16, result.getDec());
        assertEquals("10", result.toString());
    }

    @Test
    public void testHexNumSubtractInt() {

        // test adding an int to a HexNum
        HexNum hexNum = new HexNum(10);
        hexNum = hexNum.subtract(5);
        assertEquals(5, hexNum.getDec());
        assertEquals("5", hexNum.toString());

        // test adding a negative int
        HexNum hexNum2 = new HexNum(10);
        hexNum2 = hexNum2.subtract(-5);
        assertEquals(15, hexNum2.getDec());
        assertEquals("f", hexNum2.toString());
    }

    @Test
    public void testHexNumSubtractHexNum() {

        // test adding two HexNum objects
        HexNum hexNum1 = new HexNum(10);
        HexNum hexNum2 = new HexNum(6);
        HexNum result = hexNum1.subtract(hexNum2);
        assertEquals(4, result.getDec());
        assertEquals("4", result.toString());

    }

    @Test
    public void testHexNumSet() {

        // test setting a HexNum with an int
        HexNum hexNum = new HexNum();
        hexNum.set(10);
        assertEquals(10, hexNum.getDec());
        assertEquals("a", hexNum.toString());

        // test setting a HexNum with a binary string
        hexNum.set("1011", NumSystem.BIN);
        assertEquals(11, hexNum.getDec());
        assertEquals("b", hexNum.toString());

        // test setting a HexNum with a hex string
        hexNum.set("ff", NumSystem.HEX);
        assertEquals(255, hexNum.getDec());
        assertEquals("ff", hexNum.toString());
    }

    @Test
    public void testHexNumToStringWithDigits() {

        // test toString with digits
        HexNum hexNum = new HexNum(10);
        assertEquals("000a", hexNum.toString(4));
        assertEquals("00a", hexNum.toString(3));
        assertEquals("0a", hexNum.toString(2));
        assertEquals("a", hexNum.toString(1));

        HexNum hexNum2 = new HexNum("ff", NumSystem.HEX);
        assertEquals("00ff", hexNum2.toString(4));
        assertEquals("0ff", hexNum2.toString(3));
        assertEquals("ff", hexNum2.toString(2));
        assertEquals("ff", hexNum2.toString(1));
    }
}
