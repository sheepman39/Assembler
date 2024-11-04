package edu.iu.jrsalata;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SingleStatementTest {

    @Test
    public void testDefaultConstructor() {
        SingleStatement statement = new SingleStatement();
        assertEquals(statement.getLocation().getDec(), 0);
        assertEquals(statement.getSize().getDec(), 1);
        assertEquals(statement.assemble(), "00");
    }

    @Test
    public void testFIXInstruction() {
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("C4", NumSystem.HEX);
        SingleStatement statement = new SingleStatement(location, opcode);
        assertEquals(statement.getLocation().getDec(), 0);
        assertEquals(statement.getSize().getDec(), 1);
        assertEquals(statement.assemble(), "C4");
    }

    @Test
    public void testTIOInstruction() {
        HexNum location = new HexNum(100);
        HexNum opcode = new HexNum("D8", NumSystem.HEX);
        SingleStatement statement = new SingleStatement(location, opcode);
        assertEquals(statement.getLocation().getDec(), 100);
        assertEquals(statement.getLocation().toString(), "64");
        assertEquals(statement.getSize().getDec(), 1);
        assertEquals(statement.assemble(), "D8");
    }
}
