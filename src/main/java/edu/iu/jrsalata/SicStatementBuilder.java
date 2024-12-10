package edu.iu.jrsalata;

import javax.script.ScriptException;

/**
 * The SicStatementBuilder class is responsible for processing and creating SIC statements
 * from a given string. It extends the AbstractStatementBuilder class and provides
 * specific implementations for handling SIC assembly language statements.
 * 
 * @see AbstractStatementBuilder
 * @see SicStatement
 */
public class SicStatementBuilder extends AbstractStatementBuilder {
    
    /**
     * For flexibility, any SIC program must start with this flag
     * and will be removed for processing
     */
    static final String SIC_FLAG = "!USE SIC";

    /**
     * Constructs a new SicStatementBuilder object.
     * This constructor calls the superclass constructor.
     */
    public SicStatementBuilder() {
        super();
    }

    /**
     * Processes a given assembly statement.
     * 
     * @param statement The assembly statement to process.
     * @throws InvalidAssemblyFileException If the statement contains an invalid mnemonic or format.
     * @throws ScriptException If there is an error in evaluating expressions within the statement.
     */
    @Override
    public void processStatement(String statement) throws InvalidAssemblyFileException, ScriptException {
        if (statement.strip().equals(SIC_FLAG)) {
            return;
        }
        Statement newStatement;
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

    /**
     * Creates a SIC statement using the provided mnemonic and arguments.
     *
     * @param mnemonic the mnemonic representing the operation code.
     * @param args the arguments for the SIC statement.
     * @return a new SicStatement object containing the location counter, opcode, and arguments.
     */
    private SicStatement createSicStatement(String mnemonic, String args) {

        HexNum opcode = this.instructionTable.get(mnemonic);
        return new SicStatement(this.getLocctr(), opcode, args);
    }
}
