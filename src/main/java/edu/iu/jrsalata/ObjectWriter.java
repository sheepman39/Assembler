package edu.iu.jrsalata;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * The ObjectWriter class is responsible for writing object files based on the
 * provided statements and builder. It implements the ObjectWriterInterface.
 * This class supports writing various records such as header, define, refer,
 * text, and end records to the object file.
 */
public class ObjectWriter implements ObjectWriterInterface {

    /**
     * stores the name of the output file
     */
    protected String fileName;

    /**
     * holds the current builder to write its output
     */
    protected AbstractStatementBuilder builder;

    /**
     * queue stores the current queue of statements
     * Possibly unnecessary?
     */
    protected Queue<Statement> queue;

    /**
     * previouslyUsed is a boolean that indicates if the writer was used before
     * If so, output needs to be appended
     */
    protected boolean previouslyUsed;

    /**
     * Constructs an ObjectWriter with default settings.
     * Initializes the fileName to "output.obj", creates a new StatementBuilder,
     * initializes the queue as a LinkedList, and sets previouslyUsed to false.
     */
    public ObjectWriter() {
        this.fileName = "output.obj";
        this.builder = new StatementBuilder();
        this.queue = new LinkedList<>();
        this.previouslyUsed = false;
    }

    /**
     * Constructs an ObjectWriter with the specified file name, statement builder, and queue of statements.
     *
     * @param fileName the name of the file to write to
     * @param builder the builder used to create statements
     * @param queue the queue of statements to be written
     */
    public ObjectWriter(String fileName, AbstractStatementBuilder builder, Queue<Statement> queue) {
        this.fileName = fileName;
        this.builder = builder;
        this.queue = queue;
        this.previouslyUsed = false;
    }

    /**
     * Sets the name of the file.
     *
     * @param fileName the name of the file to be set
     */
    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Sets the builder for this ObjectWriter.
     *
     * @param builder the AbstractStatementBuilder to be used by this ObjectWriter
     */
    @Override
    public void setBuilder(AbstractStatementBuilder builder) {
        this.builder = builder;
    }

    /**
     * Sets the queue of statements to be processed.
     *
     * @param queue the queue of statements to set
     */
    @Override
    public void setQueue(Queue<Statement> queue) {
        this.queue = queue;
    }

    /**
     * Executes the process of writing an object file. This method writes various
     * sections of the object file including the header, define, refer, text, and
     * end records. It uses a FileWriter to write to the specified file.
     *
     * @throws InvalidAssemblyFileException if the assembly file is invalid.
     * @throws IOException if an I/O error occurs while writing to the file.
     */
    @Override
    public void execute() throws InvalidAssemblyFileException, IOException {

        try (FileWriter fileWriter = new FileWriter(this.fileName, this.previouslyUsed)) {
            try (FileWriter debugWriter = new FileWriter(this.fileName + ".txt", this.previouslyUsed)) {
                writeHeaderRecord(fileWriter, this.builder);
                writeDefineRecord(fileWriter, this.builder);
                writeReferRecords(fileWriter, this.builder);
                writeTextRecords(fileWriter, this.queue, this.builder, debugWriter);
                writeEndRecord(fileWriter, this.builder);

                // set previously used to true to indicate we want to append in the future
                this.previouslyUsed = true;
            }
        } catch (IOException e) {
            throw new IOException("Error writing to file: " + this.fileName);
        }
    }

    /**
     * Writes a header record to the specified FileWriter.
     * The header record consists of:
     * - 'H' character at the beginning
     * - Program name (columns 2-7)
     * - Starting address (columns 8-13)
     * - Length of the program (columns 14-19)
     *
     * @param fileWriter the FileWriter to write the header record to
     * @param builder the AbstractStatementBuilder containing the program details
     * @throws IOException if an I/O error occurs
     */
    public void writeHeaderRecord(FileWriter fileWriter, AbstractStatementBuilder builder) throws IOException {
        StringBuilder headerRecord = new StringBuilder();
        headerRecord.append("H");
        headerRecord.append(builder.getName());
        headerRecord.append(builder.getStart().toString(6));
        headerRecord.append(builder.getTotalLength().toString(6));
        fileWriter.write(headerRecord.toString().toUpperCase());
        fileWriter.write('\n');
    }

