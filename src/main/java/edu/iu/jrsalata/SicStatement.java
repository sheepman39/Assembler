// Class: SicStatement
// Extends: BaseStatement
// Purpose: Represents a SIC statement

package edu.iu.jrsalata;

public class SicStatement extends BaseStatement {

    protected String args;
    protected boolean xFlag = false;

    // constructors
    public SicStatement() {
        super();
        this.args = "000";
        this.format = 3;
        this.size.set(this.format);
    }

    public SicStatement(HexNum location, HexNum opcode, String args) {
        super(location, opcode);
        if (args.equals("")) {
            this.args = "000";
        } else {
            this.args = args;
        }
        this.format = 3;
        this.size.set(this.format);
    }

    // flag managers
    private void setXFlag() {
        this.xFlag = true;
    }

    public void setArgs(String args) {
        if (!args.equals("")) {
            this.args = args;
        }
    }

    @Override
    public HexNum getSize() {
        return this.size;
    }

    @Override
    public String assemble() {
        // check for the X flag
        // if the X flag exists, remove it from the args
        if (this.args.toUpperCase().replace(" ", "").contains(",X")) {
            this.setXFlag();
            this.args = this.args.toUpperCase().replace(" ", "").replace(",X", "");
        }

        // check the addressing mode of the args
        // We are just going to slice the args apart
        if (this.args.length() == 0) {
            return this.opcode.toString(2) + "0000";
        } else if (this.args.charAt(0) == '#') {
            this.args = this.args.substring(1);
        } else if (this.args.charAt(0) == '@') {
            this.args = this.args.substring(1);
        }

        // If an argument is given, find it in the symbol table
        HexNum argValue;
        if (SymTable.containsSymbol(this.args)) {
            argValue = SymTable.getSymbol(this.args);
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
}