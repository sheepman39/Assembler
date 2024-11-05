package edu.iu.jrsalata;

import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.util.HashMap;
import java.util.logging.Logger;

class Main {
    static Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        // Create an instance of the StatementFactory
        StatementFactoryInterface factory = new StatementFactory();
        ArrayList<Statement> list = fileInput("input.asm", factory);
        
        try{
            // Create a file writter to be passed around to write each section of the obj file
            FileWritter fileWritter = new FileWritter("output.obj");
            writeHeaderRecord(fileWritter, factory);
        }
        
    }

    // Write the Header Record to the given obj file
    public static void writeHeaderRecord(FileWritter fileWritter, StatementFactoryInterface factory){
        // Create the StringBuilder that will add each component
        // Start with the 'H'
        StringBuilder headerRecord = new StringBuilder();
        headerRecord.append("H");

        // Col 2-7 is program name
        headerRecord.append(factory.getName());

        // Col 8-13 is the starting address
        headerRecord.append(factory.getStart().toString(6));

        // Col 14-19 is the length of the program
        headerRecord.append(factory.getLen().toString(6));

        // write the final string to the header file
        fileWritter.writeHeaderRecord(headerRecord.toString());
    }

    // Write the Text Record to the given obj file
    public static void writeTextRecords(FileWriter fileWriter, ArrayList<Statement> list){
        // Create the StringBuilder that will add each component
        // Start with the 'T'
        StringBuilder textRecord = new StringBuilder();
        textRecord.append("T");

        // Col 2-7 is the starting address
    }
    public static ArrayList<Statement> fileInput(String filename, StatementFactoryInterface factory) {

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