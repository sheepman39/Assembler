// Class: AbstractStatementBuilder
// This is an interface that will define the methods that concretions will use to create statements
package edu.iu.jrsalata;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptException;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;

public abstract class AbstractStatementBuilder {
    static final String DEFAULT_BLOCK = "DEFAULT";
    static final Logger logger = Logger.getLogger(AbstractStatementBuilder.class.getName());

    protected String name;
    protected String block;
    protected int lineNum;
    protected ArrayList<String> absoluteExpressions = new ArrayList<>();
    protected Queue<String> externalDefinitions = new LinkedList<>();
    protected Queue<String> externalReferences = new LinkedList<>();
    protected ArrayList<String> referenceModifications = new ArrayList<>();
    protected Queue<DirectiveStatement> literals = new LinkedList<>();
    protected Queue<Statement> statements = new LinkedList<>();
    protected final HashMap<String, HexNum> instructionTable;
    protected final HashMap<String, Format> formatTable;
    protected final HashMap<String, HexNum> registerTable;
    protected final HashMap<String, HexNum> locctrTable;

    // note that we are making startTable a LinkedHashMap
    // this is so that we can maintain the order of each program block
    // which is needed for calculating the relative start locations
    // of each block
    protected final LinkedHashMap<String, HexNum> startTable;

    // constructor
    protected AbstractStatementBuilder() {
        this.instructionTable = new HashMap<>();
        this.formatTable = new HashMap<>();
        this.registerTable = new HashMap<>();
        this.name = "";
        this.lineNum = 0;
        this.block = DEFAULT_BLOCK;

        // since our locctr and start are not in the symbol table, we need to store them
        // in a separate table
        this.locctrTable = new HashMap<>();
        this.startTable = new LinkedHashMap<>();

        // add the default values to locctrTable and startTable
        this.locctrTable.put(DEFAULT_BLOCK, new HexNum(0));
        this.startTable.put(DEFAULT_BLOCK, new HexNum(0));

        loadInstructions("/instructions.txt");
        loadRegisters("/registers.txt");
    }

    // get the start location
    public HexNum getStart() {
        return this.getStart(DEFAULT_BLOCK);
    }

    public HexNum getStart(String block) {
        return this.startTable.get(block);
    }

    // get the length of the entire program
    public HexNum getTotalLength() {
        // remember to add the default starting location
        HexNum total = new HexNum();

        for (String programBlock : this.startTable.keySet()) {
            total = total.add(this.getLocctr(programBlock));
        }

        return total;
    }

    // get the location counter
    public HexNum getLocctr() {
        return this.getLocctr(this.block);
    }

    public HexNum getLocctr(String block) {
        return this.locctrTable.get(block);
    }

    public Queue<Statement> getStatements() {

        // now that the program is done with pass 1,
        // calculate the length and relative start of each block
        HexNum total = new HexNum();
        HexNum tmp;
        HexNum tmpStart;
        for (String currentBlock : this.startTable.keySet()) {
            tmpStart = this.getStart(currentBlock);
            tmpStart = tmpStart.add(total);
            this.setStart(currentBlock, tmpStart);
            tmp = this.getLocctr(currentBlock);
            total = total.add(tmp);

        }

        // now modify the value of each symbol to be relative to the start of the
        // program
        // instead of relative to the start of their block
        String symbolBlock;
        HexNum blockStart;
        for (String currentSymbol : SymTable.getKeys(this.name)) {

            // check if the currentSymbol is absolute first
            // since the value doesn't depend on a program block, we don't need to modify it
            if (this.absoluteExpressions.contains(currentSymbol)) {
                continue;
            }
            // first get the value of the symbol
            tmp = SymTable.getSymbol(currentSymbol, this.name);

            // then get the block of the symbol
            symbolBlock = SymTable.getBlock(currentSymbol, this.name);

            // then get the start of the block
            blockStart = this.getStart(symbolBlock);

            // then add the two values
            tmp = tmp.add(blockStart);

            // place the new value in the symbol table
            SymTable.addSymbol(currentSymbol, tmp, this.block, this.name);
        }
        return this.statements;
    }

    public String getName() {
        // name needs to be exactly six characters long
        return Utility.lengthCheck(!this.name.isEmpty() ? this.name : "OUTPUT");
    }

    public void setName(String name) {
        this.name = name;
    }

    public Queue<String> getExternalDefinitions() {
        return this.externalDefinitions;
    }

    public Queue<String> getExternalReferences() {
        return this.externalReferences;
    }

