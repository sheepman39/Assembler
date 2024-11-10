package edu.iu.jrsalata;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.io.FileWriter;
import java.io.File;
import java.util.Scanner;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

class Main {
    static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Create an instance of the StatementFactory
        StatementFactoryInterface factory = new StatementFactory();
        Queue<Statement> queue = fileInput("input.asm", factory);

        try {
            // Create a file writter to be passed around to write each section of the obj
            // file
            FileWriter fileWriter = new FileWriter("output.obj");
            writeHeaderRecord(fileWriter, factory);
            writeTextRecords(fileWriter, queue, factory);
            writeEndRecord(fileWriter, factory);

            fileWriter.close();
            logger.info("File written successfully");
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }

    }

    // Write the Header Record to the given obj file
    public static void writeHeaderRecord(FileWriter fileWriter, StatementFactoryInterface factory) {
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

        try {
            // write the final string to the header file
            fileWriter.write(headerRecord.toString());
            fileWriter.write('\n');
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    // Write the Text Record to the given obj file
    public static void writeTextRecords(FileWriter fileWriter, Queue<Statement> queue, StatementFactoryInterface factory) {

        // store the start to handle sizes
        HexNum start = new HexNum(factory.getStart().getDec());
        int tmpSize = 0;

        // Create the StringBuilder that will add each component
        StringBuilder textRecord = new StringBuilder();

        // Statement that will be read from the queue
        Statement statement;

        while (!queue.isEmpty()) {
            // Col 1 is "T"
            textRecord.append("T");

            // Col 2-7 is the starting address
            textRecord.append(start.toString(6));

            // Col 8-9 is the length of the record
            // We will put a placeholder here for now
            textRecord.append("--");

            // Col 10-69 is the text record
            int test = queue.peek().getSize().getDec();
            logger.info("Text Record Length: " + test);
            logger.info("Record Assemble: " + queue.peek().assemble());
            if(queue.peek() instanceof DirectiveStatement) {
                logger.info("Directive Keyword:" + ((DirectiveStatement) queue.peek()).getDirective());
            }

            tmpSize = textRecord.length();
            while(!queue.isEmpty() && (tmpSize + queue.peek().assemble().length()  < 70)) {
                statement = queue.poll();
                textRecord.append(statement.assemble());
                start = start.add(statement.getSize());
                tmpSize = tmpSize + statement.getSize().getDec() * 2;
            }

            // Update the length of the record
            // since it is needed in bytes, we need to divide by 2 and round up
            int length = textRecord.length() - 9;
            length = (int)Math.ceil(length / 2.0);
            HexNum size = new HexNum(length);
            textRecord.replace(7, 9, size.toString(2));

            // Add the text record to the file
            try{
                fileWriter.write(textRecord.toString());
                fileWriter.write('\n');
            } catch (Exception e) {
                logger.severe(e.getMessage());
            }

            // Clear the text record
            textRecord.setLength(0);
        }

    }

    // Write the End Record to the given obj file
    public static void writeEndRecord(FileWriter fileWriter, StatementFactoryInterface factory) {
        
        // Create the StringBuilder that will add each component
        StringBuilder endRecord = new StringBuilder();

        // Col 1 is "E"
        endRecord.append("E");

        // Col 2-7 is the starting address
        endRecord.append(factory.getStart().toString(6));

        try {
            // write the final string to the header file
            fileWriter.write(endRecord.toString());
            fileWriter.write('\n');
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }
    public static Queue<Statement> fileInput(String filename, StatementFactoryInterface factory) {

        // create the ArrayList that will be returned
        Queue<Statement> queue = new LinkedList<Statement>();

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
                    queue.add(statement);
                }
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("File not found");
            System.err.println(e);
        }

        return queue;
    }
}