package edu.iu.jrsalata;

/**
 * The RegisterStatement class represents a statement that involves register operations
 * in an assembler. It extends the BaseStatement class and includes two registers (reg1 and reg2).
 * The format of the statement is set to 2, and the size is adjusted accordingly.
 */
public class RegisterStatement extends BaseStatement {

    /**
     * reg1 and reg2 represent the first and second registers respectively
     */
    HexNum reg1, reg2;

    /**
     * Constructs a new RegisterStatement with default values.
     * Initializes the format to 2, sets the size based on the format,
     * and initializes both reg1 and reg2 to new HexNum instances with a value of 0.
     */
    public RegisterStatement() {
        super();
        this.format = 2;
        this.size.set(this.format);
        this.reg1 = new HexNum(0);
        this.reg2 = new HexNum(0);
    }

    /**
     * Constructs a RegisterStatement with the specified location, opcode, and register 1.
     *
     * @param location the memory location of the statement
     * @param opcode the operation code of the statement
     * @param reg1 the first register involved in the statement
     */
    public RegisterStatement(HexNum location, HexNum opcode, HexNum reg1) {
        super(location, opcode);
        this.format = 2;
        this.size.set(this.format);
        this.reg1 = reg1;
        this.reg2 = new HexNum(0);
    }

    /**
     * Constructs a RegisterStatement with the specified location, opcode, and registers.
     *
     * @param location the memory location of the statement
     * @param opcode the operation code of the statement
     * @param reg1 the first register involved in the statement
     * @param reg2 the second register involved in the statement
     */
    public RegisterStatement(HexNum location, HexNum opcode, HexNum reg1, HexNum reg2) {
        super(location, opcode);
        this.format = 2;
        this.size.set(this.format);
        this.reg1 = reg1;
        this.reg2 = reg2;
    }

    /**
     * Sets the value of the first register.
     * 
     * @param reg1 the HexNum object to set as the first register. If reg1 is null, the current value of reg1 will not be changed.
     */
    public void setReg1(HexNum reg1) {
        if (reg1 != null) {
            this.reg1 = reg1;
        }
    }

    /**
     * Sets the value of the second register.
     * 
     * @param reg2 the HexNum object to set as the second register. If reg2 is null, the current value of reg2 will not be changed.
     */
    public void setReg2(HexNum reg2) {
        if (reg2 != null) {
            this.reg2 = reg2;
        }
    }

    /**
     * Sets the opcode for this RegisterStatement.
     *
     * @param opcode the HexNum object representing the opcode to be set
     */
    public void setOpcode(HexNum opcode) {
        this.opcode = opcode;
    }

    /**
     * Assembles the opcode and register values into a hex string representation.
     * 
     * @return A hex string representation of the opcode and register values.
     */
    @Override
    public String assemble() {
        return this.opcode.toString(2) + this.reg1.toString(1) + this.reg2.toString(1);
    }

    /**
     * Accepts a visitor object and allows it to visit this RegisterStatement instance.
     *
     * @param visitor the visitor object that will visit this instance
     */
    @Override
    public void accept(VisitorInterface visitor) {
        visitor.visit(this);
    }
}
