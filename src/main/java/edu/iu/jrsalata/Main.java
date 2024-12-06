package edu.iu.jrsalata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.logging.Logger;

import javax.script.ScriptException;

class Main {
    static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        try {
            // Create an instance of the AbstractStatementBuilder
            String inputFile = "input.asm";

            // create a new builderBuilder to handle all file IO and building
            AbstractStatementBuilderBuilderInterface builderBuilder = new AbstractStatementBuilderBuilder();
            builderBuilder.setInputFile(inputFile);

            // test
            InputStream file = Main.class.getResourceAsStream("/testAsm5.asm");
            // execute the builderBuilder
            builderBuilder.execute(file);

            // grab each of the builder queues
            Queue<AbstractStatementBuilder> queue = builderBuilder.getBuilders();

            // handle the writer here
            String fileName = "output.obj";
            ObjectWriterInterface writer = new ObjectWriter();
            writer.setFileName(fileName);

            // go through the queue of builders and write the object files
            AbstractStatementBuilder builder;
            while (!queue.isEmpty()) {
                builder = queue.poll();
                writer.setBuilder(builder);
                writer.setQueue(builder.getStatements());
                // write the object file
                writer.execute();
                logger.info("Object file successfully created");
            }

        } catch (InvalidAssemblyFileException e) {
            // informative error message from our StatementFactory
            logger.severe("===========");
            logger.severe("ASSEMBLY FAILURE!!! Shutting down....");
            logger.severe(e.getMessage());
            logger.severe("===========");

        } catch (ScriptException e) {
            // informative error message from our StatementFactory
            logger.severe("===========");
            logger.severe("EXPRESSION FAILURE!!! Shutting down....");
            logger.severe(e.getMessage());
            logger.severe("===========");

        } catch (IOException e) {
            logger.severe("Something went wrong...");
            logger.severe(e.getMessage());
        } finally {
            logger.info("Shutting down...");
        }
    }
}