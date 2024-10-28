package edu.iu.jrsalata;

import java.util.HashMap;
import java.io.File;
import java.util.Scanner;
import java.io.InputStream;

// Class: StatementFactory
// Implements: StatementFactoryInterfac e
// This class will handle all of the statement parsing and statement creation, including setting flags and defining labels
public class StatementFactory implements StatementFactoryInterface {

    // locctr keeps track of the current location of each statement
    protected HexNum locctr;
    protected final HashMap<String, HexNum> symbolTable = new HashMap<String, HexNum>();
    protected final HashMap<String, Format> formatTable = new HashMap<String, Format>();
    protected final HashMap<String, HexNum> registerTable = new HashMap<String, HexNum>();

    // constructor
    public StatementFactory() {
        this.locctr = new HexNum(0);

        // add all of the opcodes to the table
        try {

            // Credit to https://github.com/cppcoders/SIC-XE-Assembler for the convenient
            // txt file
            // Format is: Mnemonic, Format, Opcode
            // Credit to
            // https://stackoverflow.com/questions/20389255/reading-a-resource-file-from-within-jar
            // for reading files within a jar
            InputStream file = getClass().getResourceAsStream("/instructions.txt");

            // read the file
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split("\\s+");

                // add the opcode and format to their respective tables
                this.symbolTable.put(parts[0], new HexNum(parts[2], NumSystem.HEX));

                // add the format to the format table
                Format newFormat = Format.ONE;
                if (parts[1].equals("1")) {
                    newFormat = Format.ONE;
                } else if (parts[1].equals("2")) {
                    newFormat = Format.TWO;
                } else if (parts[1].equals("3")) {
                    newFormat = Format.THREE;
                } else if (parts[1].equals("ASM")) {
                    newFormat = Format.ASM;
                } else {
                    System.out.println("Error: Unexpected format in instructions.txt");
                }

                this.formatTable.put(parts[0], newFormat);

            }
            // close the scanner
            sc.close();
        } catch (Exception e) {
            System.out.println("Error: Could not find instructions.txt");
            System.err.println(e);
        }

        // add all of the registers to the table
        try {
            InputStream file = getClass().getResourceAsStream("/registers.txt");

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
            System.out.println("Error: Could not find registers.txt");
            System.err.println(e);
        }
    }

    // create a statement from a string
    public Statement processStatement(String statement) {
        // define return statement
        Statement newStatement;

        // flag for format 4
        boolean eFlag = false;
        // First strip any unnecessary whitespace
        statement = statement.strip();

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
            if (!Main.symbolTable.containsKey(label)) {
                Main.symbolTable.put(label, this.locctr);
            } else {
                System.out.println("Error: Duplicate label: " + label);
            }

            mnemonic = parts[1];
            args = parts[2];
        } else {
            // throw an exception
            System.out.println("Error: Invalid number of arguments");
        }

        // since some mnemonics may contain '+' at the beginning, we want to remove it
        // for comparisons sake
        if (mnemonic.charAt(0) == '+') {
            eFlag = true;
            mnemonic = mnemonic.substring(1);
        }
        // generate a new statement based on its format
        if (this.formatTable.get(mnemonic) == Format.ONE) {
            newStatement = createStatement(mnemonic, args);
        } else if (this.formatTable.get(mnemonic) == Format.TWO) {
            newStatement = createRegStatement(mnemonic, args);
        } else if (this.formatTable.get(mnemonic) == Format.THREE) {
            newStatement = createExtStatement(mnemonic, args, eFlag);
        } else if (this.formatTable.get(mnemonic) == Format.ASM) {
            handleAsmStatement(mnemonic, args);
            return null;
        } else {
            System.out.println("Error: Format not found");
            System.out.println("Mnemonic: " + mnemonic);
            newStatement = new Statement();
        }
        this.locctr = this.locctr.add(newStatement.getSize());
        return newStatement;
    }

    private void handleAsmStatement(String mnemonic, String args) {
        if (mnemonic.equals("START")) {
            this.locctr = new HexNum(args, NumSystem.HEX);
        } else if (mnemonic.equals("END")) {
            // do nothing
        } else if (mnemonic.equals("BYTE")) {
            // add the size of the byte to the locctr
        } else if (mnemonic.equals("WORD")) {
            // add 3 to the locctr
            this.locctr = this.locctr.add(3);
        } else if (mnemonic.equals("RESB")) {
            // add args to the locctr
            this.locctr = this.locctr.add(Integer.parseInt(args));
        } else if (mnemonic.equals("RESW")) {
            // add 3 * args to the locctr
            this.locctr = this.locctr.add(3 * Integer.parseInt(args));
        } else {
            System.out.println("Error: Invalid ASM mnemonic");
        }
    }

    private Statement createStatement(String mnemonic, String args) {

        // check to make sure that there is only one element in parts
        HexNum opcode = this.symbolTable.get(mnemonic);
        return new Statement(this.locctr, opcode);
    }

    private Statement createRegStatement(String mnemonic, String args) {
        // Statement to return
        Statement returnVal = new Statement();
        HexNum opcode = this.symbolTable.get(mnemonic);

        // find both of the registers in parts[1]
        String[] registers = args.split(",");
        if (registers.length > 2 || registers.length <= 0) {
            System.out.println("Error: Invalid number of registers for format 2");

        }

        // find each of the registers in the registerTable
        HexNum reg1 = this.registerTable.get(registers[0]);
        HexNum reg2 = this.registerTable.get(registers[1]);

        // TODO: This is a bit of a hack, but it works for now
        if (reg1 == null) {
            System.out.println("Error: Register: " + registers[0] + " is invalid");
        } else if (reg2 == null && reg1 != null) {
            returnVal = new RegisterStatement(this.locctr, opcode, reg1);
        } else {
            returnVal = new RegisterStatement(this.locctr, opcode, reg1, reg2);
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

        return returnVal;
    }
}