    /**
     * Writes a define record to the specified FileWriter using the provided AbstractStatementBuilder.
     * A define record starts with 'D' and contains pairs of external symbol names and their addresses.
     * Each name/address pair occupies 12 columns, and the total length of the define record is limited to 73 columns.
     * The define record consists of:
     * - 'D' character at the beginning
     * - Symbol name (columns 2-7)
     * - Symbol address (columns 8-13)
     * - repeat name and address until we reach column 73
     * 
     * @param fileWriter the FileWriter to write the define record to
     * @param builder the AbstractStatementBuilder that provides the external definitions
     * @throws IOException if an I/O error occurs
     */
    public void writeDefineRecord(FileWriter fileWriter, AbstractStatementBuilder builder) throws IOException {

        Queue<String> builderQueue = builder.getExternalDefinitions();
        StringBuilder defineRecord;
        String symbolName;

        while (!builderQueue.isEmpty()) {
            
            defineRecord = new StringBuilder();
            defineRecord.append("D");
            while (!builderQueue.isEmpty() && defineRecord.length() <= 60) {
                symbolName = builderQueue.poll();
                defineRecord.append(symbolName);
                defineRecord.append(SymTable.getSymbol(symbolName, builder.getName()).toString(6));
            }

            fileWriter.write(defineRecord.toString().toUpperCase());
            fileWriter.write('\n');

            // clear the StringBuilder to reset it for the next record
            defineRecord.setLength(0);
        }
    }

    /**
     * Writes the reference records to the specified file using the provided FileWriter.
     * The refer record consists of:
     * - 'R' character at the beginning
     * - Symbol name (columns 2-7)
     * - repeat names of symbols until we reach column 73
     *
     * @param fileWriter the FileWriter to write the reference records to
     * @param builder the AbstractStatementBuilder that provides the external references
     * @throws IOException if an I/O error occurs
     */
    public void writeReferRecords(FileWriter fileWriter, AbstractStatementBuilder builder) throws IOException {

        Queue<String> builderQueue = builder.getExternalReferences();
        StringBuilder referRecord;
        String symbolName;

        while (!builderQueue.isEmpty()) {

            referRecord = new StringBuilder();
            referRecord.append("R");
            while (!builderQueue.isEmpty() && referRecord.length() < 66) {
                symbolName = builderQueue.poll();
                referRecord.append(symbolName);
            }

            fileWriter.write(referRecord.toString().toUpperCase());
            fileWriter.write('\n');

            // clear the StringBuilder to reset it for the next record
            referRecord.setLength(0);
        }
    }

