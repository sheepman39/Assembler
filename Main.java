import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.util.HashMap;

class Main {
    public static final HashMap<String, HexNum> symbolTable = new HashMap<String, HexNum>();

    public static void main(String[] args) {
        ArrayList<Statement> list = fileInput("input.asm");
    }

    public static ArrayList<Statement> fileInput(String filename) {

        // create the ArrayList that will be returned
        ArrayList<Statement> list = new ArrayList<Statement>();

        // Create an instance of the StatementFactory
        StatementFactoryInterface factory = new StatementFactory();

        // open up a new file and read the string
        // parse the string and create a list of lines
        try {
            // open the file
            File file = new File(filename);

            // read the file
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                Statement statement = factory.processStatement(line);
                if (statement != null) {
                    list.add(statement);
                }
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("File not found");
            System.err.println(e);
        }

        return list;
    }
}