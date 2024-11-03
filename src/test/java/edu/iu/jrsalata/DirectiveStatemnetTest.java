package edu.iu.jrsalata;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DirectiveStatemnetTest {
 
    @Test
    public void testDefaultConstructor() {
        DirectiveStatement directiveStatement = new DirectiveStatement();
        assertTrue(directiveStatement.getDirective().equals(""));
        assertTrue(directiveStatement.assemble() == "");
        assertTrue(directiveStatement.getSize().getDec() == 0);
    }

    @Test
    public void testConstructor() {
        DirectiveStatement directiveStatement = new DirectiveStatement("START");
        assertTrue(directiveStatement.getDirective().equals("START"));
        assertTrue(directiveStatement.getSize().getDec() == 0);
        directiveStatement.setDirective("END");
        assertTrue(directiveStatement.getDirective().equals("END"));
    }

    @Test 
    public void testSizeGettersAndSetters(){
        HexNum size = new HexNum(3);
        DirectiveStatement directiveStatement = new DirectiveStatement();
        directiveStatement.setSize(size);
        assertTrue(directiveStatement.getSize().getDec() == 3);
        directiveStatement.setObjCode("F1");
        assertTrue(directiveStatement.assemble().equals("F1"));
    }
}
