import java.lang.reflect.Array;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        System.out.println("Hello World");
        ArrayList<Line> list = fileInput("input.asm");
    }

    public static ArrayList<Line> fileInput(String filename) {
        // open up a new file and read the string
        // parse the string and create a list of lines
        try {
            File file = new File(filename);
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("File not found");
        }

        ArrayList<Line> list = new ArrayList<Line>();
        return list;
    }
}