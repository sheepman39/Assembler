package edu.iu.jrsalata;

/**
 * The SingleStatement class represents a single assembly statement.
 * It extends the BaseStatement class and provides specific implementations
 * for assembling the statement and accepting a visitor.
 * 
 * @see BaseStatement
 * @see HexNum
 * @see VisitorInterface
 */
public class SingleStatement extends BaseStatement {

    /**
     * Default constructor for the SingleStatement class.
     * Calls the BaseStatement constructor.
     */
    public SingleStatement() {
        super();
    }

    /**
     * Constructs a SingleStatement object with the specified location and opcode.
     *
     * @param location the memory location of the statement as a HexNum
     * @param opcode the operation code of the statement as a HexNum
     */
    public SingleStatement(HexNum location, HexNum opcode) {
        super(location, opcode);
        this.size = new HexNum(this.format);
    }

    /**
     * Assembles the opcode into its hex string representation.
     *
     * @return A hex string representation of the opcode.
     */
    @Override
    public String assemble() {
        return this.opcode.toString(2);
    }

    /**
     * Accepts a visitor object and allows it to visit this instance of SingleStatement.
     *
     * @param visitor the visitor object that will visit this instance
     */
    @Override
    public void accept(VisitorInterface visitor) {
        visitor.visit(this);
    }
}