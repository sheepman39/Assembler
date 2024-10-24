package edu.iu.jrsalata;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HexNumTest {

    @Test
    public void testHexNumConstructor() {

        // Test default input
        HexNum hexNum = new HexNum();
        assertTrue(hexNum.getDec() == 0);

        // Test decimal input
        HexNum hexNum2 = new HexNum(255);
        assertTrue(hexNum2.getDec() == 255);
        assertTrue(hexNum2.toString().equals("ff"));

        // Test hex input
        HexNum hexNum3 = new HexNum("a", NumSystem.HEX);
        assertTrue(hexNum3.getDec() == 10);
        assertTrue(hexNum3.toString().equals("a"));

        // Test binary input
        HexNum hexNum4 = new HexNum("1110", NumSystem.BIN);
        assertTrue(hexNum4.getDec() == 14);
    }

    @Test
    public void testHexNumAddInt() {

        // test adding an int to a HexNum
        HexNum hexNum = new HexNum(10);
        hexNum = hexNum.add(5);
        assertTrue(hexNum.getDec() == 15);
        assertTrue(hexNum.toString().equals("f"));

        // test adding a negative int
        HexNum hexNum2 = new HexNum(10);
        hexNum2 = hexNum2.add(-5);
        assertTrue(hexNum2.getDec() == 5);
        assertTrue(hexNum2.toString().equals("5"));
    }

    @Test
    public void testHexNumAddHexNum() {

        // test adding two HexNum objects
        HexNum hexNum1 = new HexNum(10);
        HexNum hexNum2 = new HexNum(6);
        HexNum result = hexNum1.add(hexNum2);
        assertTrue(result.getDec() == 16);
        assertTrue(result.toString().equals("10"));
    }

    @Test
    public void testHexNumSet() {

        // test setting a HexNum with an int
        HexNum hexNum = new HexNum();
        hexNum.set(10);
        assertTrue(hexNum.getDec() == 10);
        assertTrue(hexNum.toString().equals("a"));

        // test setting a HexNum with a binary string
        hexNum.set("1011", NumSystem.BIN);
        assertTrue(hexNum.getDec() == 11);
        assertTrue(hexNum.toString().equals("b"));

        // test setting a HexNum with a hex string
        hexNum.set("ff", NumSystem.HEX);
        assertTrue(hexNum.getDec() == 255);
        assertTrue(hexNum.toString().equals("ff"));
    }
}
