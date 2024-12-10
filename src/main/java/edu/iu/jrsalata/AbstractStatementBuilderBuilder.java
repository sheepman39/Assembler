package edu.iu.jrsalata;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.script.ScriptException;

/**
 * The AbstractStatementBuilderBuilder class is responsible for creating and managing
 * a queue of AbstractStatementBuilder instances. It reads an input file or stream,
 * determines the appropriate builder to use, and processes the file to populate the
 * builder queue.
 * 
 * <p>This class implements the AbstractStatementBuilderBuilderInterface and provides
 * methods to set the input file, retrieve the builder queue, and execute the file
 * processing.</p>
 * 
 * <p>It supports both SIC and non-SIC builders, handles macro definitions, and manages
 * control sections within the input file.</p>
 * 
 * @see AbstractStatementBuilderBuilderInterface
 * @see AbstractStatementBuilder
 * @see SicStatementBuilder
 * @see StatementBuilder
 * @see MacroProcessorInterface
 * @see InvalidAssemblyFileException
 * @see ScriptException
 * @see FileNotFoundException
 */
public class AbstractStatementBuilderBuilder implements AbstractStatementBuilderBuilderInterface {

    /**
     * In order to differentiate between SIC and SIC/XE, all SIC
     * files must start with this flag
     */
    public static final String SIC_FLAG = "!USE SIC";

    /**
     * Standard logger for this class
     */
    public static final Logger LOGGER = Logger.getLogger(AbstractStatementBuilderBuilder.class.getName());

    /**
     * stores each of the created builders
     */ 
    protected Queue<AbstractStatementBuilder> builderQueue;

    /**
     * stores the name of the input file
     */
    protected String inputFile;

    /**
     * Constructs a new AbstractStatementBuilderBuilder with default values.
     * 
     * <p>
     * This constructor initializes the input file name to "input.asm" and 
     * initializes the builder queue as an empty LinkedList.
     * </p>
     */
    public AbstractStatementBuilderBuilder() {

        // initialize file names to defaults
        this.inputFile = "input.asm";
        this.builderQueue = new LinkedList<>();

    }

    /**
     * Sets the input file name.
     *
     * @param fileName the name of the input file to be set
     */
    @Override
    public void setInputFile(String fileName) {
        this.inputFile = fileName;
    }

    /**
     * Retrieves the queue of AbstractStatementBuilder instances.
     *
     * @return a Queue containing AbstractStatementBuilder objects.
     */
    @Override
    public Queue<AbstractStatementBuilder> getBuilders() {
        return this.builderQueue;
    }

    /**
     * Executes the process of reading an input file, selecting the appropriate
     * builder, and processing the file to populate the builder queue.
     *
     * @throws InvalidAssemblyFileException if the assembly file is invalid.
     * @throws FileNotFoundException if the input file is not found.
     * @throws ScriptException if there is an error in the script execution.
     */
    @Override
    public void execute() throws InvalidAssemblyFileException, FileNotFoundException, ScriptException {
        
        // Read the file and create a new scanner for it
        File file = new File(this.inputFile);
        Scanner sc = new Scanner(file);

        // grab the necessary builder from choseBuilder
        AbstractStatementBuilder builder = choseBuilder(sc);
        sc = new Scanner(file);

        // now read the entire input file
        this.builderQueue = fileInput(sc, builder);

        // close the open Scanner
        sc.close();
    }