    public List<String> getReferenceModifications() {
        return this.referenceModifications;
    }

    protected void setStart(String block, HexNum start) {
        this.startTable.put(block, start);
    }

    protected void addLocctr(HexNum locctr) {
        this.addLocctr(this.block, locctr);
    }

    protected void addLocctr(String block, HexNum locctr) {

        HexNum currentLocctr = this.locctrTable.get(block);
        currentLocctr = currentLocctr.add(locctr);
        this.locctrTable.put(block, currentLocctr);

    }

    protected final void loadInstructions(String filename) {
        // add all of the opcodes to the table
        try {

            // Credit to https://github.com/cppcoders/SIC-XE-Assembler for the convenient
            // txt file
            // Format is: Mnemonic, Format, Opcode
            // Credit to
            // https://stackoverflow.com/questions/20389255/reading-a-resource-file-from-within-jar
            // for reading files within a jar
            InputStream file = getClass().getResourceAsStream(filename);

            try ( // read the file
                    Scanner sc = new Scanner(file)) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] parts = line.split("\\s+");

                    // add the opcode and format to their respective tables
                    this.instructionTable.put(parts[0], new HexNum(parts[2], NumSystem.HEX));

                    // add the format to the format table
                    Format newFormat;
                    switch (parts[1]) {
                        case "1" -> newFormat = Format.ONE;
                        case "2" -> newFormat = Format.TWO;
                        case "3" -> newFormat = Format.THREE;
                        case "SIC" -> newFormat = Format.SIC;
                        case "ASM" -> newFormat = Format.ASM;
                        default -> {
                            newFormat = Format.ASM;
                            logger.log(Level.WARNING, "Error: Unexpected format '{}' in instructions.txt", parts[1]);
                        }
                    }

                    this.formatTable.put(parts[0], newFormat);
                }
                // close the scanner
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error: Could not find {}", filename);
            logger.warning(e.getMessage());
        }
    }

    protected final void loadRegisters(String filename) {
        // add all of the registers to the table

        InputStream file = getClass().getResourceAsStream(filename);

        try ( // read the file
                Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split("\\s+");
                HexNum reg = new HexNum(parts[1], NumSystem.HEX);
                // add the register to the table
                this.registerTable.put(parts[0], reg);
            }
            // close the scanner
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error: Could not find {}", filename);
            logger.warning(e.getMessage());
        }
    }

    protected String[] splitStatement(String statement) throws InvalidAssemblyFileException {

        String[] parts = Utility.splitLine(statement);
        // the number of arguments determines the position of each part
        String mnemonic = "";
        String args = "";
        String label = "";
        switch (parts.length) {
            case 1 -> mnemonic = parts[0];
            case 2 -> {
                mnemonic = parts[0];
                args = parts[1];
            }
            case 3 -> {
                // if there are 3 parts, the 0 index is the label
                label = parts[0];
                mnemonic = parts[1];
                args = parts[2];
                handleLabels(label, mnemonic, args);
            }
            default -> // throw an exception
                throw new InvalidAssemblyFileException(lineNum, "Invalid Number of Arguments");
        }

        // check for the '=' character meaning it is a literal value
        if (!args.isEmpty() && args.charAt(0) == '=') {
            args = args.substring(1);
            handleLiteral(args);
        }
        return new String[] { mnemonic, args, label };
    }

    protected String handleModification(String copyArgs, String part) {
        // if the part is an external reference, we need to set the value to 0 and add a
        // modification record
        // this is because the value is not known at assembly time
        StringBuilder modification = new StringBuilder();
        modification.append("M");
        modification.append(this.getLocctr().toString(6));
        // we are appending the length of the modification, which is an entire word or
        // 06
        modification.append("06");

        // then we add if we are adding or subtracting its value
        char sign = copyArgs.charAt(0) == '-' ? '-' : '+';
        modification.append(sign);
        copyArgs = copyArgs.length() < 0 ? copyArgs.substring(1) : copyArgs;

        // then append the external reference
        modification.append(part);

        // append it to the external reference
        this.referenceModifications.add(modification.toString());
        return copyArgs;
    }

    protected String evaluateExpression(String args) {
        String[] parts = args.split("[+\\-*/]");
        String copyArgs = args;

        // if there are no parts, return the original string
        // since that will represent the value of the expression
        if (parts.length < 2) {
            return args;
        }
        for (String part : parts) {
            if (SymTable.containsSymbol(part, this.name)) {
                args = args.replace(part, Integer.toString(SymTable.getSymbol(part, this.name).getDec()));
            } else if (this.externalReferences.contains(part)) {
                copyArgs = handleModification(copyArgs, part);
                args = args.replace(part, "0");

            }

            // now we remove the first word from our copy args in order
            // to find the next sign for modification symbols, if necessary
            copyArgs = copyArgs.trim().replace(part, "");

        }

        Expression expression = new ExpressionBuilder(args).build();

        ValidationResult expressionResults = expression.validate();

        if (expressionResults.isValid()) {
            // note that we are type casting as int because we require a whole number
            int result = (int) expression.evaluate();
            return Integer.toString(result);
        }
        return args;

    }

    protected HexNum handleExpression(String label, String args) {

        // first split the string based on each section based on regex
        // the regex splits each section by the operators +, -, *, /
        // this allows us to replace the defined symbols with our values
        String[] parts = args.split("[+\\-*/]");
        String copyArgs = args;
        boolean isAbsolute = true;
        for (String part : parts) {
            // if the part is a symbol, replace it with the decimal value as we need to do
            // math in base 10
            if (SymTable.containsSymbol(part, this.name)) {
                args = args.replace(part, Integer.toString(SymTable.getSymbol(part, this.name).getDec()));
            } else if (this.externalReferences.contains(part)) {
                copyArgs = handleModification(copyArgs, part);
                args = args.replace(part, "0");

            } else {
                isAbsolute = false;
            }

            // now we remove the first word from our copy args in order
            // to find the next sign for modification symbols, if necessary
            copyArgs = copyArgs.trim().replace(part, "");
        }

        // now that we have replaced all of the symbols with their values, we can
        // evaluate the expression
        // credit to
        // https://www.baeldung.com/java-evaluate-math-expression-string
        // for guide on the library we are using
        Expression expression = new ExpressionBuilder(args).build();

        // note that we are type casting as int because we require a whole number
        int result = (int) expression.evaluate();
        HexNum hexResult = new HexNum(Integer.toString(result), NumSystem.DEC);

        // add the symbol to the absoluteExpressions list if there are no hard-coded
        // values
        // this means that the value is always absolute at the time of assembly
        // we need this for program block control
        if (isAbsolute) {
            this.absoluteExpressions.add(label);
            SymTable.addSymbol(label, hexResult, "ABSOLUTE", this.name);
        } else {
            SymTable.addSymbol(label, hexResult, this.block, this.name);
        }

        // evaluate the expression and return it as a string
        return hexResult;
    }

    protected void handleLabels(String label, String mnemonic, String args)
            throws InvalidAssemblyFileException {
        // We want to set the label to the current location when it is not in the symbol
        // table and one of the two conditions is true:
        // 1) mneomonic is not EQU
        // 2) args is "*"
        // because other symbols require their location to be stored or the "*"
        // EQU requires the given value to be their stored value
        label = Utility.lengthCheck(label);
        if (!SymTable.containsSymbol(label, this.name) && (!mnemonic.equals("EQU") || args.equals("*"))) {
            SymTable.addSymbol(label, this.getLocctr(this.block), this.block, this.name);
        } else if (!SymTable.containsSymbol(label, this.name) && mnemonic.equals("EQU")) {

            // since args can potentially be an expression, we need to evaluate it before
            // adding it to the table
            // we will handle the expression and add it to the symbol table in that method
            handleExpression(label, args);

        } else {

            // we can't have duplicate labels so throw an exception
            StringBuilder msg = new StringBuilder("Duplicate label: ");
            msg.append(label);
            throw new InvalidAssemblyFileException(lineNum, msg.toString());
        }

        // if the mnemonic is START, we need to set the name to it
        // for the object code that will be generated later
        if (mnemonic.equals("START")) {
            this.name = label;
        }
    }

    protected void handleLiteral(String args) throws InvalidAssemblyFileException {

        // Set the directive to the args as it is the name of the symbol
        DirectiveStatement literal = new DirectiveStatement();
        literal.setDirective(args);

        // since literals and BYTE statements use the same syntax to define and generate
        // object code, we can interchange them here
        handleByte(args, literal);

        // this queue will be handled when LTORG is called or after END
        literals.add(literal);
    };

    protected void handleByte(String args, DirectiveStatement statement) throws InvalidAssemblyFileException {
        // check if the first char is C or X
        // C represents a constant string whose length is the length of the string
        // the object code of C is the ASCII value of each character in the string
        // X represents an object code whose length is 1 and the object code is the arg
        switch (args.charAt(0)) {
            case 'C' -> {
                // remove the C and the ' at the end for easier processing
                args = args.substring(2, args.length() - 1);
                // set the size to the length of the string as we need the space in the
                // generated object code
                statement.setSize(new HexNum(args.length()));
                // set the object code to the ASCII value of each character
                StringBuilder objCode = new StringBuilder();
                for (int i = 0; i < args.length(); i++) {
                    objCode.append(Integer.toHexString(args.charAt(i)));
                }
                statement.setObjCode(objCode.toString());
            }
            case 'X' -> {
                // remove the X and the ' at the end for easier processing
                args = args.substring(2, args.length() - 1);
                // set the size to 1 as the object code is the arg
                statement.setSize(new HexNum(1));
                // set the object code to the arg
                statement.setObjCode(args);
            }
            default -> {
                // if there is an invalid argument, throw an exception
                StringBuilder msg = new StringBuilder("Invalid BYTE argument: ");
                msg.append(args);
                throw new InvalidAssemblyFileException(lineNum, msg.toString());
            }
        }
    }

    protected void assembleLiterals() {
        DirectiveStatement tmpLiteral;
        // loop to assemble each unique literal and add it to our SymTable for other
        // statements to use
        while (!this.literals.isEmpty()) {
            tmpLiteral = this.literals.poll();
            if (!SymTable.containsSymbol(tmpLiteral.getDirective(), this.name)) {
                SymTable.addSymbol(tmpLiteral.getDirective(), this.getLocctr(), this.block, this.name);
                this.addStatement(tmpLiteral);
                this.addLocctr(this.block, tmpLiteral.getSize());
            }
        }
    }

    protected Statement handleAsmStatement(String mnemonic, String args) throws InvalidAssemblyFileException {

        DirectiveStatement returnVal = new DirectiveStatement();
        returnVal.setDirective(mnemonic);
        args = evaluateExpression(args);
        switch (mnemonic) {
            case "START" -> {
                this.addLocctr(DEFAULT_BLOCK, new HexNum(0));
                this.setStart(DEFAULT_BLOCK, new HexNum(args, NumSystem.HEX));
            }
            case "END" -> // handle any remaining literals
                assembleLiterals();
            case "BYTE" -> // move BYTE logic to other method for cleanliness
                handleByte(args, returnVal);
            case "WORD" -> {
                // set size to 3 and set the object code
                returnVal.setSize(new HexNum(3));
                returnVal.setObjCode(new HexNum(args, NumSystem.DEC).toString(6));
            }
            case "RESB" -> // set the args to the size
                returnVal.setSize(new HexNum(args, NumSystem.DEC));
            case "RESW" -> // set 3 * args to the size
                returnVal.setSize(new HexNum(3 * Integer.parseInt(args)));
            case "LTORG" -> assembleLiterals();
            case "EQU" -> {
                // handle EQU in the handleLabels method
                // so this section just ensures that there is no error
            }
            case "USE" -> {

                // check if args is empty when blocks are switched back. If it is, set to
                // default block
                if (args.equals("")) {
                    args = DEFAULT_BLOCK;
                }

                // since we can use blocks as many times as we want, we need to check if we need
                // to create the block
                this.locctrTable.putIfAbsent(args, new HexNum(0));
                this.startTable.putIfAbsent(args, new HexNum(0));

                // set the current block to the provided args
                this.block = args;
            }
            case "EXTDEF" -> {
                // split the args by commas in order to get each
                String[] defList = args.trim().split(",");
                for (String def : defList) {
                    def = Utility.lengthCheck(def);
                    this.externalDefinitions.add(def);
                }
            }
            case "EXTREF" -> {
                // split the args by commas in order to get each
                String[] refList = args.trim().split(",");
                for (String ref : refList) {
                    ref = Utility.lengthCheck(ref);
                    this.externalReferences.add(ref);
                }
            }
            default -> {
                StringBuilder msg = new StringBuilder("Invalid SIC ASM mnemonic: ");
                msg.append(mnemonic);
                throw new InvalidAssemblyFileException(lineNum, msg.toString());
            }
        }
        // EQU is handled in the handleLabels method
        return returnVal;
    }

    protected void addStatement(Statement statement) {
        if (statement != null) {
            statement.setBlock(this.block);
            statement.setControlSection(this.name);
            this.statements.add(statement);
        }
    }

    public abstract void processStatement(String statement) throws InvalidAssemblyFileException, ScriptException;

}
