package edu.iu.jrsalata;

import java.util.HashMap;
import java.util.Set;

/**
 * The SymTable class provides a static symbol table, block table, and macro table
 * that can be used across other classes. It includes methods to add,
 * retrieve, and check symbols, blocks, and macros, as well as to clear the tables.
 * The symbol and block tables are organized by control sections.
 */
public class SymTable {

    /**
     * The maximum length allowed for labels and other constants
     * For SIC/XE, that length is 6
     */
    public static final int MAX_LEN = 6;

    /**
     * Contains a separate symbolTable for each program block
     * control section maps to a hashmap, which is then mapped to the symbol and its location as a HexNum
     */
    private static HashMap<String, HashMap<String, HexNum>> symbolTable;

    /**
     * Contains a separate blockTable for each program block
     * control section maps to a hashmap, which is then mapped to the symbol name and its block
     */ 
    private static HashMap<String, HashMap<String, String>> blockTable;

    /**
     * Contains a separate macroTable for an entire program
     * String maps to a MacroProcessorInterface, which contains the definition of the given macro
     */
    private static HashMap<String, MacroProcessorInterface> macroTable;

    /**
     * Private constructor for the SymTable class.
     * This constructor prevents the instantiation of the SymTable class since it is static
     */
    private SymTable() {
    }

    /**
     * Adds a symbol to the symbol table with the specified location, block, and control section.
     * If the control section does not exist, it will be created.
     *
     * @param symbol the symbol to be added
     * @param location the location of the symbol as a HexNum
     * @param block the program block associated with the symbol
     * @param controlSection the control section to which the symbol belongs
     */
    public static void addSymbol(String symbol, HexNum location, String block, String controlSection) {
        createIfNotExists(controlSection);
        symbol = Utility.lengthCheck(symbol);
        symbolTable.get(controlSection).put(symbol, location);
        SymTable.addBlock(symbol, block, controlSection);
    }

    /**
     * Adds a block to the block table for a given control section.
     * If the control section does not exist, it will be created.
     *
     * @param symbol the symbol to be added to the block table
     * @param block the block associated with the symbol
     * @param controlSection the control section where the block will be added
     */
    public static void addBlock(String symbol, String block, String controlSection) {
        createIfNotExists(controlSection);
        symbol = Utility.lengthCheck(symbol);
        blockTable.get(controlSection).put(symbol, block);
    }

    /**
     * Adds a macro to the macro table.
     *
     * @param name the name of the macro to be added
     * @param processor the macro processor interface associated with the macro
     */
    public static void addMacro(String name, MacroProcessorInterface processor) {
        createIfNotExists();
        macroTable.put(name, processor);
    }

    /**
     * Retrieves a macro from the macro table by its name.
     *
     * @param name the name of the macro to retrieve
     * @return the macro associated with the given name, or null if no such macro exists
     */
    public static MacroProcessorInterface getMacro(String name) {
        createIfNotExists();
        return macroTable.get(name);
    }

    /**
     * Retrieves the set of keys from the macro table.
     * 
     * @return a Set containing all the keys in the macro table.
     */
    public static Set<String> getMacroKeys() {
        createIfNotExists();
        return macroTable.keySet();
    }

    /**
     * Retrieves the HexNum associated with the given symbol in the specified control section.
     * If the control section does not exist, it will be created.
     *
     * @param symbol the symbol to look up
     * @param controlSection the control section where the symbol is defined
     * @return the hexadecimal number associated with the symbol, or null if the symbol is not found
     */
    public static HexNum getSymbol(String symbol, String controlSection) {
        createIfNotExists(controlSection);
        symbol = Utility.lengthCheck(symbol);
        return symbolTable.get(controlSection).get(symbol);
    }

    /**
     * Retrieves the set of keys from the symbol table for the specified control section.
     * If the control section does not exist, it will be created.
     *
     * @param controlSection the control section whose keys are to be retrieved
     * @return a set of keys from the symbol table for the specified control section
     */
    public static Set<String> getKeys(String controlSection) {
        createIfNotExists(controlSection);
        return symbolTable.get(controlSection).keySet();
    }

    /**
     * Retrieves the block associated with the given symbol and control section.
     * If the control section does not exist, it will be created.
     *
     * @param symbol the symbol whose block is to be retrieved
     * @param controlSection the control section in which to look for the symbol
     * @return the block associated with the given symbol and control section
     */
    public static String getBlock(String symbol, String controlSection) {
        createIfNotExists(controlSection);
        symbol = Utility.lengthCheck(symbol);
        return blockTable.get(controlSection).get(symbol);
    }

    /**
     * Checks if the specified symbol exists in the symbol table for the given control section.
     *
     * @param symbol the symbol to check for existence
     * @param controlSection the control section in which to check for the symbol
     * @return true if the symbol exists in the specified control section, false otherwise
     */
    public static boolean containsSymbol(String symbol, String controlSection) {
        createIfNotExists(controlSection);
        symbol = Utility.lengthCheck(symbol);
        return symbolTable.get(controlSection).containsKey(symbol);
    }

    /**
     * Checks if the block table contains the specified symbol within the given control section.
     *
     * @param symbol the symbol to check for in the block table
     * @param controlSection the control section in which to check for the symbol
     * @return true if the block table contains the symbol within the specified control section, false otherwise
     */
    public static boolean containsBlock(String symbol, String controlSection) {
        createIfNotExists(controlSection);
        symbol = Utility.lengthCheck(symbol);
        return blockTable.get(controlSection).containsKey(symbol);
    }

    /**
     * Clears all entries from the symbol table, block table, and macro table.
     * This method ensures that the tables are created if they do not already exist
     * before attempting to clear them.
     */
    public static void clear() {
        createIfNotExists();
        symbolTable.clear();
        blockTable.clear();
        macroTable.clear();
    }

    /**
     * Initializes the symbolTable, blockTable, and macroTable if they are not already initialized.
     * This method checks if each of the tables is null, and if so, creates a new HashMap for each.
     */
    private static void createIfNotExists() {
        if (symbolTable == null) {
            symbolTable = new HashMap<>();
        }
        if (blockTable == null) {
            blockTable = new HashMap<>();
        }
        if (macroTable == null) {
            macroTable = new HashMap<>();
        }
    }

    /**
     * Ensures that the specified control section exists in the symbol and block tables.
     * If the control section does not exist, it is created and initialized with empty maps.
     *
     * @param controlSection the control section to check and create if it does not exist
     */
    private static void createIfNotExists(String controlSection) {
        createIfNotExists();
        symbolTable.putIfAbsent(controlSection, new HashMap<>());
        blockTable.putIfAbsent(controlSection, new HashMap<>());
    }
}
