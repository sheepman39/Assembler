// Class: StatementFactory
// Extends: StatementFactoryBuilder
// This class will handle all of the statement parsing and statement creation, including setting flags and defining labels

package edu.iu.jrsalata;

import java.util.Queue;

public class StatementBuilder extends AbstractStatementBuilder {

    protected String base = "";

    // constructor
    public StatementBuilder() {
        super();
    }

    // create a statement from a string
    @Override
    public void processStatement(String statement) throws InvalidAssemblyFileException {
        // define return statement
        Statement newStatement;

        // increment lineNum by 1
        lineNum++;

        // flag for format 4
        boolean eFlag = false;

        // replace each * with the current locctr
        // since this can be used anywhere
        statement = statement.replace("*", Integer.toString(this.getLocctr(this.block).getDec()));

        // get the parts of the statement
        String[] parts = splitStatement(statement);
        String mnemonic = parts[0];

        // note that we are checking if there is a valid expression here
        // since *-n is a valid expression
        String args = evaluateExpression(parts[1]);
        String label = parts[2];

        // check if mnemonic is empty
        // if so, return null
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

            // split up each of the args
            String[] argsArray = args.split(",");

            Queue<String> queue = processor.getLines(argsArray);

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
        this.addLocctr(newStatement.getSize());
        this.addStatement(newStatement);
    }

    @Override
    protected Statement handleAsmStatement(String mnemonic, String args) throws InvalidAssemblyFileException {

        DirectiveStatement returnVal = new DirectiveStatement();
        returnVal.setDirective(mnemonic);

        // check for BASE or NOBASE directive first
        if (mnemonic.equals("BASE")) {
            this.base = args;
            return returnVal;
        } else if (mnemonic.equals("NOBASE")) {
            this.base = "";
            return returnVal;
        }

        // then use the parent for the rest
        return super.handleAsmStatement(mnemonic, args);
    }

    private Statement createStatement(String mnemonic) {

        // check to make sure that there is only one element in parts
        HexNum opcode = this.instructionTable.get(mnemonic);
        return new SingleStatement(this.getLocctr(), opcode);
    }

    private Statement createRegStatement(String mnemonic, String args) throws InvalidAssemblyFileException {

        // Statement to return
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

    private Statement createExtStatement(String mnemonic, String args, boolean eFlag) {

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
