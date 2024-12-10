// AbstractStatementBuilder.java
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


/**
 * The AbstractStatementBuilder is an abstract class that handles most of the logic when it comes to creating concrete StatementBuilders
 * 
 * Since most of the logic regarding assembler directives is the same,
 * most of it is stored in this class.
 */
public abstract class AbstractStatementBuilder {
    
    /**
     * Name of the default block for program blocks
     */
    static final String DEFAULT_BLOCK = "DEFAULT";

    /**
     * Standard logger for info and error messages
     */
    static final Logger logger = Logger.getLogger(AbstractStatementBuilder.class.getName());


    /**
     * Name of the given program
     */
    protected String name;

    /**
     * Current name of the program block
     */
    protected String block;

    /**
     * Current line number based on given input
     * May be inaccurate due to how comments are handled
     */
    protected int lineNum;

    /**
     * absoluteExpressions are expressions based on two variable locations
     * Ex) BUFFEND - BUFFER would be absolute since this specifies 
     * BUFFEND - 10 would not be considered to be an absolute expression
     */
    protected ArrayList<String> absoluteExpressions = new ArrayList<>();

    /**
     * externalDefinitions refers to any label that may be accessed
     * in a separate control section
     */
    protected Queue<String> externalDefinitions = new LinkedList<>();

    /**
     * externalReferences refers to labels in this control section
     * that are accessed in a separate section
     */
    protected Queue<String> externalReferences = new LinkedList<>();

    /**
     * with every external reference, we need a new modification record
     * in order to specify where to find the external reference
     */
    protected ArrayList<String> referenceModifications = new ArrayList<>();

    /**
     * literals are values that are hard coded in like =X'05'
     * These need to be placed in a literal pool later, hence why they are stored here
     */
    protected Queue<DirectiveStatement> literals = new LinkedList<>();

    /**
     * statements is the queue that is returned to the client
     * Each of them contains a statement that has appropriate info
     * on its location, args, and values so it can be assembled
     */
    protected Queue<Statement> statements = new LinkedList<>();

    /**
     * instructionTable is a map that holds each of the assembler instructions
     */
    protected final HashMap<String, HexNum> instructionTable;

    /**
     * formatTable is a map that holds the format of each instruction
     */
    protected final HashMap<String, Format> formatTable;

    /** 
     * registerTable is a map that holds each valid register
     */
    protected final HashMap<String, HexNum> registerTable;

    /**
     * locctrTable holds the location counter of each program block
     */
    protected final HashMap<String, HexNum> locctrTable;


    /**
     * startTable handles the starting location of each program block
     * 
     * Note that we are making startTable a LinkedHashMap
     * this is so that we can maintain the order of each program block
     * which is needed for calculating the relative start locations
     * of each block
     */
    protected final LinkedHashMap<String, HexNum> startTable;

    /**
     * Default constructor that initializes each variable
     */
    protected AbstractStatementBuilder() {
        this.instructionTable = new HashMap<>();
        this.formatTable = new HashMap<>();
        this.registerTable = new HashMap<>();
        this.name = "";
        this.lineNum = 0;
        this.block = DEFAULT_BLOCK;
        this.locctrTable = new HashMap<>();
        this.startTable = new LinkedHashMap<>();

        // add the default values to locctrTable and startTable
        this.locctrTable.put(DEFAULT_BLOCK, new HexNum(0));
        this.startTable.put(DEFAULT_BLOCK, new HexNum(0));

        // we store instructions and registers externally so we load them here
        loadInstructions("/instructions.txt");
        loadRegisters("/registers.txt");
    }

    /**
     * Retrieves the starting address as a HexNum for the default starting block
     *
     * @return the starting address as a HexNum for the default block.
     */
    public HexNum getStart() {
        return this.getStart(DEFAULT_BLOCK);
    }

    /**
     * Retrieves the starting HexNum for the specified block.
     *
     * @param block the name of the block for which to retrieve the starting HexNum
     * @return the starting HexNum associated with the specified block, or error if the block is not found
     */
    public HexNum getStart(String block) {
        return this.startTable.get(block);
    }

    /**
     * Calculates the total length of all program blocks by summing up the location counters
     * of each block in the start table.
     *
     * @return a HexNum representing the total length of all program blocks.
     */
    public HexNum getTotalLength() {

        HexNum total = new HexNum();

        for (String programBlock : this.startTable.keySet()) {
            total = total.add(this.getLocctr(programBlock));
        }

        return total;
    }

    /**
     * Retrieves the location counter (LOCCTR) for the current block.
     *
     * @return the location counter (LOCCTR) as a HexNum object.
     */
    public HexNum getLocctr() {
        return this.getLocctr(this.block);
    }

