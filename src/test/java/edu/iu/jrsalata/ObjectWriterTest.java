package edu.iu.jrsalata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

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
        SymTable.clear();
        AbstractStatementBuilder builder = choseBuilder();
        InputStream file = getClass().getResourceAsStream(assemblyFile);
        Stack<Queue<Statement>> stack = fileInput(file, builder);
        String fileName = "test.obj";

        ObjectWriterInterface writer = new ObjectWriter();
        writer.setBuilder(builder);
        writer.setFileName(fileName);
        testAsm(writer, stack, fileName);
    }

    public void testAsm(ObjectWriterInterface writer, Stack<Queue<Statement>> stack, String fileName) {
        while (!stack.isEmpty()) {
            writer.setQueue(stack.pop());
            // now compare the output between the two files
            try {
                writer.execute();
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
                        assertEquals(lineControl.toLowerCase(), lineTest.toLowerCase());

                    }
                }
                scTest.close();

                // delete the generated test file
                new File(fileName).delete();
            } catch (InvalidAssemblyFileException | IOException e) {
                fail(e.getMessage());
            }
        }
    }

    public AbstractStatementBuilder choseBuilder() {
        AbstractStatementBuilder builder = new StatementBuildler();

        Scanner sc = null;
        try {
            InputStream file = getClass().getResourceAsStream(assemblyFile);
            sc = new Scanner(file);

            String firstLine = sc.nextLine();

            // compare with the sicFlag defined above
            if (firstLine.strip().equals(SIC_FLAG)) {
                builder = new SicStatementBuilder();
            }
        } catch (Exception e) {
            fail(e.getMessage());
        } finally{
            if (sc != null) {
                sc.close();
            }
        }
        return builder;
    }

    public static Stack<Queue<Statement>> fileInput(InputStream filename, AbstractStatementBuilder builder) {

        // create the Stack that will be returned
        Stack<Queue<Statement>> stack = new Stack<>();

        // open up a new file and read the string
        // parse the string and create a list of lines

        try ( // read the file
                Scanner sc = new Scanner(filename)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                builder.processStatement(line);

            }
            stack = builder.getStatements();

        } catch (Exception e) {
            fail(e.getMessage());
        }

        return stack;
    }

    @Parameterized.Parameters()
    public static Collection<String[]> files() {
        return Arrays.asList(new String[][] {
                { "/testAsm1.asm", "/testAsm1.obj" },
                { "/testAsm2.asm", "/testAsm2.obj" },
                { "/testAsm3.asm", "/testAsm3.obj" },
                { "/testAsm4.asm", "/testAsm4.obj" },
        });
    }
}