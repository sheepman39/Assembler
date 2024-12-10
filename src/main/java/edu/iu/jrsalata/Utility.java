package edu.iu.jrsalata;

public class Utility {

    public static String lengthCheck(String symbol) {
        // since many different strings need to be exactly n characters long,
        // this function will set them to be n chars long
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
