// Class: ExtendedStatement
// Extends: Statement
// Handles statements in Format 3 and 4, which has significantly more complexity compared to F1 and F2
package edu.iu.jrsalata;

public class ExtendedStatement extends BaseStatement {

    String args;
    boolean nFlag = false;
    boolean iFlag = false;
    boolean xFlag = false;
    boolean bFlag = false;
    boolean pFlag = false;
    boolean eFlag = false;
    boolean sicFlag = false;

    // constructors
    public ExtendedStatement() {
        super();
        this.args = "000";
        this.format = 3;
        this.size.set(this.format);
    }

    public ExtendedStatement(HexNum location, HexNum opcode, String args) {
        super(location, opcode);
        this.args = args;
        this.format = 3;
        this.size.set(this.format);
    }

    // flag managers
    private void setNFlag() {
        this.nFlag = true;
    }

    private void setIFlag() {
        this.iFlag = true;
    }

    private void setXFlag() {
        this.xFlag = true;
    }

    public void setBFlag() {
        this.bFlag = true;
    }

    public void setPFlag() {
        this.pFlag = true;
    }

    public void setEFlag() {
        this.eFlag = true;
    }

    public void setSICFlag() {
        this.sicFlag = true;
    }

    // this will be used by the factory to clean up the args. It will also handle
    // setting the flags
    public void setArgs(String args) {
        this.args = args;
    }

    // since the size can be 3 or 4, we will add 1 to the size if the e flag is set
    @Override
    public HexNum getSize() {
        return this.eFlag ? this.size.add(1) : this.size;
    }

    // assemble
    @Override
    public String assemble() {

        // check for the X flag
        // if the X flag exists, remove it from the args
        if (this.args.toUpperCase().replace(" ", "").contains(",X")) {
            this.setXFlag();
            this.args = this.args.toUpperCase().replace(" ", "").replace(",X", "");
        }

        // check the addressing mode of the args
        // '#' means immediate addressing
        // '@' means indirect addressing
        // if neither, assume direct addressing
        if (this.args.length() == 0) {
            return this.opcode.toString(2) + "0000";
        } else if (this.args.charAt(0) == '#') {
            this.setIFlag();
            this.args = this.args.substring(1);
        } else if (this.args.charAt(0) == '@') {
            this.setNFlag();
            this.args = this.args.substring(1);
        } else {
            this.setIFlag();
            this.setNFlag();
        }

        if (this.sicFlag) {
            return assembleSic();
        }

        // Get the values of each individual flag
        int n = this.nFlag ? 2 : 0;
        int i = this.iFlag ? 1 : 0;
        int x = this.xFlag ? 8 : 0;
        int b = this.bFlag ? 4 : 0;
        int p = this.pFlag ? 2 : 0;
        int e = this.eFlag ? 1 : 0;

        // sicne n and i are part of the opcode bit, we will add them here
        HexNum first = this.opcode.add(n + i);

        // set the 3rd hex number to x, b, p, e
        HexNum third = new HexNum(x + b + p + e);
        HexNum argValue;
        // look up if args is in the symbolTable
        if (SymTable.containsSymbol(this.args)) {
            argValue = SymTable.getSymbol(this.args);
            return first.toString(2) + third.toString(1) + argValue.toString(this.size.getDec());
        }

        // if not, assume it is a hex number
        argValue = new HexNum(this.args, NumSystem.HEX);

        String returnVal = first.toString(2) + third.toString(1) + argValue.toString(this.size.getDec());
        return returnVal;
    }

    private String assembleSic() {

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