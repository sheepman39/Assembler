package edu.iu.jrsalata;

import java.util.Queue;

/**
 * Interface for a Macro Processor that handles the processing of macros.
 */
public interface MacroProcessorInterface {

    /**
     * Adds a line of unprocessed assembly to the processor.
     *
     * @param line the line of code to be added
     */
    public void addLine(String line);

    /**
     * Sets the label for the current macro being processed.
     *
     * @param label the label to be set
     */
    public void setLabel(String label);

    /**
     * Retrieves the processed lines of macro assembly code, substituting any macro arguments.
     *
     * @param args the arguments to be substituted in the macro
     * @return a queue of processed lines of code
     * @throws InvalidAssemblyFileException if the assembly file is invalid
     */
    public Queue<String> getLines(String[] args) throws InvalidAssemblyFileException;
}
