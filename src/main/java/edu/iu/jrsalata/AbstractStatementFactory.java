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
    protected final HashMap<String, HexNum> symbolTable = new HashMap<>();
    protected final HashMap<String, Format> formatTable = new HashMap<>();
    protected final HashMap<String, HexNum> registerTable = new HashMap<>();

    // constructor
    public AbstractStatementFactory() {
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


    public abstract Statement processStatement(String statement) throws InvalidAssemblyFileException;

}
