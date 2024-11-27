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
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ObjectWriterTest {
    static final Logger LOGGER = Logger.getLogger(ObjectWriterTest.class.getName());
    String assemblyFile;
    String objectFile;

    // constructor to help with parameterization
    public ObjectWriterTest(String assemblyFile, String objectFile) {
        this.assemblyFile = assemblyFile;
        this.objectFile = objectFile;
    }

    @Test
    public void testSicAsm() {
        // Create an instance of the StatementFactory
        // clear out the symtable since it is used in previous tests
        SymTable.clear();
        AbstractStatementBuilder builder = new SicStatementBuilder();
        InputStream file = getClass().getResourceAsStream("/testAsm1.asm");
        Queue<Statement> queue = fileInput(file, builder);
        String fileName = "test.obj";
        ObjectWriterInterface writer = new ObjectWriter(fileName, builder, queue);

        // now compare the output between the two files
        try {
            writer.execute();
            // read the original compare file
            InputStream control = getClass().getResourceAsStream("/testAsm1.obj");

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
            LOGGER.warning(e.getMessage());
            fail(e.getMessage());
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
            LOGGER.warning(e.getMessage());
            fail(e.getMessage());
        }
    }

    public static Queue<Statement> fileInput(InputStream filename, AbstractStatementBuilder builder) {

        // create the ArrayList that will be returned
        Queue<Statement> queue = new LinkedList<>();

        // open up a new file and read the string
        // parse the string and create a list of lines

        try ( // read the file
                Scanner sc = new Scanner(filename)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                builder.processStatement(line);

            }
            queue = builder.getStatements();

        } catch (Exception e) {
            fail(e.getMessage());
        }

        return queue;
    }

    @Parameterized.Parameters()
    public static Collection<String[]> files() {
        return Arrays.asList(new String[][] {
                { "/testAsm2.asm", "/testAsm2.obj" },
                { "/testAsm3.asm", "/testAsm3.obj" },
                { "/testAsm4.asm", "/testAsm4.obj" },
        });
    }
}
