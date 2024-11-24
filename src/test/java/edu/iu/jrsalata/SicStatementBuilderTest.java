package edu.iu.jrsalata;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Logger;
import java.io.InputStream;
import org.junit.Test;

public class SicStatementBuilderTest {

    Logger logger = Logger.getLogger(getClass().getName());

    @Test
    public void testAsm1() {
        // read the input file and create a list of statements
        // clear the symtable
        SymTable.clear();
        Queue<Statement> statements = new LinkedList<Statement>();
        AbstractStatementBuilder statementBuilder = new SicStatementBuilder();
        try {
            InputStream file = getClass().getResourceAsStream("/testAsm1.asm");

            // read the file
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                statementBuilder.processStatement(line);
            }
            statements = statementBuilder.getStatements();
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