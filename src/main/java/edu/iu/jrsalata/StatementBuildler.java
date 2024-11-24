// Class: StatementFactory
// Extends: StatementFactoryBuilder
// This class will handle all of the statement parsing and statement creation, including setting flags and defining labels

package edu.iu.jrsalata;

import java.util.logging.Logger;

import javax.script.ScriptException;

public class StatementBuildler extends AbstractStatementBuilder {

    protected String base = "";
    Logger statementLogger = Logger.getLogger(getClass().getName());

    // constructor
    public StatementBuildler() {
        super();
    }

    // create a statement from a string
    public void processStatement(String statement) throws InvalidAssemblyFileException, ScriptException {
        // define return statement
        Statement newStatement;

        // increment lineNum by 1
        lineNum++;

        // flag for format 4
        boolean eFlag = false;

        // get the parts of the statement
        String[] parts = splitStatement(statement);
        String mnemonic = parts[0];
        String args = parts[1];

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
        // generate a new statement based on its format
        switch (this.formatTable.get(mnemonic)) {
            case ONE:
                newStatement = createStatement(mnemonic, args);
                break;
            case TWO:
                newStatement = createRegStatement(mnemonic, args);
                break;
            case THREE:
                newStatement = createExtStatement(mnemonic, args, eFlag);
                break;
            case SIC:
                newStatement = createExtStatement(mnemonic, args, eFlag);
                break;
            case ASM:
                newStatement = handleAsmStatement(mnemonic, args);
                break;
            default:
                StringBuilder msg = new StringBuilder("Mnemonic '");
                msg.append(mnemonic);
                msg.append("' not found");
                throw new InvalidAssemblyFileException(lineNum, msg.toString());
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

    private Statement createStatement(String mnemonic, String args) {

        // check to make sure that there is only one element in parts
        HexNum opcode = this.symbolTable.get(mnemonic);
        return new SingleStatement(this.getLocctr(), opcode);
    }

    private Statement createRegStatement(String mnemonic, String args) throws InvalidAssemblyFileException {

        // Statement to return
        RegisterStatement returnVal = new RegisterStatement();
        returnVal.setLocation(this.getLocctr());

        // find the opcode of the mnemonic
        HexNum opcode = this.symbolTable.get(mnemonic);
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
        HexNum opcode = this.symbolTable.get(mnemonic);

        // create the ExtendedStatement
        ExtendedStatement returnVal = new ExtendedStatement(this.getLocctr(), opcode, args);

        // if there is an eFlag, set it
        if (eFlag) {
            returnVal.setEFlag();
        }
        returnVal.setBase(this.base);

        return returnVal;
    }
}
