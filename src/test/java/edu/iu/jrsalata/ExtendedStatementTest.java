package edu.iu.jrsalata;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExtendedStatementTest {

    @Test
    public void testDefaultConstructor() {
        ExtendedStatement extendedStatement = new ExtendedStatement();
        assertEquals(extendedStatement.getLocation().getDec(), 0);
        assertEquals(extendedStatement.getSize().getDec(), 3);
        assertEquals(extendedStatement.assemble(), "030000");
    }


    @Test
    public void testImmediateAddressing() {
        // use ADD instruction with indirect addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "#222");
        assertEquals(extendedStatement.getLocation().getDec(), 0);
        assertEquals(extendedStatement.getSize().getDec(), 3);
        assertEquals(extendedStatement.assemble(), "650222");
    }

    @Test
    public void testIndirectAddressing() {
        // use ADD instruction with indirect addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "@222");
        assertEquals(extendedStatement.getLocation().getDec(), 0);
        assertEquals(extendedStatement.getSize().getDec(), 3);
        assertEquals(extendedStatement.assemble(), "660222");
    }

    @Test
    public void testBaseRelative() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222");
        extendedStatement.setBFlag();
        assertEquals(extendedStatement.getLocation().getDec(), 0);
        assertEquals(extendedStatement.getSize().getDec(), 3);
        assertEquals(extendedStatement.assemble(), "674222");
    }

    @Test
    public void testPCRelative() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222");
        extendedStatement.setPFlag();
        assertEquals(extendedStatement.getLocation().getDec(), 0);
        assertEquals(extendedStatement.getSize().getDec(), 3);
        assertEquals(extendedStatement.assemble(), "672222");
    }

    @Test
    public void testXAddressing() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222, X");
        assertEquals(extendedStatement.getLocation().getDec(), 0);
        assertEquals(extendedStatement.getSize().getDec(), 3);
        assertEquals(extendedStatement.assemble(), "1b8222");
    }

    @Test
    public void testXAndBaseAddressing() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222, X");
        extendedStatement.setBFlag();
        assertEquals(extendedStatement.getLocation().getDec(), 0);
        assertEquals(extendedStatement.getSize().getDec(), 3);
        assertEquals(extendedStatement.assemble(), "67c222");
    }

    @Test
    public void testExtendedAddressing() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "2222");
        extendedStatement.setEFlag();
        assertEquals(extendedStatement.getLocation().getDec(), 0);
        assertEquals(extendedStatement.getSize().getDec(), 4);
        assertEquals(extendedStatement.assemble(), "6712222");
    }

    @Test
    public void testExtendedAndXbaseAddressing() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "2222, X");
        extendedStatement.setEFlag();
        assertEquals(extendedStatement.getLocation().getDec(), 0);
        assertEquals(extendedStatement.getSize().getDec(), 4);
        assertEquals(extendedStatement.assemble(), "6792222");
    }
}
