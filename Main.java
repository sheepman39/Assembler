import java.lang.reflect.Array;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;

class Main {
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

            // read the file
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {

                // get the current line we are working with
                String line = sc.nextLine();

                // strip the whitespace
                line = line.strip();

                // find the comment character
                // since there is the possibility of no comment existing, check if the comment character exists
                // if not, then set it to the length of the string
                int period = line.indexOf('.') == -1 ? line.length() : line.indexOf('.');
                line = line.substring(0, period).strip();

                // now we are going to split the string up into the different parts based on space or tabs
                String[] parts = line.split("\\s+");
                Statement curLine = new Statement();
                
                // TODO: Fix this from the restructure.  Should be a part of factory now
                // the number of arguments determines the position of each part
                if(parts.length == 3){
                    curLine.label = parts[0];
                    curLine.keyword = parts[1];
                    curLine.argument = parts[2];
                } else if(parts.length == 2) {
                    curLine.label = null;
                    curLine.keyword = parts[0];
                    curLine.argument = parts[1];
                } else if(parts.length == 1) {
                    curLine.label = null;
                    curLine.keyword = parts[0];
                    curLine.argument = null;
                } else {
                    // throw an exception
                    System.out.println("Error: Invalid number of arguments");
                }
                // append the curLine to the arraylist
                list.add(curLine);
            }
        } catch (Exception e) {
            System.out.println("File not found");
            System.err.println(e);
        }

        return list;
    }
}