    /**
     * Retrieves the location counter (LOCCTR) for the specified block.
     *
     * @param block the name of the block for which to retrieve the LOCCTR
     * @return the location counter (LOCCTR) as a HexNum object for the specified block,
     *         or null if the block is not found in the locctrTable
     */
    public HexNum getLocctr(String block) {
        return this.locctrTable.get(block);
    }

    /**
     * Calculates the length and relative start of each block in the program,
     * modifies the value of each symbol to be relative to the start of the program,
     * and returns the queue of statements.
     * This is to be ran after all of the statements are fed into the builder
     * 
     * <p>This method performs the following steps:
     * <ol>
     *   <li>Calculates the starting address of each block and updates the start of the block
     *       to the sum of the total and starting address. It also updates the total with the
     *       locctr of the block.</li>
     *   <li>Modifies the value of each symbol to be relative to the start of the program
     *       instead of the start of their individual block, unless the symbol is absolute.</li>
     * </ol>
     *
     * @return a queue of statements.
     */
    public Queue<Statement> getStatements() {

        // calculate the length and relative start of each block
        HexNum total = new HexNum();
        HexNum tmp;
        HexNum tmpStart;

        // for every block in this program,
        // get the starting address of the current block
        // add the total that was found so far
        // set the start of the block to the sum of total and starting address
        // Add the locctr of the block to the new total
        for (String currentBlock : this.startTable.keySet()) {
            tmpStart = this.getStart(currentBlock);
            tmpStart = tmpStart.add(total);
            this.setStart(currentBlock, tmpStart);
            tmp = this.getLocctr(currentBlock);
            total = total.add(tmp);

        }

        // now modify the value of each symbol to be relative to the start of the program
        // instead of relative to the start of their individual block
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

    /**
     * Retrieves the name, ensuring it is exactly six characters long.
     * If the name is empty, it defaults to "OUTPUT".
     *
     * @return A string representing the name, guaranteed to be six characters long.
     */
    public String getName() {
        // name needs to be exactly six characters long
        return Utility.lengthCheck(!this.name.isEmpty() ? this.name : "OUTPUT");
    }

    /**
     * Sets the name for this program.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the queue of external definitions.
     *
     * @return a Queue containing the external definitions.
     */
    public Queue<String> getExternalDefinitions() {
        return this.externalDefinitions;
    }

    /**
     * Retrieves the queue of external references.
     *
     * @return a Queue containing the external references.
     */
    public Queue<String> getExternalReferences() {
        return this.externalReferences;
    }

    /**
     * Retrieves the list of reference modifications.
     *
     * @return a list of strings representing the reference modifications.
     */
    public List<String> getReferenceModifications() {
        return this.referenceModifications;
    }

    /**
     * Sets the starting address for a given block in the start table.
     *
     * @param block the name of the block
     * @param start the starting address as a HexNum object
     */
    protected void setStart(String block, HexNum start) {
        this.startTable.put(block, start);
    }

    /**
     * Adds a given value to the location counter (locctr) of the current block.
     *
     * @param locctr the location counter to be added
     */
    protected void addLocctr(HexNum locctr) {
        this.addLocctr(this.block, locctr);
    }

    /**
     * Adds the specified location counter (locctr) value to the current location counter
     * for the given block in the locctrTable.
     *
     * @param block the block identifier for which the locctr is to be updated
     * @param locctr the HexNum value to be added to the current locctr of the specified block
     */
    protected void addLocctr(String block, HexNum locctr) {

        HexNum currentLocctr = this.locctrTable.get(block);
        currentLocctr = currentLocctr.add(locctr);
        this.locctrTable.put(block, currentLocctr);

    }

    /**
     * Loads the instruction set from a specified file and populates the instruction
     * and format tables.
     * <p>
     * The file should contain lines in the format: Mnemonic, Format, Opcode.
     * Each line is split into parts where the first part is the mnemonic, the second
     * part is the format, and the third part is the opcode.
     * <p>
     * The method reads the file from within the JAR using {@link Class#getResourceAsStream(String)}.
     * <p>
     * The format is mapped to an enum {@link Format} and stored in the format table.
     * The opcode is converted to a {@link HexNum} and stored in the instruction table.
     * <p>
     * If an unexpected format is encountered, it defaults to {@link Format#ASM} and logs a warning.
     * If the file cannot be found or read, it logs a warning with the filename.
     *
     * @param filename the name of the file containing the instruction set
     */
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

            try ( 
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

    /**
     * Loads the registers from the specified file and adds them to the register table.
     * The file should contain register names and their corresponding hexadecimal values
     * separated by whitespace on each line.
     * 
     * The file is expected to be in the format: Register letter, Hex Value
     * 
     * It is very similar in steps to loadInstructions
     * @see loadInstructions
     *
     * @param filename the name of the file containing the register data
     */
    protected final void loadRegisters(String filename) {
        // add all of the registers to the table

        InputStream file = getClass().getResourceAsStream(filename);

        try ( 
                Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split("\\s+");
                HexNum reg = new HexNum(parts[1], NumSystem.HEX);
                this.registerTable.put(parts[0], reg);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error: Could not find {}", filename);
            logger.warning(e.getMessage());
        }
    }

    /**
     * Splits an assembly statement into its constituent parts: mnemonic, arguments, and label.
     *
     * @param statement the assembly statement to be split
     * @return a String array containing the mnemonic, arguments, and label in that order
     * @throws InvalidAssemblyFileException if the statement has an invalid number of arguments
     */
    protected String[] splitStatement(String statement) throws InvalidAssemblyFileException {

        String[] parts = Utility.splitLine(statement);
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
                label = parts[0];
                mnemonic = parts[1];
                args = parts[2];
                handleLabels(label, mnemonic, args);
            }
            default -> // throw an exception if we have more than 3 arguments
                throw new InvalidAssemblyFileException(lineNum, "Invalid Number of Arguments");
        }

        // check for the '=' character meaning it is a literal value
        // and then handle the needed logic for it
        if (!args.isEmpty() && args.charAt(0) == '=') {
            args = args.substring(1);
            handleLiteral(args);
        }
        return new String[] { mnemonic, args, label };
    }

