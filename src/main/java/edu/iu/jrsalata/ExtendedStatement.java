package edu.iu.jrsalata;

/**
 * The ExtendedStatement class represents an extended statement in an assembler.
 * It extends the BaseStatement class and provides additional functionality for
 * handling extended format instructions, flags, and assembling the statement.
 * 
 * This class includes methods for setting various flags, managing arguments,
 * calculating displacement, and assembling the statement into its final form.
 * It also supports handling external symbols and creating modification records
 * when necessary.
 * 
 * <p>Flags supported by this class include:</p>
 * <ul>
 *   <li>nFlag: Indicates indirect addressing mode.</li>
 *   <li>iFlag: Indicates immediate addressing mode.</li>
 *   <li>xFlag: Indicates indexed addressing mode.</li>
 *   <li>bFlag: Indicates base relative addressing mode.</li>
 *   <li>pFlag: Indicates program counter relative addressing mode.</li>
 *   <li>eFlag: Indicates extended format (format 4) instruction.</li>
 * </ul>
 * 
 * 
 * Note: This class relies on the SymTable class for symbol table lookups and the HexNum class for handling hexadecimal numbers.
 */
public class ExtendedStatement extends BaseStatement {

    /**
     * stores each of the originally provided args
     */
    protected String args;

    /**
     * contains the name of the base used in displacement calculations
     */
    protected String base = "";

    /**
     * Stores the needed modification record, if any
     */
    protected String modification = "";

    /**
     * true if an external symbol is referenced, false if otherwise
     */
    protected boolean hasExternalSymbol = false;

    /**
     * stores the n flag
     */
    protected boolean nFlag = false;

    /**
     * stores the i flag
     */
    protected boolean iFlag = false;

    /**
     * stores the x flag
     */
    protected boolean xFlag = false;

    /**
     * stores the b flag
     */
    protected boolean bFlag = false;

    /**
     * stores the p flag
     */
    protected boolean pFlag = false;

    /**
     * stores the e flag
     */
    protected boolean eFlag = false;

    /**
     * Constructs an ExtendedStatement object with default values.
     * Initializes the arguments to "000", format to 3, and base to an empty string.
     * Sets the size based on the format.
     */
    public ExtendedStatement() {
        super();
        this.args = "000";
        this.format = 3;
        this.size.set(this.format);
        this.base = "";
    }

    /**
     * Constructs an ExtendedStatement with the specified location, opcode, and arguments.
     *
     * @param location the memory location of the statement
     * @param opcode the operation code of the statement
     * @param args the arguments for the statement
     */
    public ExtendedStatement(HexNum location, HexNum opcode, String args) {
        super(location, opcode);
        this.args = args;
        this.format = 3;
        this.size.set(this.format);
        this.base = "";
    }


    /**
     * Sets the N flag to true.
     */
    private void setNFlag() {
        this.nFlag = true;
    }

    /**
     * Sets the iFlag to true.
     */
    private void setIFlag() {
        this.iFlag = true;
    }

    /**
     * Sets the xFlag to true.
     */
    private void setXFlag() {
        this.xFlag = true;
    }

    /**
     * Sets the B flag to true.
     */
    public void setBFlag() {
        this.bFlag = true;
    }

    /**
     * Sets the pFlag to true.
     */
    public void setPFlag() {
        this.pFlag = true;
    }

    /**
     * Sets the eFlag to true.
     */
    public void setEFlag() {
        this.eFlag = true;
    }

    
    /** 
     * Sets the base of the statement
     * @param base name of the base to be set
     */
    public void setBase(String base) {
        this.base = base;
    }

    /**
     * Marks this statement as having an external symbol.
     */
    public void setExternalSymbol() {
        this.hasExternalSymbol = true;
    }

    /**
     * Sets the string of args for this statement
     * @param args string of arguments the extended statement needs
     */
    public void setArgs(String args) {
        this.args = args;
    }

    /**
     * Returns the size of the statement. If the eFlag is set, the size is incremented by 1.
     *
     * @return the size of the statement as a HexNum object. If eFlag is true, the size is incremented by 1.
     */
    @Override
    public HexNum getSize() {
        return this.eFlag ? this.size.add(1) : this.size;
    }

    /**
     * Retrieves the modification string.
     *
     * @return the modification string.
     */
    public String getModification() {
        return this.modification;
    }

    /**
     * Accepts a visitor object and allows it to visit this instance of ExtendedStatement.
     * This method is part of the Visitor design pattern.
     *
     * @param visitor the visitor object that will visit this instance
     */
    @Override
    public void accept(VisitorInterface visitor) {
        visitor.visit(this);
    }

