// Class: ObjectWriter
// Implements: ObjectWriterInterface
// Provides a concrete way to generate object files

package edu.iu.jrsalata;

import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

public class ObjectWriter implements ObjectWriterInterface {
    static Logger logger = Logger.getLogger(ObjectWriter.class.getName());
    protected String fileName;
    protected AbstractStatementBuilder builder;
    protected Queue<Statement> queue;

    // constructors
    public ObjectWriter() {
        this.fileName = "output.obj";
        this.builder = new StatementBuildler();
        this.queue = new LinkedList<>();
    }

    public ObjectWriter(String fileName, AbstractStatementBuilder builder, Queue<Statement> queue) {
        this.fileName = fileName;
        this.builder = builder;
        this.queue = queue;
    }

    // setters
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setBuilder(AbstractStatementBuilder builder) {
        this.builder = builder;
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
            writeHeaderRecord(fileWriter, this.builder);
            writeTextRecords(fileWriter, this.queue, this.builder);
            writeEndRecord(fileWriter, this.builder);
            fileWriter.close();

        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    // Write the Header Record to the given obj file
    public static void writeHeaderRecord(FileWriter fileWriter, AbstractStatementBuilder builder) {
        // Create the StringBuilder that will add each component
        // Start with the 'H'
        StringBuilder headerRecord = new StringBuilder();
        headerRecord.append("H");

        // Col 2-7 is program name
        headerRecord.append(builder.getName());

        // Col 8-13 is the starting address
        headerRecord.append(builder.getStart().toString(6));

        // Col 14-19 is the length of the program
        headerRecord.append(builder.getLen().toString(6));

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
            AbstractStatementBuilder builder) throws InvalidAssemblyFileException {

        // store the start to handle sizes
        HexNum start = new HexNum(builder.getStart().getDec());
        int tmpSize = 0;

        // Create the StringBuilder that will add each component
        StringBuilder textRecord = new StringBuilder();

        // Statement that will be read from the queue
        Statement statement;

        // create two queues to hold the assembled data and its respective size
        Queue<String> assembledQueue = new LinkedList<>();
        Queue<HexNum> sizeQueue = new LinkedList<>();

        // create the visitor that will collect modification records
        VisitorInterface visitor = new ModificationVisitor();

        while (!queue.isEmpty()) {
            statement = queue.poll();
            assembledQueue.add(statement.assemble());
            statement.accept(visitor);
            sizeQueue.add(statement.getSize());
        }

        while (!assembledQueue.isEmpty()) {
            // Col 1 is "T"
            textRecord.append("T");

            // Col 2-7 is the starting address
            textRecord.append(start.toString(6));

            // Col 8-9 is the length of the record
            // We will put a placeholder here for now
            textRecord.append("--");

            // Col 10-69 is the text record
            tmpSize = textRecord.length();
            while (!assembledQueue.isEmpty() && (tmpSize + assembledQueue.peek().length() < 70)) {
                textRecord.append(assembledQueue.poll());
                start = start.add(sizeQueue.peek());
                tmpSize = tmpSize + sizeQueue.poll().getDec() * 2;
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

        // after we are done writing the text records,
        // we need to write the modification records
        // since the visitor is local here, we are going to simply pass it
        // instead of making copies
        writeModificationRecords(fileWriter, visitor.getStrings());
    }

    public static void writeModificationRecords(FileWriter fileWriter, Queue<String> modifications) {
        // loop through each modification and write it
        while (!modifications.isEmpty()) {
            try {
                fileWriter.write(modifications.poll());
                fileWriter.write('\n');
            } catch (Exception e) {
                logger.severe(e.getMessage());
            }
        }
    }

    // Write the End Record to the given obj file
    public static void writeEndRecord(FileWriter fileWriter, AbstractStatementBuilder builder) {

        // Create the StringBuilder that will add each component
        StringBuilder endRecord = new StringBuilder();

        // Col 1 is "E"
        endRecord.append("E");

        // Col 2-7 is the starting address
        endRecord.append(builder.getStart().toString(6));

        try {
            // write the final string to the header file
            fileWriter.write(endRecord.toString());
            fileWriter.write('\n');
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

}
