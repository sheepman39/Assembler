package edu.iu.jrsalata;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SicStatementTest {

    @Test
    public void testDefaultConstructor() {
        SicStatement sicStatement = new SicStatement();
        assertEquals(0, sicStatement.getLocation().getDec());
        assertEquals(3, sicStatement.getSize().getDec());
        assertEquals("000000", sicStatement.assemble());
    }

    @Test
    public void testAddInstruction() {
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        SicStatement sicStatement = new SicStatement(location, opcode, "000");
        assertEquals(0, sicStatement.getLocation().getDec());
        assertEquals(3, sicStatement.getSize().getDec());
        assertEquals("180000", sicStatement.assemble());
    }

    @Test
    public void testAddWithXInstruction() {
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        SicStatement sicStatement = new SicStatement(location, opcode, "020, X");
        assertEquals(0, sicStatement.getLocation().getDec());
        assertEquals(3, sicStatement.getSize().getDec());
        assertEquals("188020", sicStatement.assemble());
    }
}