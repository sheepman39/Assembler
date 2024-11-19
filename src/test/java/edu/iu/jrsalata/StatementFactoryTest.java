package edu.iu.jrsalata;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;
import java.io.InputStream;
import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.fail;

// credit to this medium article for helping write paramterized tests:
// https://medium.com/bliblidotcom-techblog/junit-4-reducing-test-code-duplication-using-parameterized-4d20ecec6f8a
@RunWith(Parameterized.class)
public class StatementFactoryTest {
    Logger logger = Logger.getLogger(getClass().getName());
    String assemblyFile;
    String objectFile;

    // constructor to help with parameterization
    public StatementFactoryTest(String assemblyFile, String objectFile) {
        this.assemblyFile = assemblyFile;
        this.objectFile = objectFile;
    }

    @Test
    public void testAsm() {
        // read the input file and create a list of statements
        // clear the symtable
        SymTable.clear();
        ArrayList<Statement> statements = new ArrayList<Statement>();
        StatementFactory statementFactory = new StatementFactory();
        try {
            InputStream file = getClass().getResourceAsStream(assemblyFile);

            // read the file
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                Statement statement = statementFactory.processStatement(line);
                if (statement != null) {
                    statements.add(statement);
                }
            }

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
    public static Collection files() {
        return Arrays.asList(new Object[][] {
            {"/testAsm2.asm", "/testAsm2.txt"},
            {"/testAsm3.asm", "/testAsm3.txt"},
        });
    }
}
