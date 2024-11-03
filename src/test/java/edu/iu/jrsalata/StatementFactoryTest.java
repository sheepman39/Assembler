package edu.iu.jrsalata;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.InputStream;
import org.junit.Test;

public class StatementFactoryTest {

    @Test
    public void testAsm1() {
        // read the input file and create a list of statements
        ArrayList<Statement> statements = new ArrayList<Statement>();
        StatementFactory statementFactory = new StatementFactory();
        try {
            InputStream file = getClass().getResourceAsStream("/testAsm1.asm");

            // read the file
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                Statement statement = statementFactory.processStatement(line);
                statements.add(statement);
            }

            // close the file
            sc.close();

            // read the object code file and compare assembled results
            file = getClass().getResourceAsStream("/testAsm1.txt");
            sc = new Scanner(file);
            for (Statement statement : statements) {
                if (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    System.out.println(line + " " + statement.assemble());
                }
            }
            sc.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
