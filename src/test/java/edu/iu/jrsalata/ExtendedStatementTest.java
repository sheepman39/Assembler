package edu.iu.jrsalata;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ExtendedStatementTest {

    @Test
    public void testDefaultConstructor() {
        ExtendedStatement extendedStatement = new ExtendedStatement();
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        System.out.println(extendedStatement.assemble());
        assertTrue(extendedStatement.assemble().equals("030000"));
    }

    @Test
    public void testAddInstruction() {
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222");
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("1b0222"));
    }

    @Test
    public void testImmediateAddressing() {
        // use ADD instruction with indirect addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "#222");
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("190222"));
    }

    @Test
    public void testIndirectAddressing() {
        // use ADD instruction with indirect addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "@222");
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("1a0222"));
    }

    @Test
    public void testBaseRelative() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222");
        extendedStatement.setBFlag();
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("1b4222"));
    }

    @Test
    public void testPCRelative() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222");
        extendedStatement.setPFlag();
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("1b2222"));
    }

    @Test
    public void testXAddressing() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222, X");
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("1b8222"));
    }

    @Test
    public void testXAndBaseAddressing() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222, X");
        extendedStatement.setBFlag();
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("1bc222"));
    }

    @Test
    public void testExtendedAddressing() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "2222");
        extendedStatement.setEFlag();
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 4);
        assertTrue(extendedStatement.assemble().equals("1b12222"));
    }

    @Test
    public void testExtendedAndXbaseAddressing() {
        // use ADD instruction with base relative addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "2222, X");
        extendedStatement.setEFlag();
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 4);
        assertTrue(extendedStatement.assemble().equals("1b92222"));
    }
}
