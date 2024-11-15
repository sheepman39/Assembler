package edu.iu.jrsalata;

import java.util.LinkedList;
import java.util.Queue;
import java.io.File;
import java.util.Scanner;
import java.util.logging.Logger;

class Main {
    static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Create an instance of the StatementFactory
        StatementFactoryInterface factory = new StatementFactory();
        Queue<Statement> queue = fileInput("input.asm", factory);
        String fileName = "output.obj";
        ObjectWriterInterface writer = new ObjectWriter(fileName, factory, queue);

        writer.execute();
    }

    public static Queue<Statement> fileInput(String filename, StatementFactoryInterface factory) {

        // create the ArrayList that will be returned
        Queue<Statement> queue = new LinkedList<>();

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
        } catch (InvalidAssemblyFileException e){

            // inforamtive error message from our StatementFactory
            logger.severe("===========");
            logger.severe("ASSEMBLY FAILURE!!! Shutting down....");
            logger.severe(e.getMessage());
            logger.severe("===========");

            // exit the program
            System.exit(1);
        }catch (Exception e) {
            logger.severe(e.getMessage());
            logger.severe(e.toString());
        }

        return queue;
    }
}