package edu.iu.jrsalata;

import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.util.HashMap;
import java.util.logging.Logger;

class Main {
    static Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        ArrayList<Statement> list = fileInput("input.asm");
        for (Statement statement : list) {
            logger.info(statement.assemble());
        }
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