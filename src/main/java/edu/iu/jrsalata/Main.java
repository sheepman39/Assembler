package edu.iu.jrsalata;

import java.util.LinkedList;
import java.util.Queue;
import java.io.File;
import java.util.Scanner;
import java.util.logging.Logger;

class Main {
    static Logger logger = Logger.getLogger(Main.class.getName());
    static final String sicFlag = "!USE SIC";

    public static void main(String[] args) {
        // Create an instance of the StatementFactory
        String inputFile = "input.asm";
        AbstractStatementFactory factory;

        // open up the inputFile and look at the first line
        // to determine which factory to use
        Scanner sc = null;
        try {
            File file = new File(inputFile);
            sc = new Scanner(file);

            String firstLine = sc.nextLine();

            // compare with the sicFlag defined above
            if (firstLine.strip().equals(sicFlag)) {
                factory = new SicStatementFactory();
                logger.info("Using SIC Factory");
            } else {
                factory = new StatementFactory();
                logger.info("Using SIC/XE Factory");

                // reset the scanner to the beginning of the file
                sc.close();
                sc = new Scanner(file);
            }
            Queue<Statement> queue = fileInput(sc, factory);
            String fileName = "output.obj";
            ObjectWriterInterface writer = new ObjectWriter(fileName, factory, queue);

            // write the object file
            writer.execute();
            logger.info("Object file successfully created");

        } catch (InvalidAssemblyFileException e) {
            // inforamtive error message from our StatementFactory
            logger.severe("===========");
            logger.severe("ASSEMBLY FAILURE!!! Shutting down....");
            logger.severe(e.getMessage());
            logger.severe("===========");

        } catch (Exception e) {
            logger.severe("Something went wrong...");
            logger.severe(e.getMessage());
        } finally {
            sc.close();
            logger.info("Shutting down...");
        }
    }

    public static Queue<Statement> fileInput(Scanner sc, AbstractStatementFactory factory)
            throws InvalidAssemblyFileException, Exception {

        // create the ArrayList that will be returned
        Queue<Statement> queue = new LinkedList<>();

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Statement statement = factory.processStatement(line);
            if (statement != null) {
                queue.add(statement);
            }
        }

        return queue;
    }
}