// Class: ExtendedStatement
// Extends: Statement
// Handles statements in Format 3 and 4, which has significantly more complexity compared to F1 and F2
package edu.iu.jrsalata;
public class ExtendedStatement extends Statement {
    String args;
    boolean nFlag = false;
    boolean iFlag = false;
    boolean xFlag = false;
    boolean bFlag = false;
    boolean pFlag = false;
    boolean eFlag = false;

    // constructors
    public ExtendedStatement() {
        super();
        this.args = "";
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
    public void setNFlag() {
        this.nFlag = true;
    }

    public void setIFlag() {
        this.iFlag = true;
    }

    public void setXFlag() {
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
        if(this.args.toUpperCase().contains(",X")){
            this.setXFlag();
            this.args = this.args.replace(",X", "");
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

        // look up if args is in the symbolTable
        if(Main.symbolTable.containsKey(this.args)) {
            HexNum argValue = Main.symbolTable.get(this.args);
            return first.toString() + third.toString() + argValue.toString();
        }
        
        String returnVal = first.toString() + third.toString() + this.args;
        return returnVal;
    }

}