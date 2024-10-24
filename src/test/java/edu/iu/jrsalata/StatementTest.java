package edu.iu.jrsalata;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StatementTest {
    
    @Test
    public void testDefaultConstructor() {
        Statement statement = new Statement();
        assertTrue(statement.getLocation().getDec() == 0);
        assertTrue(statement.getSize().getDec() == 1);
        assertTrue(statement.assemble().equals("0"));
    }

    @Test
    public void testFIXInstruction(){
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("C4", NumSystem.HEX);
        Statement statement = new Statement(location, opcode);
        assertTrue(statement.getLocation().getDec() == 0);
        assertTrue(statement.getSize().getDec() == 1);
        assertTrue(statement.assemble().equals("C4"));
    }

    @Test
    public void testTIOInstruction(){
        HexNum location = new HexNum(100);
        HexNum opcode = new HexNum("D8", NumSystem.HEX);
        Statement statement = new Statement(location, opcode);
        assertTrue(statement.getLocation().getDec() == 100);
        assertTrue(statement.getLocation().toString().equals("64"));
        assertTrue(statement.getSize().getDec() == 1);
        assertTrue(statement.assemble().equals("D8"));
    }
}
