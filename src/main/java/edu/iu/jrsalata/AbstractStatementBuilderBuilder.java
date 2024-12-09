// Class: AbstractStatementBuilderBuilder
// Implements: AbstractStatementBuilderBuilderInterface
// This is a builder builder to handle more complex files and simplify the input process

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

public class AbstractStatementBuilderBuilder implements AbstractStatementBuilderBuilderInterface {

    // SIC FLAG holds the flag for the SIC builder
    public static final String SIC_FLAG = "!USE SIC";
    public static final Logger LOGGER = Logger.getLogger(AbstractStatementBuilderBuilder.class.getName());

    // builderQueue will hold each of the created builders
    protected Queue<AbstractStatementBuilder> builderQueue;

    protected String inputFile;

    public AbstractStatementBuilderBuilder() {

        // initialize file names to defaults
        this.inputFile = "input.asm";
        this.builderQueue = new LinkedList<>();

    }

    @Override
    public void setInputFile(String fileName) {
        this.inputFile = fileName;
    }

    @Override
    public Queue<AbstractStatementBuilder> getBuilders() {
        return this.builderQueue;
    }

    @Override
    public void execute() throws InvalidAssemblyFileException, FileNotFoundException, ScriptException {
        // Read the file and create a new scanner for it
        File file = new File(this.inputFile);
        Scanner sc = new Scanner(file);

        AbstractStatementBuilder builder = choseBuilder(sc);

        sc = new Scanner(file);
        // now read the entire input file
        this.builderQueue = fileInput(sc, builder);

    }

    @Override
    public void execute(InputStream file)
            throws InvalidAssemblyFileException, ScriptException, IOException {

        // Since scanning consumes an InputStream, we will have to use a
        // BufferedInputStream
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

    protected Queue<AbstractStatementBuilder> fileInput(Scanner sc, AbstractStatementBuilder builder)
            throws InvalidAssemblyFileException, ScriptException {

        Queue<AbstractStatementBuilder> queue = new LinkedList<>();

        // since we want to be able to keep the type of builder consistent, check if the
        // builder passed is an instance of the SIC builder
        boolean isSIC = builder instanceof SicStatementBuilder;

        // since we will handle macro definitions here, 
        // we will use a boolean to control its definition
        boolean processingMacro = false;
        MacroProcessorInterface macroProcessor;

        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            // check if we are at the beginning of a control section
            // in order to create a new builder to handle it
            if (line.contains("CSECT")) {
                queue.add(builder);
                builder = isSIC ? new SicStatementBuilder() : new StatementBuilder();

                // handle setting the new name of the builder
                String[] parts = Utility.splitLine(line);
                builder.setName(parts[0]);
                continue;
            } else if(line.contains("MACRO")){

                
            }
            builder.processStatement(line);
        }
        queue.add(builder);
        return queue;
    }

    protected void handleMacroCreation(String line) throws InvalidAssemblyFileException {

        // first split up the macro definition line into sections
        String[] parts = Utility.splitLine(line);

        // if the length != 3, then we have an invalid definition
        if(parts.length != 3){
            throw new InvalidAssemblyFileException(-1, "INVALID MACRO DEFINITION");
        }
    }

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
