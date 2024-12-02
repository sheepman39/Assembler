// Class: ExtendedStatement
// Extends: Statement
// Handles statements in Format 3 and 4, which has significantly more complexity compared to F1 and F2
package edu.iu.jrsalata;

public class ExtendedStatement extends BaseStatement {

    protected String args;
    protected String assembled = "";
    protected String base = "";
    protected String modification = "";
    protected boolean hasExternalSymbol = false;
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
        this.base = "";
    }

    public ExtendedStatement(HexNum location, HexNum opcode, String args) {
        super(location, opcode);
        this.args = args;
        this.format = 3;
        this.size.set(this.format);
        this.assembled = "";
        this.base = "";
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

    public void setBase(String base) {
        this.base = base;
    }

    public void setExternalSymbol() {
        this.hasExternalSymbol = true;
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

    public String getModification() {
        return this.modification;
    }

    @Override
    public void accept(VisitorInterface visitor) {
        visitor.visit(this);
    }

    // assemble
    @Override
    public String assemble() throws InvalidAssemblyFileException {
        if (!this.assembled.equals("")) {
            return this.assembled;
        }

        String processedArgs = this.args;

        // if the args is empty, assume it is 000
        if (processedArgs.length() == 0) {
            processedArgs = "000";
        }

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
        switch (processedArgs.charAt(0)) {
            case '#' -> {
                this.setIFlag();
                processedArgs = processedArgs.substring(1);
            }
            case '@' -> {
                this.setNFlag();
                processedArgs = processedArgs.substring(1);
            }
            default -> {
                this.setIFlag();
                this.setNFlag();
            }
        }

        HexNum targetAddress;

        // look up if args is in the symbolTable
        if (SymTable.containsSymbol(processedArgs)) {
            targetAddress = SymTable.getSymbol(processedArgs);
            targetAddress = this.calculateDisp(targetAddress);
        } else {
            // if not, assume it is a hex number
            targetAddress = new HexNum(processedArgs, NumSystem.DEC);
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

        this.assembled = first.toString(2) + third.toString(1) + targetAddress.toString(argSize);

        // check if we need to create a modification record
        // we need to create a modification record if it is using direct addressing
        // meaning that we are not using base or pc relative addressing
        if (!this.bFlag && !this.pFlag && this.iFlag && this.nFlag && !processedArgs.equals("000")) {
            StringBuilder modificationBuilder = new StringBuilder();
            modificationBuilder.append("M");
            modificationBuilder.append(this.location.add(1).toString(6));
            modificationBuilder.append("0");
            modificationBuilder.append(argSize);

            // if the args is defined in an external symbol, we need to specify that here
            if(this.hasExternalSymbol) {
                modificationBuilder.append("+");
                modificationBuilder.append(processedArgs);
            }
            this.modification = modificationBuilder.toString();
        }

        return this.assembled;
    }

    // this will be used to set the displacement
    private HexNum calculateDisp(HexNum targetAddress) throws InvalidAssemblyFileException {
        // now we calculate disp and if it is base or pc relative
        // if we are in F4, then keep it the same
        // otherwise assume pc relative first then base relative
        HexNum disp = new HexNum();
        if (this.eFlag) {
            disp = targetAddress;
        } else {

            // try to do pc relative first
            // it is easier to convert each value to decimal and compare
            int pc = this.location.add(3).getDec();
            int pcRelative = targetAddress.getDec() - pc;
            if (pcRelative >= -2048 && pcRelative <= 2047) {
                this.setPFlag();
                disp = new HexNum(pcRelative);
            } else {
                // if pc relative is not possible, try base relative
                int baseInt = SymTable.getSymbol(this.base).getDec();
                int baseRelative = targetAddress.getDec() - baseInt;
                if (this.base.length() > 0 && baseRelative >= 0 && baseRelative <= 4095) {
                    this.setBFlag();
                    disp = new HexNum(baseRelative);
                } else if (this.base.length() == 0) {
                    throw new InvalidAssemblyFileException(-1, "MISSING BASE REGISTER");
                }
            }
        }
        return disp;
    }
}