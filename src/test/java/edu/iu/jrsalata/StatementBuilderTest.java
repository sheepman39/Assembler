package edu.iu.jrsalata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.script.ScriptException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

// credit to this medium article for helping write parametrized tests:
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
        try {
            InputStream file = getClass().getResourceAsStream(assemblyFile);

            AbstractStatementBuilderBuilderInterface builderBuilder = new AbstractStatementBuilderBuilder();
            builderBuilder.setInputFile(assemblyFile);
            builderBuilder.execute(file);

            Queue<AbstractStatementBuilder> queue = builderBuilder.getBuilders();

            // read the object code file and compare assembled results
            file = getClass().getResourceAsStream(objectFile);
            try (Scanner sc = new Scanner(file)) {
                while (!queue.isEmpty()) {
                    AbstractStatementBuilder builder = queue.poll();
                    statements = builder.getStatements();
                    for (Statement statement : statements) {
                        if (sc.hasNextLine()) {
                            String line = sc.nextLine();
                            assertEquals(line.toUpperCase(), statement.assemble().toUpperCase());
                        }
                    }
                }
            }

        } catch (InvalidAssemblyFileException e) {
            LOGGER.severe(e.getMessage());
            fail("FATAL ASSEMBLY FILE ERROR");
        } catch (FileNotFoundException e) {
            LOGGER.severe("FILE NOT FOUND ERROR");
            fail("FILE NOT FOUND");
        } catch (IOException | ScriptException e) {
            LOGGER.severe(e.getMessage());
            fail("FATAL ERROR");
        }
    }

    
    /** 
     * @return Collection<String[]>
     */
    @Parameterized.Parameters()
    public static Collection<String[]> files() {
        return Arrays.asList(new String[][] {
                { "/testAsm1.asm", "/testAsm1.txt" },
                { "/testAsm2.asm", "/testAsm2.txt" },
                { "/testAsm3.asm", "/testAsm3.txt" },
                { "/testAsm4.asm", "/testAsm4.txt" },
                { "/testAsm5.asm", "/testAsm5.txt" }
        });
    }
}
