package edu.iu.jrsalata;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.Queue;
import java.util.LinkedList;
import org.junit.Test;

public class ObjectWriterTest {
    Logger logger = Logger.getLogger(getClass().getName());

    @Test
    public void testAsm1() {
        // Create an instance of the StatementFactory
        // clear out the symtable since it is used in previous tests
        SymTable.clear();
        StatementFactoryInterface factory = new SicStatementFactory();
        InputStream file = getClass().getResourceAsStream("/testAsm1.asm");
        Queue<Statement> queue = fileInput(file, factory);
        String fileName = "test.obj";
        ObjectWriterInterface writer = new ObjectWriter(fileName, factory, queue);

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
        }
    }

    @Test
    public void testAsm2() {
        // Create an instance of the StatementFactory
        // clear out the symtable since it is used in previous tests
        SymTable.clear();
        StatementFactoryInterface factory = new StatementFactory();
        InputStream file = getClass().getResourceAsStream("/testAsm2.asm");
        Queue<Statement> queue = fileInput(file, factory);
        String fileName = "test.obj";
        ObjectWriterInterface writer = new ObjectWriter(fileName, factory, queue);

        writer.execute();

        // now compare the output between the two files
        try {

            // read the original compare file
            InputStream control = getClass().getResourceAsStream("/testAsm2.obj");

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

    public static Queue<Statement> fileInput(InputStream filename, StatementFactoryInterface factory) {

        // create the ArrayList that will be returned
        Queue<Statement> queue = new LinkedList<Statement>();

        // open up a new file and read the string
        // parse the string and create a list of lines
        try {

            // read the file
            Scanner sc = new Scanner(filename);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                Statement statement = factory.processStatement(line);
                if (statement != null) {
                    queue.add(statement);
                }
                
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("File not found");
            System.err.println(e);
        }

        return queue;
    }
}
