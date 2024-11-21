package edu.iu.jrsalata;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.Queue;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ObjectWriterTest {
    Logger logger = Logger.getLogger(getClass().getName());
    String assemblyFile;
    String objectFile;

    // constructor to help with parameterization
    public ObjectWriterTest(String assemblyFile, String objectFile) {
        this.assemblyFile = assemblyFile;
        this.objectFile = objectFile;
    }

    @Test
    public void testAsm1() {
        // Create an instance of the StatementFactory
        // clear out the symtable since it is used in previous tests
        SymTable.clear();
        AbstractStatementBuilder builder = new SicStatementBuilder();
        InputStream file = getClass().getResourceAsStream("/testAsm1.asm");
        Queue<Statement> queue = fileInput(file, builder);
        String fileName = "test.obj";
        ObjectWriterInterface writer = new ObjectWriter(fileName, builder, queue);

        writer.execute();

        // now compare the output between the two files
        try {

            // read the original compare file
            InputStream control = getClass().getResourceAsStream("/testAsm1.obj");

            // read the generated file
            InputStream test = new FileInputStream(fileName);

            // create a scanner for each file
            Scanner scControl = new Scanner(control);
            Scanner scTest = new Scanner(test);

            // compare the two files
            while (scControl.hasNextLine() && scTest.hasNextLine()) {
                String lineControl = scControl.nextLine();
                String lineTest = scTest.nextLine();
                assertEquals(lineControl.toLowerCase(), lineTest.toLowerCase());
            }

            scControl.close();
            scTest.close();

            // delete the generated test file
            new File(fileName).delete();
        } catch (Exception e) {
            logger.warning(e.getMessage());
            assertEquals(true, false);
        }
    }

    @Test
    public void testAsm2() {
        // Create an instance of the StatementFactory
        // clear out the symtable since it is used in previous tests
        SymTable.clear();
        AbstractStatementBuilder factory = new StatementBuildler();
        InputStream file = getClass().getResourceAsStream(assemblyFile);
        Queue<Statement> queue = fileInput(file, factory);
        String fileName = "test.obj";
        ObjectWriterInterface writer = new ObjectWriter(fileName, factory, queue);

        writer.execute();

        // now compare the output between the two files
        try {

            // read the original compare file
            InputStream control = getClass().getResourceAsStream(objectFile);

            // read the generated file
            InputStream test = new FileInputStream(fileName);

            // create a scanner for each file
            Scanner scControl = new Scanner(control);
            Scanner scTest = new Scanner(test);

            // compare the two files
            while (scControl.hasNextLine() && scTest.hasNextLine()) {
                String lineControl = scControl.nextLine();
                String lineTest = scTest.nextLine();
                assertEquals(lineControl.toLowerCase(), lineTest.toLowerCase());
            }

            scControl.close();
            scTest.close();

            // delete the generated test file
            new File(fileName).delete();
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }

    public static Queue<Statement> fileInput(InputStream filename, AbstractStatementBuilder builder) {

        // create the ArrayList that will be returned
        Queue<Statement> queue = new LinkedList<Statement>();

        // open up a new file and read the string
        // parse the string and create a list of lines
        try {

            // read the file
            Scanner sc = new Scanner(filename);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                builder.processStatement(line);

            }
            queue = builder.getStatements();
            sc.close();
        } catch (Exception e) {
            System.out.println("File not found");
            System.err.println(e);
        }

        return queue;
    }

    @Parameterized.Parameters()
    public static Collection<String[]> files() {
        return Arrays.asList(new String[][] {
                { "/testAsm2.asm", "/testAsm2.obj" },
                { "/testAsm3.asm", "/testAsm3.obj" },
        });
    }
}
