package edu.iu.jrsalata;

/**
 * The SicStatement class represents a SIC statement
 * in an assembler. It extends the BaseStatement class and provides functionality for
 * handling SIC-specific instructions and addressing modes.
 */
public class SicStatement extends BaseStatement {

    /**
     * args is the arguments of the given statement
     * default value is "000"
     */
    protected String args;

    /**
     * xFlag represents if an index is being used
     * represented by ",X" at the end of args
     */
    protected boolean xFlag = false;

    /**
     * Constructs a new SicStatement object with default values.
     * Initializes the arguments to "000", sets the format to 3,
     * and updates the size based on the format.
     */
    public SicStatement() {
        super();
        this.args = "000";
        this.format = 3;
        this.size.set(this.format);
    }

    /**
     * Constructs a SicStatement object with the specified location, opcode, and arguments.
     *
     * @param location the memory location of the statement as a HexNum object
     * @param opcode the operation code of the statement as a HexNum object
     * @param args the arguments for the statement as a String; if empty, defaults to "000"
     */
    public SicStatement(HexNum location, HexNum opcode, String args) {
        super(location, opcode);
        if (args.isEmpty()) {
            this.args = "000";
        } else {
            this.args = args;
        }
        this.format = 3;
        this.size.set(this.format);
    }

    /**
     * Sets the xFlag to true.
     */
    private void setXFlag() {
        this.xFlag = true;
    }

    /**
     * Sets the arguments for the SIC statement.
     * If the provided arguments string are empty, the value of args will not change
     *
     * @param args the arguments to be set
     */
    public void setArgs(String args) {
        if (!args.isEmpty()) {
            this.args = args;
        }
    }

    /**
     * Assembles the SIC statement into its machine code representation.
     * 
     * This method processes the arguments to determine the addressing mode and 
     * checks for the X flag. It then looks up the argument value in the symbol table or 
     * interprets it as a hexadecimal number. If the X flag is set, it adjusts 
     * the argument value to reflect indexed addressing.
     * 
     * @return A string representing the machine code of the SIC statement.
     */
    @Override
    public String assemble() {
        // check for the X flag
        // if the X flag exists, remove it from the args
        if (this.args.toUpperCase().replace(" ", "").contains(",X")) {
            this.setXFlag();
            this.args = this.args.toUpperCase().replace(" ", "").replace(",X", "");
        }

        // check the addressing mode of the args
        if (this.args.isEmpty()) {
            return this.opcode.toString(2) + "0000";
        } else if (this.args.charAt(0) == '#') {
            this.args = this.args.substring(1);
        } else if (this.args.charAt(0) == '@') {
            this.args = this.args.substring(1);
        }

        // If an argument is given, find it in the symbol table
        HexNum argValue;
        if (SymTable.containsSymbol(this.args, this.controlSection)) {
            argValue = SymTable.getSymbol(this.args, this.controlSection);
        } else {
            argValue = new HexNum(this.args, NumSystem.HEX);
        }

        // addresses are 15 bits with the 16th representing X
        // so we need to add a 1 to the front of the string
        if (this.xFlag) {
            argValue = argValue.add(new HexNum("8000", NumSystem.HEX));
        }

        return this.opcode.toString(2) + argValue.toString(4);
    }

    /**
     * Accepts a visitor object that implements the VisitorInterface.
     * This method allows the visitor to perform some operation on this SicStatement instance.
     *
     * @param visitor the visitor object that will visit this SicStatement instance
     */
    @Override
    public void accept(VisitorInterface visitor) {
        visitor.visit(this);
    }
}