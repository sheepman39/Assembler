// Class: SicStatementBuilder
// Extends: AbstractStatementBuilder
// Uses sic specific instructions to assemble for SIC machines

package edu.iu.jrsalata;

import javax.script.ScriptException;

public class SicStatementBuilder extends AbstractStatementBuilder {
    static final String SIC_FLAG = "!USE SIC";

    // constructors
    public SicStatementBuilder() {
        super();
    }

    // create a statement from a string
    @Override
    public void processStatement(String statement) throws InvalidAssemblyFileException, ScriptException {
        if (statement.strip().equals(SIC_FLAG)) {
            return;
        }
        // define return statement
        Statement newStatement;

        // increment lineNum by 1
        lineNum++;

                // replace each * with the current locctr
        // since this can be used anywhere
        statement = statement.replace("*", Integer.toString(this.getLocctr(this.block).getDec()));

        // get the parts of the statement
        String[] parts = splitStatement(statement);
        String mnemonic = parts[0];
        String args = evaluateExpression(parts[1]);

        // check if mnemonic is empty
        // if so, return null
        if (mnemonic.equals("")) {
            return;
        }

        // generate a new statement based on its format
        switch (this.formatTable.get(mnemonic)) {
            case SIC -> newStatement = createSicStatement(mnemonic, args);
            case ASM -> newStatement = handleAsmStatement(mnemonic, args);
            default -> {
                StringBuilder msg = new StringBuilder("SIC Mnemonic '");
                msg.append(mnemonic);
                msg.append("' not found");
                throw new InvalidAssemblyFileException(lineNum, msg.toString());
            }
        }
        this.addLocctr(newStatement.getSize());
        this.addStatement(newStatement);
    }

    private Statement createSicStatement(String mnemonic, String args) {

        // find the opcode of the mnemonic
        HexNum opcode = this.instructionTable.get(mnemonic);

        // create and return the SICStatement
        return new SicStatement(this.getLocctr(), opcode, args);
    }
}
