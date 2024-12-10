package edu.iu.jrsalata;

/**
 * Utility class provides static methods for string manipulation and processing.
 * This class is not meant to be instantiated.
 */
public class Utility {

    /**
     * Ensures the given symbol string is exactly SymTable.MAX_LEN characters long.
     * If the symbol is longer, it is truncated. If it is shorter, it is padded with spaces.
     * The resulting string is converted to uppercase.
     *
     * @param symbol the input string to be checked and adjusted
     * @return the adjusted string of length SymTable.MAX_LEN in uppercase
     */
    public static String lengthCheck(String symbol) {
        symbol = symbol.replace("\t", " ").trim();
        if (symbol.equals("")) {
            return symbol;
        } else if (symbol.length() > SymTable.MAX_LEN) {
            return symbol.substring(0, SymTable.MAX_LEN).toUpperCase();
        } else if (symbol.length() < SymTable.MAX_LEN) {
            StringBuilder sb = new StringBuilder(symbol);
            for (int i = 0; i < SymTable.MAX_LEN - symbol.length(); i++) {
                sb.append(" ");
            }
            return sb.toString().toUpperCase();
        } else {
            return symbol.toUpperCase();
        }
    }

    /**
     * Cleans the given line by stripping unnecessary whitespace and removing comments.
     * If the line is empty or is just a comment, an empty string is returned.
     *
     * @param line the input string to be cleaned
     * @return the cleaned string without comments and unnecessary whitespace
     */
    public static String cleanLine(String line) {
        // First strip any unnecessary whitespace
        line = line.strip();

        // if the line is empty or is just a comment, return null
        if (line.equals("") || line.charAt(0) == '.') {
            return "";
        }
        // find the comment character
        // since there is the possibility of no comment existing, check if the comment
        // character exists
        // if not, then set it to the length of the string
        int period = line.indexOf('.') == -1 ? line.length() : line.indexOf('.');
        return line.substring(0, period).strip();

    }

    /**
     * Splits the given line into parts based on spaces after cleaning it.
     * If the line is empty or is just a comment, an array with two empty strings is returned.
     *
     * @param line the input string to be split
     * @return an array of strings split by spaces or tabs
     */
    public static String[] splitLine(String line) {

        // clean up the line with the above method
        line = cleanLine(line);

        // if the line is empty or is just a comment, return null
        if (line.equals("") || line.charAt(0) == '.') {
            return new String[] { "", "" };
        }

        // now we are going to split the string up into the different parts based on
        // space or tabs
        return line.split("\\s+");
    }

}