    /**
     * Handles the modification of an external reference in the assembly code.
     * If the part is an external reference, it sets the value to 0 and adds a
     * modification record because the value is not known at assembly time.
     * 
     * Format of Modification records:
     * Modification Records (Revised)
     * Col 1. M
     * Col 2-7. Starting address of the field to be modified, relative to beginning of control section
     * Col 8-9. Length of field to be modified in half-bytes
     * Col. 10. Modification flag (+ or -)
     * Col. 11-16. External symbol whose value is to be added or subtracted from the indicated field
     *
     * @param copyArgs the a copy of the provided arguments, which may include a sign
     *                 indicating addition or subtraction.
     * @param part     the label of the code that is an external reference.
     * @return the modified copyArgs with the external reference handled.
     */
    protected String handleModification(String copyArgs, String part) {
        // if the part is an external reference, we need to set the value to 0 and add a
        // modification record
        // this is because the value is not known at assembly time
        StringBuilder modification = new StringBuilder();
        modification.append("M");
        modification.append(this.getLocctr().toString(6));

        // we are appending the length of the modification, which is a word of len 6
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

    /**
     * Evaluates a mathematical expression represented as a string.
     * The expression can contain symbols that are either defined in the symbol table
     * or are external references. Symbols in the expression are replaced with their
     * corresponding values before evaluation.
     *
     * @param args The string representation of the mathematical expression to evaluate.
     * @return The evaluated result as a string if the expression is valid and contains
     *         more than one part; otherwise, returns the original string.
     */
    protected String evaluateExpression(String args) {
        Expression expression;
        ValidationResult expressionResults;
        int result;
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

        // build and validate the produced expression
        expression = new ExpressionBuilder(args).build();
        expressionResults = expression.validate();

        if (expressionResults.isValid()) {
            // note that we are type casting as int because we require a whole number
            result = (int) expression.evaluate();
            return Integer.toString(result);
        }
        return args;

    }

    /**
     * Handles the evaluation of an expression, replacing symbols with their decimal values,
     * and determining if the expression is absolute or relative. The result is stored in the
     * symbol table and returned as a HexNum object.
     * 
     * Note that it is very similar to evaluateExpression.  The main difference is that this detects
     * and handles the situation when it is an absolute expression.
     * 
     * 
     * @param label The label associated with the expression.
     * @param args The expression to be evaluated, potentially containing symbols and operators.
     * @return The evaluated expression as a HexNum object.
     */
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

        // add the symbol to the absoluteExpressions list if there are no hard-coded values
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

    /**
     * Handles the processing of labels in the assembly code.
     * 
     * This method sets the label to the current location if it is not already in the symbol table
     * and one of the following conditions is true:
     * 1) The mnemonic is not "EQU".
     * 2) The args is "*".
     * 
     * If the mnemonic is "EQU", it evaluates the expression in args and adds it to the symbol table.
     * If the label already exists in the symbol table, it throws an InvalidAssemblyFileException.
     * 
     * Additionally, if the mnemonic is "START", it sets the name to the label for later object code generation.
     * 
     * @param label The label to be processed.
     * @param mnemonic The mnemonic associated with the label.
     * @param args The arguments associated with the mnemonic.
     * @throws InvalidAssemblyFileException If a duplicate label is found or other assembly file errors occur.
     */
    protected void handleLabels(String label, String mnemonic, String args)
            throws InvalidAssemblyFileException {

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

    /**
     * Handles a literal in the assembly code.
     *
     * This method processes a literal by creating a DirectiveStatement, setting its directive
     * to the provided argument, and then handling it as a BYTE statement. The processed literal
     * is added to a queue to be handled later when LTORG is called or after the END directive.
     *
     * @param args the literal to be processed
     * @throws InvalidAssemblyFileException if the literal cannot be processed correctly
     */
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

    /**
     * Handles the BYTE directive in an assembly file.
     * 
     * @param args The argument string for the BYTE directive. It should start with either 'C' or 'X'.
     *             'C' represents a constant string whose length is the length of the string, and the object code is the ASCII value of each character in the string.
     *             'X' represents an object code whose length is 1, and the object code is the argument itself.
     * @param statement The DirectiveStatement object to be updated with the size and object code.
     * @throws InvalidAssemblyFileException If the argument is invalid or does not start with 'C' or 'X'.
     */
    protected void handleByte(String args, DirectiveStatement statement) throws InvalidAssemblyFileException {
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
                // set the size to the ceil of the length of args / 2
                // since each section is 4 bits
                HexNum length = new HexNum((int)Math.ceil(args.length() / 2));
                statement.setSize(length);
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

    /**
     * Assembles literals by processing each unique literal from the literals queue.
     * For each literal, it checks if the literal's directive is already present in the
     * symbol table. If not, it adds the directive to the symbol table, adds the statement
     * to the current list of statements, and updates the location counter.
     */
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

    /**
     * Handles assembly statements by processing the given mnemonic and arguments.
     * Depending on the mnemonic, it performs various operations such as setting
     * directives, evaluating expressions, managing location counters, and handling
     * literals.
     *
     * @param mnemonic The assembly mnemonic to be processed.
     * @param args     The arguments associated with the mnemonic.
     * @return A DirectiveStatement object containing the processed directive and
     *         its associated data.
     * @throws InvalidAssemblyFileException If the mnemonic is invalid or if there
     *                                      is an error in processing the statement.
     */
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
            case "BYTE" ->
                handleByte(args, returnVal);
            case "WORD" -> {
                // set size to 3 and set the object code
                // since the size of a word is 3 bytes
                returnVal.setSize(new HexNum(3));
                returnVal.setObjCode(new HexNum(args, NumSystem.DEC).toString(6));
            }
            case "RESB" -> 
                returnVal.setSize(new HexNum(args, NumSystem.DEC));
            case "RESW" -> 
                returnVal.setSize(new HexNum(3 * Integer.parseInt(args)));
            case "LTORG" -> assembleLiterals();
            case "EQU" -> {
                // we handle EQU in the handleLabels method
                // so this section just ensures that there is no error
            }
            case "USE" -> {

                // check if args is empty when blocks are switched back. If it is, set to
                // default block
                if (args.equals("")) {
                    args = DEFAULT_BLOCK;
                }

                this.locctrTable.putIfAbsent(args, new HexNum(0));
                this.startTable.putIfAbsent(args, new HexNum(0));

                // set the current block to the provided args
                this.block = args;
            }
            case "EXTDEF" -> {
                // find and add each externally defined label
                String[] defList = args.trim().split(",");
                for (String def : defList) {
                    def = Utility.lengthCheck(def);
                    this.externalDefinitions.add(def);
                }
            }
            case "EXTREF" -> {
                // find and add each externally referenced label
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
        return returnVal;
    }

    /**
     * Adds a statement to the list of statements.
     * If the provided statement is not null, it sets the block and control section
     * of the statement to the current block and name, respectively, and then adds
     * the statement to the list of statements.
     * 
     * Use this method to add new statements to the queue as it handles necessary data
     *
     * @param statement the statement to be added. If null, the method does nothing.
     */
    protected void addStatement(Statement statement) {
        if (statement != null) {
            statement.setBlock(this.block);
            statement.setControlSection(this.name);
            this.statements.add(statement);
        }
    }

    /**
     * Processes a given assembly statement.
     *
     * @param statement the assembly statement to be processed
     * @throws InvalidAssemblyFileException if the statement is invalid or cannot be processed
     * @throws ScriptException if there is an error in the script processing
     */
    public abstract void processStatement(String statement) throws InvalidAssemblyFileException, ScriptException;

}
