// Class: SicStatementFactory
// Extends: StatementFactoryInterface
// Uses sic specific instructions to assemble for SIC machines

package edu.iu.jrsalata;

import java.util.logging.Logger;

public class SicStatementFactory extends AbstractStatementFactory {

    protected int lineNum = 0;
    Logger logger = Logger.getLogger(getClass().getName());

    // constructors
    public SicStatementFactory() {
        super();

    }

    // create a statement from a string
    public Statement processStatement(String statement) throws InvalidAssemblyFileException {
        // define return statement
        Statement newStatement;

        // increment lineNum by 1
        lineNum++;

        // First strip any unnecessary whitespace
        statement = statement.strip();

        // if the line is emtpy or is just a comment, return null
        if (statement.equals("") || statement.charAt(0) == '.') {
            return null;
        }

        // find the comment character
        // since there is the possibility of no comment existing, check if the comment
        // character exists
        // if not, then set it to the length of the string
        int period = statement.indexOf('.') == -1 ? statement.length() : statement.indexOf('.');
        statement = statement.substring(0, period).strip();

        // now we are going to split the string up into the different parts based on
        // space or tabs
        String[] parts = statement.split("\\s+");

        // the number of arguments determines the position of each part
        String mnemonic = "";
        String args = "";
        if (parts.length == 1) {
            mnemonic = parts[0];
        } else if (parts.length == 2) {
            mnemonic = parts[0];
            args = parts[1];
        } else if (parts.length == 3) {

            // if there are 3 parts, the 0 index is the label
            String label = parts[0];

            // add the label with the to the symbol table
            if (!SymTable.containsSymbol(label)) {
                SymTable.addSymbol(label, this.locctr);
            } else {
                StringBuilder msg = new StringBuilder("Duplicate label: ");
                msg.append(label);
                throw new InvalidAssemblyFileException(lineNum, msg.toString());
            }

            mnemonic = parts[1];
            args = parts[2];

            if (mnemonic.equals("START")) {
                this.name = label;
            }
        } else {
            // throw an exception
            throw new InvalidAssemblyFileException(lineNum, "Invalid Number of Arguments");
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
        return newStatement;
    }

    

    private void handleByte(String args, DirectiveStatement statement) throws InvalidAssemblyFileException {
        // check if the first char is C or X
        // C represents a constant string whose length is the length of the string
        // the object code of C is the ASCII value of each character in the string
        // X represents an object code whose length is 1 and the object code is the arg
        if (args.charAt(0) == 'C') {

            // remove the C and the ' at the end
            args = args.substring(2, args.length() - 1);

            // set the size to the length of the string
            statement.setSize(new HexNum(args.length()));

            // set the object code to the ASCII value of each character
            String objCode = "";
            for (int i = 0; i < args.length(); i++) {
                objCode += Integer.toHexString(args.charAt(i));
            }
            statement.setObjCode(objCode);

        } else if (args.charAt(0) == 'X') {

            // remove the X and the ' at the end
            args = args.substring(2, args.length() - 1);

            // set the size to 1
            statement.setSize(new HexNum(1));

            // set the object code to the arg
            statement.setObjCode(args);

        } else {
            StringBuilder msg = new StringBuilder("Invalid BYTE argument: ");
            msg.append(args);
            throw new InvalidAssemblyFileException(lineNum, msg.toString());
        }
    }

    private Statement handleAsmStatement(String mnemonic, String args) throws InvalidAssemblyFileException {

        DirectiveStatement returnVal = new DirectiveStatement();
        returnVal.setDirective(mnemonic);
        switch (mnemonic) {
            case "START":
                this.locctr = new HexNum(args, NumSystem.HEX);
                this.start = new HexNum(this.locctr.getDec());
                break;
            case "END":
                // do nothing
                break;
            case "BYTE":
                // move BYTE logic to other method for cleanliness
                handleByte(args, returnVal);
                break;
            case "WORD":
                // set size to 3 and set the object code
                returnVal.setSize(new HexNum(3));
                returnVal.setObjCode(new HexNum(args, NumSystem.DEC).toString(6));
                break;
            case "RESB":
                // set the args to the size
                returnVal.setSize(new HexNum(args, NumSystem.DEC));
                break;
            case "RESW":
                // set 3 * args to the size
                returnVal.setSize(new HexNum(3 * Integer.parseInt(args)));
                break;
            default:
                StringBuilder msg = new StringBuilder("Invalid SIC ASM mnemonic: ");
                msg.append(mnemonic);
                throw new InvalidAssemblyFileException(lineNum, msg.toString());
        }
        return returnVal;
    }

    private Statement createSicStatement(String mnemonic, String args) {

        // find the opcode of the mnemonic
        HexNum opcode = this.symbolTable.get(mnemonic);

        // create the SICStatement
        SicStatement returnVal = new SicStatement(this.locctr, opcode, args);

        return returnVal;
    }
}
