package edu.iu.jrsalata;

import java.io.File;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Logger;

import javax.script.ScriptException;

class Main {
    static final Logger logger = Logger.getLogger(Main.class.getName());
    static final String SIC_FLAG = "!USE SIC";

    public static void main(String[] args) {
        

        // open up the inputFile and look at the first line
        // to determine which factory to use
        Scanner sc = null;
        try {
            // Create an instance of the AbstractStatementBuilder

            String inputFile = "input.asm";

            // open up the input file and create a scanner to select the builder
            // note that we have to create a new scanner since chooseBuilder will close
            // the scanner given to it
            // this also ensures that after we read the first line, we can reuse the entire file
            File file = new File(inputFile);
            sc = new Scanner(file);
            AbstractStatementBuilder builder = choseBuilder(inputFile, sc);
            sc = new Scanner(file);

            // to allow for files to be appended, we will create the writer out here and
            // reset the queue each time
            String fileName = "output.obj";
            ObjectWriterInterface writer = new ObjectWriter();
            writer.setFileName(fileName);
            writer.setBuilder(builder);
            // Multiple control sections produces multiple Queues with statements
            Stack<Queue<Statement>> stack = fileInput(sc, builder);
            while (!stack.isEmpty()) {
                writer.setQueue(stack.pop());
                // write the object file
                writer.execute();
                logger.info("Object file successfully created");
            }

        } catch (InvalidAssemblyFileException e) {
            // inforamtive error message from our StatementFactory
            logger.severe("===========");
            logger.severe("ASSEMBLY FAILURE!!! Shutting down....");
            logger.severe(e.getMessage());
            logger.severe("===========");

        } catch (ScriptException e) {
            // inforamtive error message from our StatementFactory
            logger.severe("===========");
            logger.severe("EXPRESSION FAILURE!!! Shutting down....");
            logger.severe(e.getMessage());
            logger.severe("===========");

        } catch (Exception e) {
            logger.severe("Something went wrong...");
            logger.severe(e.getMessage());
        } finally {
            if (sc != null) {
                sc.close();
            }
            logger.info("Shutting down...");
        }
    }

    public static Stack<Queue<Statement>> fileInput(Scanner sc, AbstractStatementBuilder factory)
            throws InvalidAssemblyFileException, Exception {

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            factory.processStatement(line);
        }

        return factory.getStatements();
    }

    public static AbstractStatementBuilder choseBuilder(String inputFile, Scanner sc) {
        AbstractStatementBuilder builder = new StatementBuildler();

        try {

            String firstLine = sc.nextLine();

            // compare with the sicFlag defined above
            if (firstLine.strip().equals(SIC_FLAG)) {
                builder = new SicStatementBuilder();
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
        } finally{
            if (sc != null) {
                sc.close();
            }
        }
        return builder;
    }
}