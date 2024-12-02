// Class: OpTable
// This class handles the Symbol Table across the entire program

package edu.iu.jrsalata;

import java.util.HashMap;
import java.util.Set;

public class SymTable {

    // static symbol table can be used across each instance of the class
    private static HashMap<String, HashMap<String, HexNum>> symbolTable;
    private static HashMap<String, HashMap<String, String>> blockTable;
    // constructor
    // private because we don't need to instantiate this class
    private SymTable() {
    }


    public static void addSymbol(String symbol, HexNum location, String block, String controlSection) {
        createIfNotExists(controlSection);
        symbol = lengthCheck(symbol);
        symbolTable.get(controlSection).put(symbol, location);
        SymTable.addBlock(symbol, block, controlSection);
    }

    public static void addBlock(String symbol, String block, String controlSection) {
        createIfNotExists(controlSection);
        symbol = lengthCheck(symbol);
        blockTable.get(controlSection).put(symbol, block);
    }

    public static HexNum getSymbol(String symbol, String controlSection) {
        createIfNotExists(controlSection);
        symbol = lengthCheck(symbol);
        return symbolTable.get(controlSection).get(symbol);
    }

    public static Set<String> getKeys(String controlSection) {
        createIfNotExists(controlSection);
        return symbolTable.get(controlSection).keySet();
    }

    public static String getBlock(String symbol, String controlSection) {
        createIfNotExists(controlSection);
        symbol = lengthCheck(symbol);
        return blockTable.get(controlSection).get(symbol);
    }

    public static boolean containsSymbol(String symbol, String controlSection) {
        createIfNotExists(controlSection);
        symbol = lengthCheck(symbol);
        return symbolTable.get(controlSection).containsKey(symbol);
    }

    public static boolean containsBlock(String symbol, String controlSection) {
        createIfNotExists(controlSection);
        symbol = lengthCheck(symbol);
        return blockTable.get(controlSection).containsKey(symbol);
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

    private static void createIfNotExists(String controlSection) {
        if (symbolTable == null) {
            symbolTable = new HashMap<>();
        }
        if (blockTable == null) {
            blockTable = new HashMap<>();
        }
        symbolTable.putIfAbsent(controlSection, new HashMap<>());
        blockTable.putIfAbsent(controlSection, new HashMap<>());
    }

    private static String lengthCheck(String symbol) {
        int max = 6;
                // since many different strings need to be exactly n characters long,
        // this function will set them to be n chars long
        if (symbol.equals("")) {
            return symbol;
        } else if (symbol.length() > max) {
            return symbol.substring(0, max).toUpperCase();
        } else if (symbol.length() < max) {
            StringBuilder sb = new StringBuilder(symbol);
            for (int i = 0; i < max - symbol.length(); i++) {
                sb.append(" ");
            }
            return sb.toString().toUpperCase();
        } else {
            return symbol.toUpperCase();
        }
    }
}
