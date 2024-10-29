package edu.iu.jrsalata;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SingleStatementTest {

    @Test
    public void testDefaultConstructor() {
        SingleStatement statement = new SingleStatement();
        assertTrue(statement.getLocation().getDec() == 0);
        assertTrue(statement.getSize().getDec() == 1);
        assertTrue(statement.assemble().equals("00"));
    }

    @Test
    public void testFIXInstruction() {
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("C4", NumSystem.HEX);
        SingleStatement statement = new SingleStatement(location, opcode);
        assertTrue(statement.getLocation().getDec() == 0);
        assertTrue(statement.getSize().getDec() == 1);
        assertTrue(statement.assemble().equals("C4"));
    }

    @Test
    public void testTIOInstruction() {
        HexNum location = new HexNum(100);
        HexNum opcode = new HexNum("D8", NumSystem.HEX);
        SingleStatement statement = new SingleStatement(location, opcode);
        assertTrue(statement.getLocation().getDec() == 100);
        assertTrue(statement.getLocation().toString().equals("64"));
        assertTrue(statement.getSize().getDec() == 1);
        assertTrue(statement.assemble().equals("D8"));
    }
}
