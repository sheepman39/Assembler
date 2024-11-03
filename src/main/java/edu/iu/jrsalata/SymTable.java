// Class: OpTable
// This class handles the Symbol Table across the entire program

package edu.iu.jrsalata;

import java.util.HashMap;

public class SymTable {

    // static symbol table can be used across each instance of the class
    private static HashMap<String, HexNum> symbolTable;

    // constructor
    public SymTable() {
        createIfNotExists();
    }

    public static void addSymbol(String symbol, HexNum location) {
        createIfNotExists();
        symbolTable.put(symbol, location);
    }

    public static HexNum getSymbol(String symbol) {
        createIfNotExists();
        return symbolTable.get(symbol);
    }

    public static boolean containsSymbol(String symbol) {
        createIfNotExists();
        return symbolTable.containsKey(symbol);
    }

    private static void createIfNotExists() {
        if(symbolTable == null) {
            symbolTable = new HashMap<String, HexNum>();
        }
    }
}
