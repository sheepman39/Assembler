package edu.iu.jrsalata;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class RegisterStatementTest {

    @Test
    public void testDefaultConstructor() {
        RegisterStatement registerStatement = new RegisterStatement();
        assertEquals(0, registerStatement.getLocation().getDec());
        assertEquals(2, registerStatement.getSize().getDec());
        assertEquals("0000", registerStatement.assemble());
    }

    @Test
    public void testOneRegConstructor() {
        // Test using the COMPR instruction and register B
        // Opcode for COMPR is A0
        // Register B is 3
        HexNum location = new HexNum(10);
        HexNum opcode = new HexNum("A0", NumSystem.HEX);
        HexNum reg1 = new HexNum(3);
        RegisterStatement registerStatement = new RegisterStatement(location, opcode, reg1);
        assertEquals(10, registerStatement.getLocation().getDec());
        assertEquals(2, registerStatement.getSize().getDec());
        assertEquals("A030", registerStatement.assemble());
    }

    @Test
    public void testTwoRegConstructor() {
        // Test using the COMPR instruction and registers B and C
        // Opcode for COMPR is A0
        // Register B is 3 and Register A is 0
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("A0", NumSystem.HEX);
        HexNum reg1 = new HexNum(2);
        HexNum reg2 = new HexNum(3);
        RegisterStatement registerStatement = new RegisterStatement(location, opcode, reg1, reg2);
        assertEquals(0, registerStatement.getLocation().getDec());
        assertEquals(2, registerStatement.getSize().getDec());
        assertEquals("A023", registerStatement.assemble());
    }

}
