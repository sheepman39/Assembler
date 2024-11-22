// Class: SicStatementBuilder
// Extends: AbstractStatementBuilder
// Uses sic specific instructions to assemble for SIC machines

package edu.iu.jrsalata;

import java.util.logging.Logger;

import javax.script.ScriptException;

public class SicStatementBuilder extends AbstractStatementBuilder {
    static final String sicFlag = "!USE SIC";
    Logger sicStatementLogger = Logger.getLogger(getClass().getName());

    // constructors
    public SicStatementBuilder() {
        super();
    }

    // create a statement from a string
    public void processStatement(String statement) throws InvalidAssemblyFileException, ScriptException {
        if (statement.strip().equals(sicFlag)) {
            return;
        }
        // define return statement
        Statement newStatement;

        // increment lineNum by 1
        lineNum++;

        // get the parts of the statement
        String[] parts = splitStatement(statement);
        String mnemonic = parts[0];
        String args = parts[1];

        // check if mnemonic is empty
        // if so, return null
        if (mnemonic.equals("")) {
            return;
        }

        // generate a new statement based on its format
        switch (this.formatTable.get(mnemonic)) {
            case SIC:
                newStatement = createSicStatement(mnemonic, args);
                break;
            case ASM:
                newStatement = handleAsmStatement(mnemonic, args);
                break;
            default:
                StringBuilder msg = new StringBuilder("SIC Mnemonic '");
                msg.append(mnemonic);
                msg.append("' not found");
                throw new InvalidAssemblyFileException(lineNum, msg.toString());
        }
        this.locctr = this.locctr.add(newStatement.getSize());
        this.addStatement(newStatement);
    }

    private Statement createSicStatement(String mnemonic, String args) {

        // find the opcode of the mnemonic
        HexNum opcode = this.symbolTable.get(mnemonic);

        // create and return the SICStatement
        return new SicStatement(this.locctr, opcode, args);
    }
}
