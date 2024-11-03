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
                } else if (parts[1].equals("SIC")){
                    newFormat = Format.SIC;
                }else if (parts[1].equals("ASM")) {
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
            if (!SymTable.containsSymbol(label)) {
                SymTable.addSymbol(label, this.locctr);
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
        } else if (this.formatTable.get(mnemonic) == Format.THREE || this.formatTable.get(mnemonic) == Format.SIC) {
            newStatement = createExtStatement(mnemonic, args, eFlag);
        } else if (this.formatTable.get(mnemonic) == Format.ASM) {
            newStatement = handleAsmStatement(mnemonic, args);
        } else {
            System.out.println("Error: Mnemonic not found");
            System.out.println("Mnemonic: " + mnemonic);
            return null;
        }
        this.locctr = this.locctr.add(newStatement.getSize());
        return newStatement;
    }

    private void handleByte(String args, DirectiveStatement statement){
        // check if the first char is C or X
        // C represents a constant string whose length is the length of the string
        // the object code of C is the ASCII value of each character in the string
        // X represents an object code whose length is 1 and the object code is the arg
        if(args.charAt(0) == 'C'){

            // remove the C and the ' at the end
            args = args.substring(2, args.length() - 1);

            // set the size to the length of the string
            statement.setSize(new HexNum(args.length()));

            // set the object code to the ASCII value of each character
            String objCode = "";
            for(int i = 0; i < args.length(); i++){
                objCode += Integer.toHexString((int)args.charAt(i));
            }
            statement.setObjCode(objCode);
        
        } else if(args.charAt(0) == 'X'){

            // remove the X and the ' at the end
            args = args.substring(2, args.length() - 1);

            // set the size to 1
            statement.setSize(new HexNum(1));

            // set the object code to the arg
            statement.setObjCode(args);

        } else {
            System.out.println("Error: Invalid BYTE argument");
        }
    }

    private Statement handleAsmStatement(String mnemonic, String args) {

        DirectiveStatement returnVal = new DirectiveStatement();
        returnVal.setDirective(mnemonic);
        if (mnemonic.equals("START")) {
            this.locctr = new HexNum(args, NumSystem.HEX);
        } else if (mnemonic.equals("END")) {
            // do nothing
        } else if (mnemonic.equals("BYTE")) {
            // move BYTE logic to other method for cleanliness
            handleByte(args, returnVal);
        } else if (mnemonic.equals("WORD")) {
            // set size to 3 and set the object code
            returnVal.setSize(new HexNum(3));
            returnVal.setObjCode(new HexNum(args, NumSystem.DEC).toString(6));
        } else if (mnemonic.equals("RESB")) {
            // set the args to the size
            returnVal.setSize(new HexNum(Integer.parseInt(args)));
        } else if (mnemonic.equals("RESW")) {
            // set 3 * args to the size
            returnVal.setSize(new HexNum(3 * Integer.parseInt(args)));
        } else {
            System.out.println("Error: Invalid ASM mnemonic");
        }
        return returnVal;
    }

    private Statement createStatement(String mnemonic, String args) {

        // check to make sure that there is only one element in parts
        HexNum opcode = this.symbolTable.get(mnemonic);
        return new SingleStatement(this.locctr, opcode);
    }

    private Statement createRegStatement(String mnemonic, String args) {
        
        // Statement to return
        RegisterStatement returnVal = new RegisterStatement();
        returnVal.setLocation(this.locctr);

        // find the opcode of the mnemonic
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
            returnVal.setReg1(reg1);
        } else {
            returnVal.setReg1(reg1);
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

        // if the format is SIC, set the flag
        if(this.formatTable.get(mnemonic) == Format.SIC){
            returnVal.setSICFlag();
        }

        return returnVal;
    }
}
