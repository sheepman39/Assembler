package edu.iu.jrsalata;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SingleStatementTest {

    @Test
    public void testDefaultConstructor() {
        SingleStatement statement = new SingleStatement();
        assertEquals(0, statement.getLocation().getDec());
        assertEquals(1, statement.getSize().getDec());
        assertEquals("00", statement.assemble());
    }

    @Test
    public void testFIXInstruction() {
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("C4", NumSystem.HEX);
        SingleStatement statement = new SingleStatement(location, opcode);
        assertEquals(0, statement.getLocation().getDec());
        assertEquals(1, statement.getSize().getDec());
        assertEquals("C4", statement.assemble());
    }

    @Test
    public void testTIOInstruction() {
        HexNum location = new HexNum(100);
        HexNum opcode = new HexNum("D8", NumSystem.HEX);
        SingleStatement statement = new SingleStatement(location, opcode);
        assertEquals(100, statement.getLocation().getDec());
        assertEquals("64", statement.getLocation().toString());
        assertEquals(1, statement.getSize().getDec());
        assertEquals("D8", statement.assemble());
    }
}
