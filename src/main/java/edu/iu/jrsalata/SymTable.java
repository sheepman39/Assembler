// Class: OpTable
// This class handles the Symbol Table across the entire program

package edu.iu.jrsalata;

import java.util.HashMap;
import java.util.Set;

public class SymTable {
    public static final int MAX_LEN = 6;
    // static symbol table can be used across each instance of the class
    private static HashMap<String, HashMap<String, HexNum>> symbolTable;
    private static HashMap<String, HashMap<String, String>> blockTable;
    private static HashMap<String, MacroProcessorInterface> macroTable;

    // constructor
    // private because we don't need to instantiate this class
    private SymTable() {
    }

    public static void addSymbol(String symbol, HexNum location, String block, String controlSection) {
        createIfNotExists(controlSection);
        symbol = Utility.lengthCheck(symbol);
        symbolTable.get(controlSection).put(symbol, location);
        SymTable.addBlock(symbol, block, controlSection);
    }

    public static void addBlock(String symbol, String block, String controlSection) {
        createIfNotExists(controlSection);
        symbol = Utility.lengthCheck(symbol);
        blockTable.get(controlSection).put(symbol, block);
    }

    public static void addMacro(String name, MacroProcessorInterface processor){
        createIfNotExists();
        macroTable.put(name, processor);
    }

    public static MacroProcessorInterface getMacro(String name){
        createIfNotExists();
        return macroTable.get(name);
    }

    public static Set<String> getMacroKeys(){
        createIfNotExists();
        return macroTable.keySet();
    }

    public static HexNum getSymbol(String symbol, String controlSection) {
        createIfNotExists(controlSection);
        symbol = Utility.lengthCheck(symbol);
        return symbolTable.get(controlSection).get(symbol);
    }

    public static Set<String> getKeys(String controlSection) {
        createIfNotExists(controlSection);
        return symbolTable.get(controlSection).keySet();
    }

    public static String getBlock(String symbol, String controlSection) {
        createIfNotExists(controlSection);
        symbol = Utility.lengthCheck(symbol);
        return blockTable.get(controlSection).get(symbol);
    }

    public static boolean containsSymbol(String symbol, String controlSection) {
        createIfNotExists(controlSection);
        symbol = Utility.lengthCheck(symbol);
        return symbolTable.get(controlSection).containsKey(symbol);
    }

    public static boolean containsBlock(String symbol, String controlSection) {
        createIfNotExists(controlSection);
        symbol = Utility.lengthCheck(symbol);
        return blockTable.get(controlSection).containsKey(symbol);
    }

    public static void clear() {
        createIfNotExists();
        symbolTable.clear();
        blockTable.clear();
        macroTable.clear();
    }

    private static void createIfNotExists() {
        if (symbolTable == null) {
            symbolTable = new HashMap<>();
        }
        if (blockTable == null) {
            blockTable = new HashMap<>();
        }
        if (macroTable == null){
            macroTable = new HashMap<>();
        }
    }

    private static void createIfNotExists(String controlSection) {
        createIfNotExists();
        symbolTable.putIfAbsent(controlSection, new HashMap<>());
        blockTable.putIfAbsent(controlSection, new HashMap<>());
    }
}
