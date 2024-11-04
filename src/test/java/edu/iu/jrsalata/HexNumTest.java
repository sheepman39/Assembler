package edu.iu.jrsalata;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HexNumTest {

    @Test
    public void testHexNumConstructor() {

        // Test default input
        HexNum hexNum = new HexNum();
        assertEquals(hexNum.getDec(), 0);
        assertEquals(hexNum.toString(), "0");

        // Test decimal input
        HexNum hexNum2 = new HexNum(255);
        assertEquals(hexNum2.getDec(), 255);
        assertEquals(hexNum2.toString(), "ff");

        // Test hex input
        HexNum hexNum3 = new HexNum("a", NumSystem.HEX);
        assertEquals(hexNum3.getDec(), 10);
        assertEquals(hexNum3.toString(), "a");

        // Test binary input
        HexNum hexNum4 = new HexNum("1110", NumSystem.BIN);
        assertEquals(hexNum4.getDec(), 14);
    }

    @Test
    public void testHexNumAddInt() {

        // test adding an int to a HexNum
        HexNum hexNum = new HexNum(10);
        hexNum = hexNum.add(5);
        assertEquals(hexNum.getDec(), 15);
        assertEquals(hexNum.toString(), "f");

        // test adding a negative int
        HexNum hexNum2 = new HexNum(10);
        hexNum2 = hexNum2.add(-5);
        assertEquals(hexNum2.getDec(), 5);
        assertEquals(hexNum2.toString(), "5");
    }

    @Test
    public void testHexNumAddHexNum() {

        // test adding two HexNum objects
        HexNum hexNum1 = new HexNum(10);
        HexNum hexNum2 = new HexNum(6);
        HexNum result = hexNum1.add(hexNum2);
        assertEquals(result.getDec(), 16);
        assertEquals(result.toString(), "10");
    }

    @Test
    public void testHexNumSet() {

        // test setting a HexNum with an int
        HexNum hexNum = new HexNum();
        hexNum.set(10);
        assertEquals(hexNum.getDec(), 10);
        assertEquals(hexNum.toString(), "a");

        // test setting a HexNum with a binary string
        hexNum.set("1011", NumSystem.BIN);
        assertEquals(hexNum.getDec(), 11);
        assertEquals(hexNum.toString(), "b");

        // test setting a HexNum with a hex string
        hexNum.set("ff", NumSystem.HEX);
        assertEquals(hexNum.getDec(), 255);
        assertEquals(hexNum.toString(), "ff");
    }

    @Test
    public void testHexNumToStringWithDigits() {

        // test toString with digits
        HexNum hexNum = new HexNum(10);
        assertEquals(hexNum.toString(4), "000a");
        assertEquals(hexNum.toString(3), "00a");
        assertEquals(hexNum.toString(2), "0a");
        assertEquals(hexNum.toString(1), "a");

        HexNum hexNum2 = new HexNum("ff", NumSystem.HEX);
        assertEquals(hexNum2.toString(4), "00ff");
        assertEquals(hexNum2.toString(3), "0ff");
        assertEquals(hexNum2.toString(2), "ff");
        assertEquals(hexNum2.toString(1), "ff");
    }
}
