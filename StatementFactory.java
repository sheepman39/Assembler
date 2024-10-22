import java.util.HashMap;
import java.io.File;
import java.util.Scanner;
// Class: StatementFactory
// Implements: StatementFactoryInterfac e
// This class will handle all of the statement parsing and statement creation, including setting flags and defining labels
public class StatementFactory implements StatementFactoryInterface{

    // locctr keeps track of the current location of each statement
    protected HexNum locctr;
    protected final HashMap<String, HexNum> symbolTable = new HashMap<String, HexNum>();
    protected final HashMap<String, Format> formatTable = new HashMap<String, Format>();

    // constructor 
    public StatementFactory(){
        this.locctr = new HexNum(0);
        
        // add all of the opcodes to the table
        try{

            // Credit to https://github.com/cppcoders/SIC-XE-Assembler for the convenient txt file
            // Format is: Mnemonic, Format, Opcode
            File file = new File("instructions.txt");

            // read the file
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split("\\s+");

                // add the opcode and format to their respective tables
                this.symbolTable.put(parts[0], new HexNum(parts[2], NumSystem.HEX));

                // add the format to the format table
                Format newFormat = Format.ONE;
                if(parts[1].equals("1")){
                    newFormat = Format.ONE;
                } else if(parts[1].equals("2")){
                    newFormat = Format.TWO;
                } else if(parts[1].equals("3")){
                    newFormat = Format.THREE;
                } else {
                    System.out.println("Error: Unexpected format in instructions.txt");
                }

                this.formatTable.put(parts[0], newFormat);

            }
        }catch (Exception e){
            System.out.println("Error: Could not find instructions.txt");
            System.err.println(e);
        }
    }

    // create a statement from a string
    public Statement processStatement(String statement){
        // define return statement
        Statement newStatement;
        // First strip any unnecessary whitespace
        statement = statement.strip();

        // find the comment character
        // since there is the possibility of no comment existing, check if the comment character exists
        // if not, then set it to the length of the string
        int period = statement.indexOf('.') == -1 ? statement.length() : statement.indexOf('.');
        statement = statement.substring(0, period).strip();

        // now we are going to split the string up into the different parts based on space or tabs
        String[] parts = statement.split("\\s+");
        
        // the number of arguments determines the position of each part
        String mnemonic = "";

        if(parts.length == 1){
            mnemonic = parts[0];
        } else if(parts.length == 2){
            mnemonic = parts[0];
        } else if(parts.length == 3){
            mnemonic = parts[1];
        } else {
            // throw an exception
            System.out.println("Error: Invalid number of arguments");
        }
        
        // ensure that the mnemonic is in the symbol table
        if(!this.symbolTable.containsKey(mnemonic)){
            System.out.println("Error: Mnemonic not found in instructions.txt");
        }

        // generate a new statement based on its format
        if(this.formatTable.get(mnemonic) == Format.ONE){
            newStatement = createStatement(parts);
        } else if(this.formatTable.get(mnemonic) == Format.TWO){
            newStatement = createRegStatement(parts);
        } else if(this.formatTable.get(mnemonic) == Format.THREE){
            newStatement = createExtStatement(parts);
        } else {
            System.out.println("Error: Unexpected format in instructions.txt");
            newStatement = new Statement();
        }

        return new Statement();
    }

    private Statement createStatement(String[] parts){
        return new Statement();
    }

    private Statement createRegStatement(String[] parts){
        return new Statement();
    }
    private Statement createExtStatement(String[] parts){
        return new Statement();
    }
}
