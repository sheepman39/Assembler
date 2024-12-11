package edu.iu.jrsalata;

/**
 * The Statement class represents an abstract base class for different types of statements
 * in an assembler.
 */
public abstract class Statement {
    /**
     * size represents how many bytes long the statement is
     */
    protected HexNum size;

    /**
     * block represents the current program block the statement is in
     */
    protected String block;

    /**
     * controlSection represents the current controlSection of the statement
     */
    protected String controlSection;

    /**
     * line represents the line of assembly this statement represents
     */
    protected String line;

    
    /**
     * Protected constructor for the Statement class.
     * Initializes a new Statement object with default values.
     * Sets the size to a new HexNum object with a value of 0.
     * Sets the block to an empty string.
     */
    protected Statement() {
        this.size = new HexNum(0);
        this.block = "";
        this.line = "";
    }

    /**
     * Constructs a new Statement with the specified size.
     *
     * @param size the size of the statement as a HexNum object
     */
    protected Statement(HexNum size) {
        this.size = size;
        this.block = "";
    }

    /**
     * Constructs a new Statement with the specified size and block.
     *
     * @param size  the size of the statement as a HexNum
     * @param block the block of the statement as a String
     */
    protected Statement(HexNum size, String block) {
        this.size = size;
        this.block = block;
    }

    /**
     * Returns the size of the statement as a HexNum.
     *
     * @return the size of the statement
     */
    public HexNum getSize() {
        return this.size;
    }

    /**
     * Retrieves the block associated with this statement.
     *
     * @return the block as a String
     */
    public String getBlock() {
        return this.block;
    }

    /**
     * Retrieves the control section of the statement.
     *
     * @return the control section as a String.
     */
    public String getControlSection() {
        return this.controlSection;
    }

    /**
     * Retrieves the line of the statement
     * 
     * @return the human-readable line
     */
    public String getLine(){
        return this.line;
    }

    /**
     * Sets the line of the statement.
     *
     * @param line the line to set
     */
    public void setLine(String line){
        this.line = line;
    }

    /**
     * Sets the control section for this statement.
     *
     * @param controlSection the control section to set
     */
    public void setControlSection(String controlSection) {
        this.controlSection = controlSection;
    }

    /**
     * Sets the block for this statement.
     *
     * @param block the block to set
     */
    public void setBlock(String block) {
        this.block = block;
    }

    /**
     * Accepts a visitor object that implements the VisitorInterface.
     * This method allows the visitor to perform some operation on the current statement.
     *
     * @param visitor the visitor object that will operate on the statement
     */
    public abstract void accept(VisitorInterface visitor);

    /**
     * Assembles the statement into its corresponding representation
     *
     * @return A string representing the assembled statement.
     * @throws InvalidAssemblyFileException if the assembly file is invalid or contains errors.
     */
    public abstract String assemble() throws InvalidAssemblyFileException;
}
