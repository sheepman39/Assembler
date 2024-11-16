// Class: ExtendedStatement
// Extends: Statement
// Handles statements in Format 3 and 4, which has significantly more complexity compared to F1 and F2
package edu.iu.jrsalata;

public class ExtendedStatement extends BaseStatement {

    protected String args;
    protected String assembled = "";
    protected boolean nFlag = false;
    protected boolean iFlag = false;
    protected boolean xFlag = false;
    protected boolean bFlag = false;
    protected boolean pFlag = false;
    protected boolean eFlag = false;

    // constructors
    public ExtendedStatement() {
        super();
        this.args = "000";
        this.format = 3;
        this.size.set(this.format);
        this.assembled = "";
    }

    public ExtendedStatement(HexNum location, HexNum opcode, String args) {
        super(location, opcode);
        this.args = args;
        this.format = 3;
        this.size.set(this.format);
        this.assembled = "";
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
        if(!this.assembled.equals("")) {
            return this.assembled;
        }

        String processedArgs = this.args;
        // check for the X flag
        // if the X flag exists, remove it from the args
        if (processedArgs.toUpperCase().replace(" ", "").contains(",X")) {
            this.setXFlag();
            processedArgs = processedArgs.toUpperCase().replace(" ", "").replace(",X", "");
        }

        // check the addressing mode of the args
        // '#' means immediate addressing
        // '@' means indirect addressing
        // if neither, assume direct addressing
        if (processedArgs.length() == 0) {
            this.assembled = this.opcode.toString(2) + "0000";
            return this.assembled;
        } else if (processedArgs.charAt(0) == '#') {
            this.setIFlag();
            processedArgs = processedArgs.substring(1);

        } else if (processedArgs.charAt(0) == '@') {
            this.setNFlag();
            processedArgs = processedArgs.substring(1);

        } else {
            this.setIFlag();
            this.setNFlag();
        }

        HexNum argValue;
        // look up if args is in the symbolTable
        if (SymTable.containsSymbol(processedArgs)) {
            argValue = SymTable.getSymbol(processedArgs);

            // if we are PC relative, we need to subtract the PC from the value
            if (this.pFlag) {
                HexNum pc = this.location.add(this.size);
                argValue = argValue.subtract(pc);
            }
        } else {
            // if not, assume it is a hex number
            argValue = new HexNum(processedArgs, NumSystem.DEC);
            this.pFlag = false;

        }

        // Get the values of each individual flag
        int n = this.nFlag ? 2 : 0;
        int i = this.iFlag ? 1 : 0;
        int x = this.xFlag ? 8 : 0;
        int b = this.bFlag ? 4 : 0;
        int p = this.pFlag ? 2 : 0;
        int e = this.eFlag ? 1 : 0;

        // if the e flag is set, we need to ensure the args is 5 hex numbers
        int argSize = this.eFlag ? 5 : 3;

        // sicne n and i are part of the opcode bit, we will add them here
        HexNum first = this.opcode.add(n + i);

        // set the 3rd hex number to x, b, p, e
        HexNum third = new HexNum(x + b + p + e);

        this.assembled = first.toString(2) + third.toString(1) + argValue.toString(argSize);
        return this.assembled;
    }
}