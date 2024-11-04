package edu.iu.jrsalata;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class SicStatementTest {
    
    @Test
    public void testDefaultConstructor() {
        SicStatement sicStatement = new SicStatement();
        assertEquals(sicStatement.getLocation().getDec(), 0);
        assertEquals(sicStatement.getSize().getDec(), 3);
        assertEquals(sicStatement.assemble(), "000000");
    }

    @Test 
    public void testAddInstruction(){
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        SicStatement sicStatement = new SicStatement(location, opcode, "000");
        assertEquals(sicStatement.getLocation().getDec(), 0);
        assertEquals(sicStatement.getSize().getDec(), 3);
        assertEquals(sicStatement.assemble(), "180000");
    }

    @Test 
    public void testAddWithXInstruction(){
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("18", NumSystem.HEX);
        SicStatement sicStatement = new SicStatement(location, opcode, "020, X");
        assertEquals(sicStatement.getLocation().getDec(), 0);
        assertEquals(sicStatement.getSize().getDec(), 3);
        assertEquals(sicStatement.assemble(), "188020");
    }
}
