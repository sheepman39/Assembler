package edu.iu.jrsalata;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExtendedStatementTest {

    @Test
    public void testDefaultConstructor() {
        ExtendedStatement extendedStatement = new ExtendedStatement();
        assertEquals(0, extendedStatement.getLocation().getDec());
        assertEquals(3, extendedStatement.getSize().getDec());
        assertEquals("030000", extendedStatement.assemble());
    }

    @Test
    public void testImmediateAddressing() {
        // use ADD instruction with indirect addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "#222");
        assertEquals(0, extendedStatement.getLocation().getDec());
        assertEquals(3, extendedStatement.getSize().getDec());
        assertEquals("650222", extendedStatement.assemble());
    }

    @Test
    public void testIndirectAddressing() {
        // use ADD instruction with indirect addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "@222");
        assertEquals(0, extendedStatement.getLocation().getDec());
        assertEquals(3, extendedStatement.getSize().getDec());
        assertEquals("660222", extendedStatement.assemble());
    }

    @Test
    public void testBaseRelative() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222");
        extendedStatement.setBFlag();
        assertEquals(0, extendedStatement.getLocation().getDec());
        assertEquals(3, extendedStatement.getSize().getDec());
        assertEquals("674222", extendedStatement.assemble());
    }

    @Test
    public void testPCRelative() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222");
        extendedStatement.setPFlag();
        assertEquals(0, extendedStatement.getLocation().getDec());
        assertEquals(3, extendedStatement.getSize().getDec());
        assertEquals("672222", extendedStatement.assemble());
    }

    @Test
    public void testXAddressing() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222, X");
        assertEquals(0, extendedStatement.getLocation().getDec());
        assertEquals(3, extendedStatement.getSize().getDec());
        assertEquals("1b8222", extendedStatement.assemble());
    }

    @Test
    public void testXAndBaseAddressing() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222, X");
        extendedStatement.setBFlag();
        assertEquals(0, extendedStatement.getLocation().getDec());
        assertEquals(3, extendedStatement.getSize().getDec());
        assertEquals("67c222", extendedStatement.assemble());
    }

    @Test
    public void testExtendedAddressing() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "2222");
        extendedStatement.setEFlag();
        assertEquals(0, extendedStatement.getLocation().getDec());
        assertEquals(4, extendedStatement.getSize().getDec());
        assertEquals("67102222", extendedStatement.assemble());
    }

    @Test
    public void testExtendedAndXbaseAddressing() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "2222, X");
        extendedStatement.setEFlag();
        assertEquals(0, extendedStatement.getLocation().getDec());
        assertEquals(4, extendedStatement.getSize().getDec());
        assertEquals("67902222", extendedStatement.assemble());
    }
}
