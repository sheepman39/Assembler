package edu.iu.jrsalata;

import static org.junit.Assert.assertEquals;

import java.util.Scanner;
import java.util.logging.Logger;
import java.io.InputStream;
import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.Assert.fail;

// credit to this medium article for helping write paramterized tests:
// https://medium.com/bliblidotcom-techblog/junit-4-reducing-test-code-duplication-using-parameterized-4d20ecec6f8a
@RunWith(Parameterized.class)
public class StatementBuilderTest {
    Logger logger = Logger.getLogger(getClass().getName());
    String assemblyFile;
    String objectFile;

    // constructor to help with parameterization
    public StatementBuilderTest(String assemblyFile, String objectFile) {
        this.assemblyFile = assemblyFile;
        this.objectFile = objectFile;
    }

    @Test
    public void testAsm() {
        // read the input file and create a list of statements
        // clear the symtable
        SymTable.clear();
        Queue<Statement> statements = new LinkedList<>();
        StatementBuildler statementFactory = new StatementBuildler();
        try {
            InputStream file = getClass().getResourceAsStream(assemblyFile);

            // read the file
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                statementFactory.processStatement(line);
            }
            statements = statementFactory.getStatements();
            // close the file
            sc.close();

            // read the object code file and compare assembled results
            file = getClass().getResourceAsStream(objectFile);
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
            logger.severe(e.getStackTrace().toString());
            fail("FATAL ERROR");
        }
    }

    @Parameterized.Parameters()
    public static Collection<String[]> files() {
        return Arrays.asList(new String[][] {
                { "/testAsm2.asm", "/testAsm2.txt" },
                { "/testAsm3.asm", "/testAsm3.txt" },
        });
    }
}
