package edu.iu.jrsalata;

/**
 * Exception thrown to indicate that there is an error with the assembly file.
 * This exception can be thrown with a default message, a specific line number,
 * or a line number along with a custom message.
 */
public class InvalidAssemblyFileException extends Exception {

    /**
     * Exception thrown when there is an error with the input assembly file.
     */
    public InvalidAssemblyFileException() {
        super("Error with input file");
    }

    /**
     * Exception thrown when an invalid assembly file is encountered.
     *
     * @param lineNum the line number where the error occurred
     */
    public InvalidAssemblyFileException(int lineNum) {
        super("Error on line " + lineNum);
    }

    /**
     * Exception thrown when an invalid assembly file is encountered.
     *
     * @param lineNum the line number where the error occurred
     * @param msg the error message describing the issue
     */
    public InvalidAssemblyFileException(int lineNum, String msg) {
        super("Error on line " + lineNum + ": " + msg);
    }
}
