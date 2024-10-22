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
    protected final HashMap<String, HexNum> registerTable = new HashMap<String, HexNum>();
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

        // add all of the registers to the table
        try{
            File file = new File("registers.txt");

            // read the file
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split("\\s+");
                HexNum reg = new HexNum(parts[1], NumSystem.HEX);
                // add the register to the table
                this.registerTable.put(parts[0], reg);
            }
        } catch(Exception e){
            System.out.println("Error: Could not find registers.txt");
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
            System.out.println("Error: Mnemonic: " + mnemonic + " not found in instructions.txt");
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
        this.locctr = this.locctr.add(newStatement.getSize());
        return newStatement;
    }

    private Statement createStatement(String[] parts){

        // check to make sure that there is only one element in parts
        if(parts.length != 1){
            System.out.println("Error: Invalid number of arguments for format 1");
        }
        HexNum opcode = this.symbolTable.get(parts[0]);
        return new Statement(this.locctr, opcode);
    }

    private Statement createRegStatement(String[] parts){
        // Statement to return
        Statement returnVal = new Statement();
        // check to make sure that there are two elements in parts
        if(parts.length != 2){
            System.out.println("Error: Invalid number of arguments for format 2");
        }
        HexNum opcode = this.symbolTable.get(parts[0]);

        // find both of the registers in parts[1]
        String[] registers = parts[1].split(",");
        if(registers.length >= 2 && registers.length > 0){
            System.out.println("Error: Invalid number of registers for format 2");
        }

        // find each of the registers in the registerTable
        HexNum reg1 = this.registerTable.get(registers[0]);
        HexNum reg2 = this.registerTable.get(registers[1]);

        // TODO: This is a bit of a hack, but it works for now
        if(reg1 == null){
            System.out.println("Error: Register: " + registers[0] + " is invalid");
        } else if(reg2 == null && reg1 != null){
            returnVal = new RegisterStatement(this.locctr, opcode, reg1); 
        } else {
            returnVal = new RegisterStatement(this.locctr, opcode, reg1, reg2);
        }

        return returnVal;
    }

    private Statement createExtStatement(String[] parts){
        return new Statement();
    }
}
