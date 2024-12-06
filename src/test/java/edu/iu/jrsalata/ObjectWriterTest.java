package edu.iu.jrsalata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import javax.script.ScriptException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ObjectWriterTest {
    static final String SIC_FLAG = "!USE SIC";
    String assemblyFile;
    String objectFile;

    // constructor to help with parameterization
    public ObjectWriterTest(String assemblyFile, String objectFile) {
        this.assemblyFile = assemblyFile;
        this.objectFile = objectFile;
    }

    @Test
    public void testAsm() {
        // Create an instance of the StatementFactory
        // clear out the symtable since it is used in previous tests
        try {
            SymTable.clear();
            InputStream file = getClass().getResourceAsStream(assemblyFile);
            AbstractStatementBuilderBuilderInterface builderBuilder = new AbstractStatementBuilderBuilder();
            builderBuilder.execute(file);
            Queue<AbstractStatementBuilder> queue = builderBuilder.getBuilders();
            
            String fileName = "test.obj";
    
            ObjectWriterInterface writer = new ObjectWriter();
            testAsm(writer, queue, fileName);
            
        } catch (InvalidAssemblyFileException | IOException | ScriptException e) {
            fail(e.getMessage());
        }

    }

    public void testAsm(ObjectWriterInterface writer, Queue<AbstractStatementBuilder> queue, String fileName) {

        // lets first write the object file
        AbstractStatementBuilder builder;
        while (!queue.isEmpty()) {

            builder = queue.poll();
            writer.setBuilder(builder);
            writer.setQueue(builder.getStatements());
            writer.setFileName(fileName);
            try {

                writer.execute();

            } catch (InvalidAssemblyFileException | IOException e) {
                fail(e.getMessage());
            }
        }

        // now compare the output between the two files
        try {
            // read the original compare file
            InputStream control = getClass().getResourceAsStream(objectFile);

            // read the generated file
            InputStream test = new FileInputStream(fileName);

            Scanner scTest;
            try ( // create a scanner for each file
                    Scanner scControl = new Scanner(control)) {
                scTest = new Scanner(test);
                // compare the two files

                while (scControl.hasNextLine() && scTest.hasNextLine()) {

                    String lineControl = scControl.nextLine();
                    String lineTest = scTest.nextLine();
                    assertEquals(lineControl.trim().toLowerCase(), lineTest.trim().toLowerCase());

                }
            }
            scTest.close();

            // delete the generated test file
            new File(fileName).delete();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Parameterized.Parameters()
    public static Collection<String[]> files() {
        return Arrays.asList(new String[][] {
                { "/testAsm1.asm", "/testAsm1.obj" },
                { "/testAsm2.asm", "/testAsm2.obj" },
                { "/testAsm3.asm", "/testAsm3.obj" },
                { "/testAsm4.asm", "/testAsm4.obj" },
                { "/testAsm5.asm", "/testAsm5.obj" },
        });
    }
}