package edu.iu.jrsalata;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RegisterStatementTest {

    @Test
    public void testDefaultConstructor() {
        RegisterStatement registerStatement = new RegisterStatement();
        assertTrue(registerStatement.getLocation().getDec() == 0);
        assertTrue(registerStatement.getSize().getDec() == 2);
        assertTrue(registerStatement.assemble().equals("000"));
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
        assertTrue(registerStatement.getLocation().getDec() == 10);
        assertTrue(registerStatement.getSize().getDec() == 2);
        assertTrue(registerStatement.assemble().equals("A030"));
    }

    @Test
    public void testTwoRegConstructor() {
        // Test using the COMPR instruction and registers B and C
        // Opcode for COMPR is A0
        // Register B is 3 and Register A is 0
        HexNum location = new HexNum(0);
        HexNum opcode = new HexNum("A0", NumSystem.HEX);
        HexNum reg1 = new HexNum(0);
        HexNum reg2 = new HexNum(3);
        RegisterStatement registerStatement = new RegisterStatement(location, opcode, reg1, reg2);
        assertTrue(registerStatement.getLocation().getDec() == 0);
        assertTrue(registerStatement.getSize().getDec() == 2);
        assertTrue(registerStatement.assemble().equals("A003"));
    }

}
