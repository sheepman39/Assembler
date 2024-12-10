package edu.iu.jrsalata;

/**
 * The BaseStatement class is an abstract class that extends the Statement class.
 * It represents an assembled statement with an opcode, location, and format.
 * 
 * This class provides constructors to initialize the opcode, location, and format,
 * as well as getter and setter methods for the location.
 */
public abstract class BaseStatement extends Statement {

    /**
     * stores the operation code of this statement
     */
    protected HexNum opcode;

    /**
     * stores the location of this statement
     */
    protected HexNum location;

    /**
     * stores the format (1,2,3,4) that this
     * statement represents
     */
    protected int format;

    /**
     * Constructs a new BaseStatement object with default values.
     * Initializes the location and opcode as new HexNum objects,
     * sets the format to 1, and initializes the size as a new HexNum
     * object with the format value.
     */
    protected BaseStatement() {
        this.location = new HexNum();
        this.opcode = new HexNum();
        this.format = 1;
        this.size = new HexNum(this.format);
    }

    /**
     * Constructs a BaseStatement with the specified location and opcode.
     *
     * @param location the memory location of the statement
     * @param opcode the operation code of the statement
     */
    protected BaseStatement(HexNum location, HexNum opcode) {
        this.location = location;
        this.opcode = opcode;
        this.format = 1;
        this.size = new HexNum(this.format);
    }

    /**
     * Retrieves the location of this statement.
     *
     * @return the location as a HexNum object.
     */
    public HexNum getLocation() {
        return this.location;
    }

    /**
     * Sets the location of this BaseStatement.
     *
     * @param location the HexNum representing the new location
     */
    public void setLocation(HexNum location) {
        this.location = location;
    }

}
