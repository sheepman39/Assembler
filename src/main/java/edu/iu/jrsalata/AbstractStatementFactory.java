// Class: StatementFactoryInterface
// This is an interface that will define the methods that concretions will use to create statements
package edu.iu.jrsalata;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractStatementFactory {
    Logger logger = Logger.getLogger(getClass().getName());

    // locctr keeps track of the current location of each statement
    protected HexNum locctr;
    protected HexNum start = new HexNum(0);
    protected String name = "";
    protected int lineNum = 0;
    protected final HashMap<String, HexNum> symbolTable;
    protected final HashMap<String, Format> formatTable;
    protected final HashMap<String, HexNum> registerTable;

    // constructor
    public AbstractStatementFactory() {
        this.symbolTable = new HashMap<>();
        this.formatTable = new HashMap<>();
        this.registerTable = new HashMap<>();
        this.locctr = new HexNum(0);
        this.start = new HexNum();
        this.name = "";
        loadInstructions("/instructions.txt");
        loadRegisters("/registers.txt");
    }

    // get the start location
    public HexNum getStart() {
        return this.start;
    }

    // get the length of the program
    public HexNum getLen() {
        int lenStart = this.start.getDec();
        int lenEnd = this.locctr.getDec();
        return new HexNum(lenEnd - lenStart);
    }

    public String getName() {

        // name needs to be exactly six characters long
        // if we have no name, default is OBJECT
        // if the name is longer than 6, truncate it
        // if the name is shorter than 6, pad it with spaces at the end
        // if the name is exactly 6, return it
        if (this.name.equals("")) {
            return "OUTPUT";
        } else if (this.name.length() > 6) {
            return this.name.substring(0, 6).toUpperCase();
        } else if (this.name.length() < 6) {
            StringBuilder sb = new StringBuilder(this.name);
            for (int i = 0; i < 6 - this.name.length(); i++) {
                sb.append(" ");
            }
            return sb.toString().toUpperCase();
        } else {
            return this.name.toUpperCase();
        }
    }

    protected void loadInstructions(String filename) {
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
                        newFormat = Format.SIC;
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

    protected void loadRegisters(String filename) {
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

    protected String[] splitStatement(String statement) throws InvalidAssemblyFileException {
        // First strip any unnecessary whitespace
        statement = statement.strip();

        // if the line is emtpy or is just a comment, return null
        if (statement.equals("") || statement.charAt(0) == '.') {
            return new String[] { "", "" };
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
        return new String[] { mnemonic, args };
    }

    public abstract Statement processStatement(String statement) throws InvalidAssemblyFileException;

}