    /**
     * Assembles the statement into its object code representation.
     *
     * @return The assembled object code as a string.
     * @throws InvalidAssemblyFileException If there is an error in the assembly process.
     */
    @Override
    public String assemble() throws InvalidAssemblyFileException {
        
        /**
         * creates a copy of the args to handle easier
         */
        String processedArgs = this.args;

        /**
         * stores the entire assembled object code
         */
        String assembled;

        /**
         * stores the calculated target address
         */
        HexNum targetAddress;

        // if the args is empty, assume it is 000
        if (processedArgs.isEmpty()) {
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

        // process the arguments and see if there is another value
        if (SymTable.containsSymbol(processedArgs, this.controlSection)) {
            targetAddress = SymTable.getSymbol(processedArgs, this.controlSection);
            targetAddress = this.calculateDisp(targetAddress);
        } else if (!this.hasExternalSymbol) {
            targetAddress = new HexNum(processedArgs, NumSystem.DEC);
        } else {
            targetAddress = new HexNum("0", NumSystem.DEC);
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

        // since n and i are part of the opcode bit, we will add them here
        HexNum first = this.opcode.add(n + i);

        // set the 3rd hex number to x, b, p, e
        HexNum third = new HexNum(x + b + p + e);
        assembled = first.toString(2) + third.toString(1) + targetAddress.toString(argSize);

        // handle any necessary modifications, if any exist
        handleModificationRecords(processedArgs, new HexNum(argSize));

        return assembled;
    }

    /**
     * Calculates the displacement (disp) for the given target address.
     * The displacement can be either PC-relative or base-relative, depending on the addressing mode.
     * 
     * @param targetAddress The target address for which the displacement is to be calculated.
     * @return The calculated displacement as a HexNum object.
     * @throws InvalidAssemblyFileException If the base register is missing when base-relative addressing is required.
     */
    private HexNum calculateDisp(HexNum targetAddress) throws InvalidAssemblyFileException {

        // if we are in F4, then keep the target address the same
        // otherwise assume pc relative first, then base relative
        HexNum disp = new HexNum();
        if (this.eFlag) {
            disp = targetAddress;
        } else {

            // try to do pc relative first
            // it is easier to convert each value to decimal and compare
            int pc = this.location.add(this.getSize()).getDec();
            int pcRelative = targetAddress.getDec() - pc;
            if (pcRelative >= -2048 && pcRelative <= 2047) {
                this.setPFlag();
                disp = new HexNum(pcRelative);
            } else {
                // if pc relative is not possible, try base relative
                int baseInt = SymTable.getSymbol(this.base, this.controlSection).getDec();
                int baseRelative = targetAddress.getDec() - baseInt;
                if (!this.base.isEmpty() && baseRelative >= 0 && baseRelative <= 4095) {
                    this.setBFlag();
                    disp = new HexNum(baseRelative);
                } else if (this.base.isEmpty()) {
                    throw new InvalidAssemblyFileException(-1, "MISSING BASE REGISTER");
                }
            }
        }
        return disp;
    }

    /**
     * Handles the creation of modification records for the assembler.
     * 
     * A modification record is created if the instruction uses direct addressing,
     * which means it is not using base or PC-relative addressing. The modification
     * record is formatted according to the following rules:
     * - 'M' (column 1)
     * - Starting address of the field to be modified, relative to beginning of control section (columns 2-7)
     * - Length of field to be modified in half-bytes (columns 8-9)
     * - If the argument is defined in an external symbol, a '+' followed by the
     *   processed argument is appended. (column 10)
     * - If the argument is defined in an external symbol, include the name of said symbol (columns 11-16)
     * 
     * @param processedArgs The processed arguments for the instruction.
     * @param argSize The size of the argument.
     */
    private void handleModificationRecords(String processedArgs, HexNum argSize){
        // check if we need to create a modification record
        // we need to create a modification record if it is using direct addressing
        // meaning that we are not using base or pc relative addressing
        if (!this.bFlag && !this.pFlag && this.iFlag && this.nFlag && !processedArgs.equals("000")) {
            StringBuilder modificationBuilder = new StringBuilder();
            modificationBuilder.append("M");
            modificationBuilder.append(this.location.add(1).toString(6));
            modificationBuilder.append(argSize.toString(2));

            // if the args is defined in an external symbol, we need to specify that here
            if (this.hasExternalSymbol) {
                modificationBuilder.append("+");
                modificationBuilder.append(processedArgs);
            }
            this.modification = modificationBuilder.toString();
        }
    }
}