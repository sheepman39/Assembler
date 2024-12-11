package edu.iu.jrsalata;

import java.util.Queue;

/**
 * The StatementBuilder class is responsible for processing SIC/XE assembly statements
 * and generating corresponding Statement objects based on the mnemonic and arguments.
 * It extends the AbstractStatementBuilder class and provides implementations for
 * handling different formats of statements, including macros and assembler directives.
 */
public class StatementBuilder extends AbstractStatementBuilder {

    /**
     * The base address for the BASE directive, when it is set
     */
    protected String base = "";

    /**
     * Constructor for StatementBuilder.
     * Initializes the StatementBuilder by calling the superclass constructor.
     */
    public StatementBuilder() {
        super();
    }

    /**
     * Processes an assembly statement and generates a corresponding Statement object.
     * 
     * @param statement The assembly statement to process.
     * @throws InvalidAssemblyFileException If the statement is invalid or contains errors.
     */
    @Override
    public void processStatement(String statement) throws InvalidAssemblyFileException {
        Statement newStatement;

        // to keep an accurate lineNum count, increment before any processing is done
        lineNum++;

        // flag for format 4
        boolean eFlag = false;

        // replace each * with the current locctr
        // since this can be used anywhere
        statement = statement.replace("*", Integer.toString(this.getLocctr(this.block).getDec()));

        // note that we are checking if there is a valid expression for args
        // since *-n is a valid expression
        String[] parts = splitStatement(statement);
        String mnemonic = parts[0];
        String args = evaluateExpression(parts[1]);
        String label = parts[2];

        // check if mnemonic is empty
        // if so, return null since there is nothing to do
        if (mnemonic.equals("")) {
            return;
        }

        // since some mnemonics may contain '+' at the beginning, we want to remove it
        // for comparisons sake
        if (mnemonic.charAt(0) == '+') {
            eFlag = true;
            mnemonic = mnemonic.substring(1);
        }

        // check if it is a macro before looking for a mnemonic
        if (SymTable.getMacroKeys().contains(mnemonic)) {

            // get the MP
            MacroProcessorInterface processor = SymTable.getMacro(mnemonic);

            // set the processor's label to the current label
            processor.setLabel(label);

            // find each of the given arguments for the macro
            String[] argsArray = args.split(",");
            Queue<String> queue = processor.getLines(argsArray);

            // recursively call this method so it is as if the statements are part of the file
            while (!queue.isEmpty()) {
                this.processStatement(queue.poll());
            }

            return;
        }

        // generate a new statement based on its format
        switch (this.formatTable.get(mnemonic)) {
            case ONE -> newStatement = createStatement(mnemonic);
            case TWO -> newStatement = createRegStatement(mnemonic, args);
            case THREE -> newStatement = createExtStatement(mnemonic, args, eFlag);
            case SIC -> newStatement = createExtStatement(mnemonic, args, eFlag);
            case ASM -> newStatement = handleAsmStatement(mnemonic, args);
            default -> {

                StringBuilder msg = new StringBuilder("Mnemonic '");
                msg.append(mnemonic);
                msg.append("' not found");
                throw new InvalidAssemblyFileException(lineNum, msg.toString());

            }
        }

        // add the size of the statement to the location counter
        // and use the addStatement method to handle the other logic surrounding
        // the statement
        this.line = statement;
        this.addLocctr(newStatement.getSize());
        this.addStatement(newStatement);
    }

    /**
     * Handles assembler directives and generates a corresponding DirectiveStatement object.
     * 
     * @param mnemonic The mnemonic of the assembler directive.
     * @param args The arguments for the assembler directive.
     * @return The generated Statement object.
     * @throws InvalidAssemblyFileException If the directive is invalid or contains errors.
     */
    @Override
    protected DirectiveStatement handleAsmStatement(String mnemonic, String args) throws InvalidAssemblyFileException {
        DirectiveStatement returnVal = new DirectiveStatement();
        returnVal.setDirective(mnemonic);

        // check for BASE or NOBASE directive first
        // if it is base, set the args to the base
        // which can only be used for the SIC/XE machine
        if (mnemonic.equals("BASE")) {
            this.base = args;
            return returnVal;
        } else if (mnemonic.equals("NOBASE")) {
            this.base = "";
            return returnVal;
        }

        // then use the parent for the rest of the logic
        return super.handleAsmStatement(mnemonic, args);
    }

    /**
     * Creates a Statement object for format 1 instructions.
     * 
     * @param mnemonic The mnemonic of the instruction.
     * @return The generated Statement object.
     */
    private Statement createStatement(String mnemonic) {
        HexNum opcode = this.instructionTable.get(mnemonic);
        return new SingleStatement(this.getLocctr(), opcode);
    }

    /**
     * Creates a RegisterStatement object for format 2 instructions.
     * 
     * @param mnemonic The mnemonic of the instruction.
     * @param args The arguments for the instruction.
     * @return The generated RegisterStatement object.
     * @throws InvalidAssemblyFileException If the instruction is invalid or contains errors.
     */
    private RegisterStatement createRegStatement(String mnemonic, String args) throws InvalidAssemblyFileException {
        RegisterStatement returnVal = new RegisterStatement();
        returnVal.setLocation(this.getLocctr());

        // find the opcode of the mnemonic
        HexNum opcode = this.instructionTable.get(mnemonic);
        returnVal.setOpcode(opcode);

        // find both of the registers in parts[1]
        String[] registers = args.split(",");
        if (registers.length > 2 || registers.length <= 0) {
            throw new InvalidAssemblyFileException(lineNum, "Error: Invalid number of registers for format 2");
        }

        // find each of the registers in the registerTable
        HexNum reg1 = this.registerTable.get(registers[0]);

        // setters ensure a null value can not be set
        returnVal.setReg1(reg1);

        // if there is a second register, set it
        if (registers.length == 2) {
            HexNum reg2 = this.registerTable.get(registers[1]);
            returnVal.setReg2(reg2);
        }

        return returnVal;
    }

    /**
     * Creates an ExtendedStatement object for format 3 and 4 instructions.
     * 
     * @param mnemonic The mnemonic of the instruction.
     * @param args The arguments for the instruction.
     * @param eFlag A flag indicating if the instruction is format 4.
     * @return The generated ExtendedStatement object.
     */
    private ExtendedStatement createExtStatement(String mnemonic, String args, boolean eFlag) {
        // find the opcode of the mnemonic
        HexNum opcode = this.instructionTable.get(mnemonic);

        // create the ExtendedStatement
        ExtendedStatement returnVal = new ExtendedStatement(this.getLocctr(), opcode, args);

        // if there is an eFlag, set it
        if (eFlag) {
            returnVal.setEFlag();
        }

        // check if the args is in an external reference
        if (this.externalReferences.contains(Utility.lengthCheck(args))) {
            returnVal.setExternalSymbol();
        }

        returnVal.setBase(this.base);

        return returnVal;
    }
}
