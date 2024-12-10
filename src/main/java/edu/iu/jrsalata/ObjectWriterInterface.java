package edu.iu.jrsalata;

import java.io.IOException;
import java.util.Queue;

/**
 * The ObjectWriterInterface defines the contract for writing objects to a file.
 * Implementations of this interface are responsible for setting the file name,
 * the statement builder, the queue of statements, and executing the writing process.
 */
public interface ObjectWriterInterface {

    /**
     * Sets the name of the output file.
     *
     * @param fileName the name of the file to be set
     */
    public void setFileName(String fileName);

    /**
     * Sets the builder to be used for constructing statements.
     *
     * @param builder the AbstractStatementBuilder instance to be set
     */
    public void setBuilder(AbstractStatementBuilder builder);

    /**
     * Sets the queue of statements.
     *
     * @param queue the queue of statements to be set
     */
    public void setQueue(Queue<Statement> queue);

    /**
     * Executes the object writing process to generate an output object file
     *
     * @throws InvalidAssemblyFileException if the assembly file is invalid.
     * @throws IOException if an I/O error occurs during the writing process.
     */
    public void execute() throws InvalidAssemblyFileException, IOException;
}
