package edu.iu.jrsalata;

import java.util.HashMap;
import java.util.Scanner;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

// Class: StatementFactory
// extends: StatementFactoryInterfac e
// This class will handle all of the statement parsing and statement creation, including setting flags and defining labels
public class StatementFactory extends AbstractStatementFactory {

    protected int lineNum = 0;
    protected String base = "";
    protected final HashMap<String, HexNum> symbolTable = new HashMap<String, HexNum>();
    protected final HashMap<String, Format> formatTable = new HashMap<String, Format>();
    protected final HashMap<String, HexNum> registerTable = new HashMap<String, HexNum>();
    Logger logger = Logger.getLogger(getClass().getName());

    // constructor
    public StatementFactory() {
        super();
        loadInstructions("/instructions.txt");
        loadRegisters("/registers.txt");
    }


    // create a statement from a string
    public Statement processStatement(String statement) throws InvalidAssemblyFileException {
        // define return statement
        Statement newStatement;

        // increment lineNum by 1
        lineNum++;

        // flag for format 4
        boolean eFlag = false;
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
            case ASM:
                newStatement = handleAsmStatement(mnemonic, args);
                break;
            default:
                StringBuilder msg = new StringBuilder("Mnemonic '");
                msg.append(mnemonic);
                msg.append("' not found");
                throw new InvalidAssemblyFileException(lineNum, msg.toString());
        }
        this.locctr = this.locctr.add(newStatement.getSize());
        return newStatement;
    }

    private void loadInstructions(String filename) {
        // add all of the opcodes to the table
        try {

            // Credit to https://github.com/cppcoders/SIC-XE-Assembler for the convenient
            // txt file
            // Format is: Mnemonic, Format, Opcode
            // Credit to
            // https://stackoverflow.com/questions/20389255/reading-a-resource-file-from-within-jar
            // for reading files within a jar
            InputStream file = getClass().getResourceAsStream(filename);

            // read the file
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split("\\s+");

                // add the opcode and format to their respective tables
                this.symbolTable.put(parts[0], new HexNum(parts[2], NumSystem.HEX));

                // add the format to the format table
                Format newFormat = Format.ONE;
                switch (parts[1]) {
                    case "1":
                        newFormat = Format.ONE;
                        break;
                    case "2":
                        newFormat = Format.TWO;
                        break;
                    case "3":
                        newFormat = Format.THREE;
                        break;
                    case "SIC":
                        // note that since this is sic/xe
                        // we will be using F3
                        newFormat = Format.THREE;
                        break;
                    case "ASM":
                        newFormat = Format.ASM;
                        break;
                    default:
                        logger.log(Level.WARNING, "Error: Unexpected format '{}' in instructions.txt", parts[1]);
                        break;
                }

                this.formatTable.put(parts[0], newFormat);

            }
            // close the scanner
            sc.close();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error: Could not find {}", filename);
            logger.warning(e.getMessage());
        }
    }

    private void loadRegisters(String filename) {
        // add all of the registers to the table
        try {
            InputStream file = getClass().getResourceAsStream(filename);

            // read the file
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split("\\s+");
                HexNum reg = new HexNum(parts[1], NumSystem.HEX);
                // add the register to the table
                this.registerTable.put(parts[0], reg);
            }
            // close the scanner
            sc.close();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error: Could not find {}", filename);
            logger.warning(e.getMessage());
        }
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
            case "BASE":
                // set the base to the args
                // will be passed to the ExtendedStatement
                this.base = args;
                break;
            case "NOBASE":
                // clear the base
                this.base = "";
            default:
                StringBuilder msg = new StringBuilder("Invalid ASM mnemonic: ");
                msg.append(mnemonic);
                throw new InvalidAssemblyFileException(lineNum, msg.toString());
        }
        return returnVal;
    }

    private Statement createStatement(String mnemonic, String args) {

        // check to make sure that there is only one element in parts
        HexNum opcode = this.symbolTable.get(mnemonic);
        return new SingleStatement(this.locctr, opcode);
    }

    private Statement createRegStatement(String mnemonic, String args) throws InvalidAssemblyFileException {

        // Statement to return
        RegisterStatement returnVal = new RegisterStatement();
        returnVal.setLocation(this.locctr);

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
        ExtendedStatement returnVal = new ExtendedStatement(this.locctr, opcode, args);

        // if there is an eFlag, set it
        if (eFlag) {
            returnVal.setEFlag();
        } 
        returnVal.setBase(this.base);

        return returnVal;
    }
}