    /**
     * Executes the process of reading and parsing an assembly file.
     *
     * @param file the InputStream of the assembly file to be processed
     * @throws InvalidAssemblyFileException if the assembly file is invalid
     * @throws ScriptException if there is an error in the script execution
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void execute(InputStream file)
            throws InvalidAssemblyFileException, ScriptException, IOException {

        // Since scanning consumes an InputStream, we will have to use a
        // BufferedInputStream in order for it to be reusable
        BufferedInputStream bufferedFile = new BufferedInputStream(file);

        // mark the starting location of the bufferedFile
        bufferedFile.mark(Integer.MAX_VALUE);

        Scanner sc = new Scanner(bufferedFile);
        AbstractStatementBuilder builder = choseBuilder(sc);

        // reset the buffer
        bufferedFile.reset();
        sc = new Scanner(bufferedFile);

        this.builderQueue = fileInput(sc, builder);

        sc.close();
    }

    /**
     * Processes the input from a Scanner and builds a queue of AbstractStatementBuilder objects.
     * 
     * This method reads lines from the provided Scanner, processes them, and constructs 
     * AbstractStatementBuilder objects based on the input. It handles control sections (CSECT) 
     * and macro definitions (MACRO, MEND) within the input.
     * 
     * @param sc the Scanner to read input from
     * @param builder the initial AbstractStatementBuilder to use for processing
     * @return a Queue of AbstractStatementBuilder objects representing the processed input
     * @throws InvalidAssemblyFileException if the input file contains invalid assembly code
     * @throws ScriptException if there is an error in processing a script
     */
    protected Queue<AbstractStatementBuilder> fileInput(Scanner sc, AbstractStatementBuilder builder)
            throws InvalidAssemblyFileException, ScriptException {

        Queue<AbstractStatementBuilder> queue = new LinkedList<>();

        // since we want to be able to keep the type of builder consistent, check if the
        // builder passed is an instance of the SIC builder
        boolean isSIC = builder instanceof SicStatementBuilder;

        // since we will handle macro definitions here,
        // we will use a boolean to control its definition
        boolean processingMacro = false;
        MacroProcessorInterface macroProcessor = new MacroProcessor();

        // line holds the current line in the file
        String line;

        while (sc.hasNextLine()) {
            line = sc.nextLine();
            line = Utility.cleanLine(line);

            // check if we are at the beginning of a control section
            // in order to create a new builder to handle it
            if (line.contains("CSECT")) {
                queue.add(builder);
                builder = isSIC ? new SicStatementBuilder() : new StatementBuilder();

                // handle setting the new name of the builder
                String[] parts = Utility.splitLine(line);
                builder.setName(parts[0]);
                continue;
            } else if (line.contains("MACRO")) {
                processingMacro = true;
                macroProcessor = handleMacroCreation(line);
                continue;
            } else if (line.contains("MEND")) {
                processingMacro = false;
                continue;
            }

            // macro definitions goes to the macroProcessor
            // everything else to the builder
            if (processingMacro) {
                macroProcessor.addLine(line);
            } else {
                builder.processStatement(line);
            }
        }

        // add the finished builder to the queue
        queue.add(builder);
        return queue;
    }

    /**
     * Handles the creation of a macro processor from a given line of macro definition.
     *
     * @param line The line containing the macro definition.
     * @return A MacroProcessorInterface instance created from the macro definition.
     * @throws InvalidAssemblyFileException If the macro definition is invalid.
     */
    protected MacroProcessorInterface handleMacroCreation(String line) throws InvalidAssemblyFileException {

        // first split up the macro definition line into sections
        String[] parts = Utility.splitLine(line);

        // if the length != 3, then we have an invalid definition
        if (parts.length != 3) {
            throw new InvalidAssemblyFileException(-1, "INVALID MACRO DEFINITION");
        }

        // create an array of each parameter defined with the macro
        String[] params = parts[2].split(",");

        // now create a processor with those params and store it for future use
        MacroProcessorInterface processor = new MacroProcessor(params);
        SymTable.addMacro(parts[0], processor);

        return processor;

    }

    /**
     * Chooses the appropriate statement builder based on the input from the scanner.
     * If the first line of the input matches the SIC_FLAG, a SicStatementBuilder is chosen.
     * Otherwise, a default StatementBuilder is used.
     *
     * @param sc the Scanner object used to read the input
     * @return an instance of AbstractStatementBuilder, either a StatementBuilder or SicStatementBuilder
     */
    protected AbstractStatementBuilder choseBuilder(Scanner sc) {
        AbstractStatementBuilder builder = new StatementBuilder();

        try {

            String firstLine = sc.nextLine();

            // compare with the sicFlag defined above
            if (firstLine.strip().equals(SIC_FLAG)) {
                builder = new SicStatementBuilder();
            }

        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }
        return builder;
    }
}
