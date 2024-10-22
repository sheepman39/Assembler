import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.util.HashMap;

class Main {
    public static final HashMap<String, HexNum> symbolTable = new HashMap<String, HexNum>();

    public static void main(String[] args) {
        System.out.println("Hello World");
        ArrayList<Statement> list = fileInput("input.asm");
        // print out every item in list
        for (Statement l : list) {
            System.out.println(l.toString());
        }
    }

    public static ArrayList<Statement> fileInput(String filename) {

        // create the ArrayList that will be returned
        ArrayList<Statement> list = new ArrayList<Statement>();

        // open up a new file and read the string
        // parse the string and create a list of lines
        try {
            // open the file
            File file = new File(filename);

            // TODO: update this with the latest structure
            // read the file
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {

            }
            sc.close();
        } catch (Exception e) {
            System.out.println("File not found");
            System.err.println(e);
        }

        return list;
    }
}