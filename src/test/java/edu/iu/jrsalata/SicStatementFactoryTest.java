package edu.iu.jrsalata;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;
import java.io.InputStream;
import org.junit.Test;

public class SicStatementFactoryTest {

    Logger logger = Logger.getLogger(getClass().getName());

    @Test
    public void testAsm1() {
        // read the input file and create a list of statements
        // clear the symtable
        SymTable.clear();
        ArrayList<Statement> statements = new ArrayList<Statement>();
        AbstractStatementFactory statementFactory = new SicStatementFactory();
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
                    assertEquals(line.toUpperCase(), statement.assemble().toUpperCase());
                }
            }
            sc.close();

        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }
}
