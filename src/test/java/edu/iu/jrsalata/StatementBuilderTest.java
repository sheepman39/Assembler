package edu.iu.jrsalata;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

// credit to this medium article for helping write paramterized tests:
// https://medium.com/bliblidotcom-techblog/junit-4-reducing-test-code-duplication-using-parameterized-4d20ecec6f8a
@RunWith(Parameterized.class)
public class StatementBuilderTest {
    static final Logger LOGGER = Logger.getLogger(StatementBuilderTest.class.getName());
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
        Queue<Statement> statements;
        StatementBuildler statementFactory = new StatementBuildler();
        try {
            InputStream file = getClass().getResourceAsStream(assemblyFile);

            // read the file
            Scanner sc = new Scanner(file);
            Queue<AbstractStatementBuilder> queue = fileInput(sc, statementFactory);

            // read the object code file and compare assembled results
            file = getClass().getResourceAsStream(objectFile);
            sc = new Scanner(file);
            while(!queue.isEmpty()){
                AbstractStatementBuilder builder = queue.poll();
                statements = builder.getStatements();
                for (Statement statement : statements) {
                    if (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        assertEquals(line.toUpperCase(), statement.assemble().toUpperCase());
                    }
                }
            }
            sc.close();

        } catch (InvalidAssemblyFileException e) {
            LOGGER.severe(e.getMessage());
            LOGGER.severe(Arrays.toString(e.getStackTrace()));
            fail("FATAL ERROR");
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            LOGGER.severe(Arrays.toString(e.getStackTrace()));
            fail("FATAL ERROR");
        }
    }

    @Parameterized.Parameters()
    public static Collection<String[]> files() {
        return Arrays.asList(new String[][] {
                { "/testAsm2.asm", "/testAsm2.txt" },
                { "/testAsm3.asm", "/testAsm3.txt" },
                { "/testAsm4.asm", "/testAsm4.txt" },
                { "/testAsm5.asm", "/testAsm5.txt" }
        });
    }

public static Queue<AbstractStatementBuilder> fileInput(Scanner sc, AbstractStatementBuilder builder)
            throws InvalidAssemblyFileException, Exception {
        
        Queue<AbstractStatementBuilder> queue = new LinkedList<>();

        // since we want to be able to keep the type of builder consistent, check if the builder passed is an instance of the SIC builder
        boolean isSIC = builder instanceof SicStatementBuilder;

        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            // check if we are at the beginning of a control section
            // in order to create a new builder to handle it
            if (line.contains("CSECT")){
                queue.add(builder);
                builder = isSIC ? new SicStatementBuilder() : new StatementBuildler();

                // handle setting the new name of the builder
                String[] parts = line.split(" ");
                builder.setName(parts[0]);
                continue;
            }
            builder.processStatement(line);
        }
        queue.add(builder);
        return queue;
    }
}
