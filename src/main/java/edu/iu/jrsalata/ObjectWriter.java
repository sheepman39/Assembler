// Class: ObjectWriter
// Implements: ObjectWriterInterface
// Provides a concrete way to generate object files

package edu.iu.jrsalata;

import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

public class ObjectWriter implements ObjectWriterInterface {
    static Logger logger = Logger.getLogger(Main.class.getName());
    protected String fileName;
    protected StatementFactoryInterface factory;
    protected Queue<Statement> queue;

    // constructors
    public ObjectWriter() {
        this.fileName = "output.obj";
        this.factory = new StatementFactory();
        this.queue = new LinkedList<Statement>();
    }

    public ObjectWriter(String fileName, StatementFactoryInterface factory, Queue<Statement> queue) {
        this.fileName = fileName;
        this.factory = factory;
        this.queue = queue;
    }

    // setters
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFactory(StatementFactoryInterface factory) {
        this.factory = factory;
    }

    public void setQueue(Queue<Statement> queue) {
        this.queue = queue;
    }

    // execute the writing of the object file
    public void execute() {
        try {
            // Create a file writter to be passed around to write each section of the obj
            // file
            FileWriter fileWriter = new FileWriter(this.fileName);
            writeHeaderRecord(fileWriter, this.factory);
            writeTextRecords(fileWriter, this.queue, this.factory);
            writeEndRecord(fileWriter, this.factory);
            fileWriter.close();
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    // Write the Header Record to the given obj file
    public static void writeHeaderRecord(FileWriter fileWriter, StatementFactoryInterface factory) {
        // Create the StringBuilder that will add each component
        // Start with the 'H'
        StringBuilder headerRecord = new StringBuilder();
        headerRecord.append("H");

        // Col 2-7 is program name
        headerRecord.append(factory.getName());

        // Col 8-13 is the starting address
        headerRecord.append(factory.getStart().toString(6));

        // Col 14-19 is the length of the program
        headerRecord.append(factory.getLen().toString(6));

        try {
            // write the final string to the header file
            fileWriter.write(headerRecord.toString());
            fileWriter.write('\n');
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    // Write the Text Record to the given obj file
    public static void writeTextRecords(FileWriter fileWriter, Queue<Statement> queue,
            StatementFactoryInterface factory) {

        // store the start to handle sizes
        HexNum start = new HexNum(factory.getStart().getDec());
        int tmpSize = 0;

        // Create the StringBuilder that will add each component
        StringBuilder textRecord = new StringBuilder();

        // Statement that will be read from the queue
        Statement statement;

        while (!queue.isEmpty()) {
            // Col 1 is "T"
            textRecord.append("T");

            // Col 2-7 is the starting address
            textRecord.append(start.toString(6));

            // Col 8-9 is the length of the record
            // We will put a placeholder here for now
            textRecord.append("--");

            // Col 10-69 is the text record
            int test = queue.peek().getSize().getDec();
            logger.info("Text Record Length: " + test);
            logger.info("Record Assemble: " + queue.peek().assemble());
            if (queue.peek() instanceof DirectiveStatement) {
                logger.info("Directive Keyword:" + ((DirectiveStatement) queue.peek()).getDirective());
            }

            tmpSize = textRecord.length();
            while (!queue.isEmpty() && (tmpSize + queue.peek().assemble().length() < 70)) {
                statement = queue.poll();
                textRecord.append(statement.assemble());
                start = start.add(statement.getSize());
                tmpSize = tmpSize + statement.getSize().getDec() * 2;
            }

            // Update the length of the record
            // since it is needed in bytes, we need to divide by 2 and round up
            int length = textRecord.length() - 9;
            length = (int) Math.ceil(length / 2.0);
            HexNum size = new HexNum(length);
            textRecord.replace(7, 9, size.toString(2));

            // Add the text record to the file
            try {
                fileWriter.write(textRecord.toString());
                fileWriter.write('\n');
            } catch (Exception e) {
                logger.severe(e.getMessage());
            }

            // Clear the text record
            textRecord.setLength(0);
        }

    }

    // Write the End Record to the given obj file
    public static void writeEndRecord(FileWriter fileWriter, StatementFactoryInterface factory) {

        // Create the StringBuilder that will add each component
        StringBuilder endRecord = new StringBuilder();

        // Col 1 is "E"
        endRecord.append("E");

        // Col 2-7 is the starting address
        endRecord.append(factory.getStart().toString(6));

        try {
            // write the final string to the header file
            fileWriter.write(endRecord.toString());
            fileWriter.write('\n');
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

}