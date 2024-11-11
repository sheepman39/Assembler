package edu.iu.jrsalata;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DirectiveStatemnetTest {

    @Test
    public void testDefaultConstructor() {
        DirectiveStatement directiveStatement = new DirectiveStatement();
        assertEquals(directiveStatement.getDirective(), "");
        assertEquals(directiveStatement.assemble(), "");
        assertEquals(directiveStatement.getSize().getDec(), 0);
    }

    @Test
    public void testConstructor() {
        DirectiveStatement directiveStatement = new DirectiveStatement("START");
        assertEquals(directiveStatement.getDirective(), "START");
        assertEquals(directiveStatement.getSize().getDec(), 0);
        directiveStatement.setDirective("END");
        assertEquals(directiveStatement.getDirective(), "END");
    }

    @Test
    public void testSizeGettersAndSetters() {
        HexNum size = new HexNum(3);
        DirectiveStatement directiveStatement = new DirectiveStatement();
        directiveStatement.setSize(size);
        assertEquals(directiveStatement.getSize().getDec(), 3);
        directiveStatement.setObjCode("F1");
        assertEquals(directiveStatement.assemble(), "F1");
    }
}
