package edu.iu.jrsalata;

import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class MacroProcessorTest {

    @Test
    public void testMacroProcessorConstructor() {
        String[] params = {"ARG1", "ARG2", "ARG3"};
        String[] args = {"FRIEND", "CATS", "SWIM"};
        MacroProcessor processor = new MacroProcessor(params);
        processor.addLine("HELLO ARG1");
        processor.addLine("I LIKE ARG2");
        processor.addLine("CAN YOU ARG3");

        try {
            Queue<String> lines = processor.getLines(args);
            
            assertEquals("HELLO FRIEND", lines.poll());
            assertEquals("I LIKE CATS", lines.poll());
            assertEquals("CAN YOU SWIM", lines.poll());

        } catch (InvalidAssemblyFileException e){
            fail(e.getMessage());
        }
    }

    @Test
    public void TestInvalidArgs(){
        String[] params = {"ARG1", "ARG2", "ARG3"};
        String[] args = {"FRIEND", "CATS"};
        MacroProcessor processor = new MacroProcessor(params);
        processor.addLine("HELLO ARG1");
        processor.addLine("I LIKE ARG2");
        processor.addLine("CAN YOU ARG3");

        try {

            processor.getLines(args);
            fail("addLine should have failed");

        } catch (InvalidAssemblyFileException e){
            assertTrue(!e.getMessage().isEmpty());
        }
    }

    @Test
    public void testMacroProcessorLabel() {
        String[] params = {"ARG1", "ARG2", "ARG3"};
        String[] args = {"FRIEND", "CATS", "SWIM"};
        MacroProcessor processor = new MacroProcessor(params);
        processor.setLabel("START");
        processor.addLine("HELLO ARG1");
        processor.addLine("I LIKE ARG2");
        processor.addLine("CAN YOU ARG3");

        try {
            Queue<String> lines = processor.getLines(args);
            
            assertEquals("START   HELLO FRIEND", lines.poll());
            assertEquals("I LIKE CATS", lines.poll());
            assertEquals("CAN YOU SWIM", lines.poll());

        } catch (InvalidAssemblyFileException e){
            fail(e.getMessage());
        }
    }
}
