package edu.iu.jrsalata;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ExtendedStatementTest {

    @Test
    public void testDefaultConstructor() {
        ExtendedStatement extendedStatement = new ExtendedStatement();
        extendedStatement.setSICFlag();
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("000000"));
    }

    @Test
    public void testAddInstruction() {
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222");
        extendedStatement.setSICFlag();
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("180222"));
    }

    @Test
    public void testImmediateAddressing() {
        // use ADD instruction with indirect addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "#222");
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("650222"));
    }

    @Test
    public void testIndirectAddressing() {
        // use ADD instruction with indirect addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "@222");
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("660222"));
    }

    @Test
    public void testBaseRelative() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222");
        extendedStatement.setBFlag();
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("674222"));
    }

    @Test
    public void testPCRelative() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222");
        extendedStatement.setPFlag();
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("672222"));
    }

    @Test
    public void testXAddressing() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222, X");
        extendedStatement.setSICFlag();
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("188222"));
    }

    @Test
    public void testXAndBaseAddressing() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222, X");
        extendedStatement.setBFlag();
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("67c222"));
    }

    @Test
    public void testExtendedAddressing() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "2222");
        extendedStatement.setEFlag();
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 4);
        assertTrue(extendedStatement.assemble().equals("6712222"));
    }

    @Test
    public void testExtendedAndXbaseAddressing() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("64", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "2222, X");
        extendedStatement.setEFlag();
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 4);
        assertTrue(extendedStatement.assemble().equals("6792222"));
    }
}