    /**
     * Writes text records to the provided FileWriter based on the given queue of Statements.
     * Each text record is constructed by assembling the statements and appending their byte code.
     * The method also handles different program blocks and ensures that text records are correctly
     * formatted and written to the file.
     * The text record consists of:
     * - 'T' character at the beginning
     * - Starting address (columns 2-7)
     * - Length (in bytes) (columns 8-9)
     * - Object code (10-69)
     *
     * @param fileWriter the FileWriter to write the text records to
     * @param queue the queue of Statements to be assembled and written
     * @param builder the AbstractStatementBuilder used to get starting addresses and other information
     * @throws InvalidAssemblyFileException if there is an error in the assembly file
     * @throws IOException if an I/O error occurs
     */
    public void writeTextRecords(FileWriter fileWriter, Queue<Statement> queue,
            AbstractStatementBuilder builder, FileWriter debugWriter) throws InvalidAssemblyFileException, IOException {

        // hold the length of the current assembled text record
        int tempRecordLength;

        // holds the length of an entire text record
        int length;
        
        // Used to convert length to hex
        HexNum size;

        // Used to store the current program block
        String currentBlock;

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

        // in order to ensure that nothing is assembled after a blank space, we will use
        // a boolean to track the current status of the blanks
        // blankSpace recognizes when an assembled value has a size, but no generated
        // object code
        // when that happens, start a new line
        boolean blankSpace;

        while (!queue.isEmpty()) {

            currentBlock = queue.peek().getBlock();
            blankSpace = false;

            // If the block is not in the start table, add it
            startTable.putIfAbsent(currentBlock, builder.getStart(currentBlock));

            // set the locctr
            currentStartLocctr = startTable.get(currentBlock);

            // Clear the text record
            textRecord.setLength(0);
            assembledTextRecord.setLength(0);

            textRecord.append("T");
            textRecord.append(currentStartLocctr.toString(6));
            textRecord.append("--");
            tempRecordLength = textRecord.length();
            
            // loop while
            // 1) we have items left in the queue
            // 2) there is room for the next assembled record
            // 3) we are in the same program block
            // 4) the previously assembled item was not a blank space
            while (!queue.isEmpty() && (tempRecordLength + queue.peek().assemble().length() < 70)
                    && queue.peek().getBlock().equals(currentBlock)
                    && !blankSpace) {
                statement = queue.poll();
                assembledTextRecord.append(statement.assemble());
                currentStartLocctr = currentStartLocctr.add(statement.getSize());
                tempRecordLength = tempRecordLength + statement.getSize().getDec() * 2;
                statement.accept(visitor);

                // if the assembled value is a blank space and it generates some space, then we
                // need to set the blankSpace flag to true
                blankSpace = statement.assemble().equals("") && statement.getSize().getDec() > 0;

                // we also need to write out every statement
                debugWriter.write(statement.assemble());
                debugWriter.write('\t');
                debugWriter.write(statement.getLine());
                debugWriter.write('\n');
            }

            // update the currentStartLocctr
            startTable.put(currentBlock, currentStartLocctr);

            if (assembledTextRecord.length() == 0) {
                // if the assembledTextRecord is empty, then we need to skip this block
                // and move on to the next block
                continue;
            }

            textRecord.append(assembledTextRecord);

            // Update the length of the record
            // since it is needed in bytes, we need to divide by 2 and round up
            length = textRecord.length() - 9;
            length = (int) Math.ceil(length / 2.0);
            size = new HexNum(length);
            textRecord.replace(7, 9, size.toString(2));

            // Add the text record to the file
            fileWriter.write(textRecord.toString().toUpperCase());
            fileWriter.write('\n');

        }

        // after we are done writing the text records,
        // we need to write the modification records with our visitor here
        writeModificationRecords(fileWriter, visitor.getStrings(), builder);
    }

    /**
     * Writes modification records to the provided FileWriter.
     * 
     * This method processes a queue of modification strings and writes each one to the
     * FileWriter, followed by a newline character. After processing the queue, it writes
     * any additional modification records stored in the provided AbstractStatementBuilder.
     * 
     * The definition of modification records can vary and is defined in the other methods that
     * define them
     * 
     * @param fileWriter the FileWriter to write the modification records to
     * @param modifications a queue of modification strings to be written
     * @param builder an AbstractStatementBuilder containing additional modification records
     * @throws IOException if an I/O error occurs
     */
    public void writeModificationRecords(FileWriter fileWriter, Queue<String> modifications,
            AbstractStatementBuilder builder) throws IOException {
        // loop through each modification and write it
        while (!modifications.isEmpty()) {

            fileWriter.write(modifications.poll().toUpperCase());
            fileWriter.write('\n');

        }

        // write any modification records that were stored in the builder
        List<String> modificationRecords = builder.getReferenceModifications();
        for (String records : modificationRecords) {
            fileWriter.write(records.toUpperCase());
            fileWriter.write('\n');
        }
    }

    /**
     * Writes the end record to the specified FileWriter.
     * The end record consists of the character 'E' followed by the starting address
     * if this is the first output. All other outputs are just 'E'
     *
     * @param fileWriter the FileWriter to write the end record to
     * @param builder the AbstractStatementBuilder containing the starting address
     * @throws IOException if an I/O error occurs
     */
    public void writeEndRecord(FileWriter fileWriter, AbstractStatementBuilder builder) throws IOException {

        // Create the StringBuilder that will add each component
        StringBuilder endRecord = new StringBuilder();
        endRecord.append("E");

        // only write starting address if this is the first output
        // since everything else depends on the first
        if (!this.previouslyUsed) {

            endRecord.append(builder.getStart().toString(6));

        }

        // write the final string to the header file
        fileWriter.write(endRecord.toString().toUpperCase());
        fileWriter.write('\n');
    }

}
