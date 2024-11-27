// Class: ObjectWriter
// Implements: ObjectWriterInterface
// Provides a concrete way to generate object files

package edu.iu.jrsalata;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class ObjectWriter implements ObjectWriterInterface {
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
    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void setBuilder(AbstractStatementBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void setQueue(Queue<Statement> queue) {
        this.queue = queue;
    }

    // execute the writing of the object file
    @Override
    public void execute() throws InvalidAssemblyFileException, IOException {

        try (
                // Create a file writter to be passed around to write each section of the obj
                // file
                FileWriter fileWriter = new FileWriter(this.fileName)) {
            writeHeaderRecord(fileWriter, this.builder);
            writeTextRecords(fileWriter, this.queue, this.builder);
            writeEndRecord(fileWriter, this.builder);

        } catch (IOException e) {
            // Caller will have to handle exceptions to ensure that the user knows
            // an error has occureds
            throw new IOException("Error writing to file: " + this.fileName);
        }
    }

    // Write the Header Record to the given obj file
    public static void writeHeaderRecord(FileWriter fileWriter, AbstractStatementBuilder builder) throws IOException {
        // Create the StringBuilder that will add each component
        // Start with the 'H'
        StringBuilder headerRecord = new StringBuilder();
        headerRecord.append("H");

        // Col 2-7 is program name
        headerRecord.append(builder.getName());

        // Col 8-13 is the starting address
        headerRecord.append(builder.getStart().toString(6));

        // Col 14-19 is the length of the program
        headerRecord.append(builder.getTotalLength().toString(6));

        // write the final string to the header file
        fileWriter.write(headerRecord.toString());
        fileWriter.write('\n');

    }

    // Write the Text Record to the given obj file
    public static void writeTextRecords(FileWriter fileWriter, Queue<Statement> queue,
            AbstractStatementBuilder builder) throws InvalidAssemblyFileException, IOException {

        // hold the length of the current assembled text record
        int tempRecordLength;

        // Create the StringBuilder that will add each component
        StringBuilder textRecord = new StringBuilder();

        // assembledTextRecord will temporarily store the assembled byte code
        // if none is produced, then we move on to the next block
        StringBuilder assembledTextRecord = new StringBuilder();

        // Statement that will be read from the queue
        Statement statement;

        // create the visitor that will collect modification records
        VisitorInterface visitor = new ModificationVisitor();

        // Create a hashmap to store the starting address of each block
        // this is needed for each separate program block
        // every time we switch blocks, we need to update the previous and current block
        HashMap<String, HexNum> startTable = new HashMap<>();
        startTable.put(AbstractStatementBuilder.DEFAULT_BLOCK,
                builder.getStart(AbstractStatementBuilder.DEFAULT_BLOCK));
        HexNum currentStartLocctr;

        while (!queue.isEmpty()) {

            // Get the current block
            String currentBlock = queue.peek().getBlock();

            // If the block is not in the start table, add it
            if (!startTable.containsKey(currentBlock)) {
                startTable.put(currentBlock, builder.getStart(currentBlock));
            }
            // set the locctr
            currentStartLocctr = startTable.get(currentBlock);

            // Clear the text record
            textRecord.setLength(0);
            assembledTextRecord.setLength(0);

            // Col 1 is "T"
            textRecord.append("T");

            // Col 2-7 is the starting address
            textRecord.append(currentStartLocctr.toString(6));

            // Col 8-9 is the length of the record
            // We will put a placeholder here for now
            textRecord.append("--");

            // Col 10-69 is the text record
            tempRecordLength = textRecord.length();
            while (!queue.isEmpty() && (tempRecordLength + queue.peek().assemble().length() < 70)
                    && queue.peek().getBlock().equals(currentBlock)) {
                statement = queue.poll();
                assembledTextRecord.append(statement.assemble());
                currentStartLocctr = currentStartLocctr.add(statement.getSize());
                tempRecordLength = tempRecordLength + statement.getSize().getDec() * 2;
                statement.accept(visitor);
            }

            // update the currentStartLocctr
            startTable.put(currentBlock, currentStartLocctr);

            if (assembledTextRecord.length() == 0) {
                // if the assembledTextRecord is empty, then we need to skip this block
                // and move on to the next block
                continue;
            }

            // append the assembled text record to the text record
            textRecord.append(assembledTextRecord);

            // Update the length of the record
            // since it is needed in bytes, we need to divide by 2 and round up
            int length = textRecord.length() - 9;
            length = (int) Math.ceil(length / 2.0);
            HexNum size = new HexNum(length);
            textRecord.replace(7, 9, size.toString(2));

            // Add the text record to the file

            fileWriter.write(textRecord.toString());
            fileWriter.write('\n');

        }

        // after we are done writing the text records,
        // we need to write the modification records
        // since the visitor is local here, we are going to simply pass it
        // instead of making copies
        writeModificationRecords(fileWriter, visitor.getStrings());
    }

    public static void writeModificationRecords(FileWriter fileWriter, Queue<String> modifications) throws IOException {
        // loop through each modification and write it
        while (!modifications.isEmpty()) {

            fileWriter.write(modifications.poll());
            fileWriter.write('\n');

        }
    }

    // Write the End Record to the given obj file
    public static void writeEndRecord(FileWriter fileWriter, AbstractStatementBuilder builder) throws IOException {

        // Create the StringBuilder that will add each component
        StringBuilder endRecord = new StringBuilder();

        // Col 1 is "E"
        endRecord.append("E");

        // Col 2-7 is the starting address
        endRecord.append(builder.getStart().toString(6));

        // write the final string to the header file
        fileWriter.write(endRecord.toString());
        fileWriter.write('\n');

    }

}
