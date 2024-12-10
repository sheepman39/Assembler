package edu.iu.jrsalata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

import javax.script.ScriptException;

/**
 * Interface for building and executing abstract statement builders.
 */
public interface AbstractStatementBuilderBuilderInterface {

    /**
     * Retrieves a queue of abstract statement builders.
     *
     * @return a queue of {@link AbstractStatementBuilder} instances.
     */
    public Queue<AbstractStatementBuilder> getBuilders();

    /**
     * Executes the statement builder builder to create the needed builders
     *
     * @throws InvalidAssemblyFileException if the assembly file is invalid.
     * @throws ScriptException if there is an error in the script execution.
     * @throws IOException if an I/O error occurs.
     */
    public void execute() throws InvalidAssemblyFileException, ScriptException, IOException;

    /**
     * Executes the statement builder builder with the provided input stream.
     *
     * @param file the input stream of the file to be executed.
     * @throws InvalidAssemblyFileException if the assembly file is invalid.
     * @throws ScriptException if there is an error in the script execution.
     * @throws IOException if an I/O error occurs.
     */
    public void execute(InputStream file)
            throws InvalidAssemblyFileException, ScriptException, IOException;

    /**
     * Sets the input file name for the statement builders.
     *
     * @param filename the name of the input file.
     */
    public void setInputFile(String filename);

}
