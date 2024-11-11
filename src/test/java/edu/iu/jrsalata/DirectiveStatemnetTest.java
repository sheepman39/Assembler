package edu.iu.jrsalata;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DirectiveStatemnetTest {

    @Test
    public void testDefaultConstructor() {
        DirectiveStatement directiveStatement = new DirectiveStatement();
        assertEquals("", directiveStatement.getDirective());
        assertEquals("", directiveStatement.assemble());
        assertEquals(0, directiveStatement.getSize().getDec());
    }

    @Test
    public void testConstructor() {
        DirectiveStatement directiveStatement = new DirectiveStatement("START");
        assertEquals("START", directiveStatement.getDirective());
        assertEquals(0, directiveStatement.getSize().getDec());
        directiveStatement.setDirective("END");
        assertEquals("END", directiveStatement.getDirective());
    }

    @Test
    public void testSizeGettersAndSetters() {
        HexNum size = new HexNum(3);
        DirectiveStatement directiveStatement = new DirectiveStatement();
        directiveStatement.setSize(size);
        assertEquals(directiveStatement.getSize().getDec(), 3);
        directiveStatement.setObjCode("F1");
        assertEquals("F1", directiveStatement.assemble());
    }
}
