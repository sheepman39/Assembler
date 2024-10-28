package edu.iu.jrsalata;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ExtendedStatementTest {
    
    @Test
    public void testDefaultConstructor(){
        ExtendedStatement extendedStatement = new ExtendedStatement();
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        System.out.println(extendedStatement.assemble());
        assertTrue(extendedStatement.assemble().equals("030000"));
    }

    @Test 
    public void testAddInstruction(){
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "222");
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("1b0222"));
    }
    
    @Test
    public void testImmediateAddressing(){
        // use ADD instruction with indirect addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "#222");
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("190222"));
    }
    @Test
    public void testIndirectAddressing(){
        // use ADD instruction with indirect addressing
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        ExtendedStatement extendedStatement = new ExtendedStatement(location, opcode, "@222");
        assertTrue(extendedStatement.getLocation().getDec() == 0);
        assertTrue(extendedStatement.getSize().getDec() == 3);
        assertTrue(extendedStatement.assemble().equals("1a0222"));
    }
}
