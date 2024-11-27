// Class: OpTable
// This class handles the Symbol Table across the entire program

package edu.iu.jrsalata;

import java.util.HashMap;
import java.util.Set;

public class SymTable {

    // static symbol table can be used across each instance of the class
    private static HashMap<String, HexNum> symbolTable;
    private static HashMap<String, String> blockTable;

    // constructor
    // private because we don't need to instantiate this class
    private SymTable() {
    }

    public static void addSymbol(String symbol, HexNum location) {
        createIfNotExists();
        symbolTable.put(symbol, location);
    }

    public static void addSymbol(String symbol, HexNum location, String block) {
        createIfNotExists();
        symbolTable.put(symbol, location);
        SymTable.addBlock(symbol, block);
    }

    public static void addBlock(String symbol, String block) {
        createIfNotExists();
        blockTable.put(symbol, block);
    }

    public static HexNum getSymbol(String symbol) {
        createIfNotExists();
        return symbolTable.get(symbol);
    }

    public static Set<String> getKeys() {
        createIfNotExists();
        return symbolTable.keySet();
    }

    public static String getBlock(String symbol) {
        createIfNotExists();
        return blockTable.get(symbol);
    }

    public static boolean containsSymbol(String symbol) {
        createIfNotExists();
        return symbolTable.containsKey(symbol);
    }

    public static boolean containsBlock(String symbol) {
        createIfNotExists();
        return blockTable.containsKey(symbol);
    }

    public static void clear() {
        createIfNotExists();
        symbolTable.clear();
        blockTable.clear();
    }

    private static void createIfNotExists() {
        if (symbolTable == null) {
            symbolTable = new HashMap<>();
        }
        if (blockTable == null) {
            blockTable = new HashMap<>();
        }
    }
}
