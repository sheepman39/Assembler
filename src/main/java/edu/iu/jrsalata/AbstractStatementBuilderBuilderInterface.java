package edu.iu.jrsalata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

import javax.script.ScriptException;

public interface AbstractStatementBuilderBuilderInterface {
    
    public Queue<AbstractStatementBuilder> getBuilders();
    public void execute() throws InvalidAssemblyFileException, FileNotFoundException, ScriptException, IOException;
    public void execute(InputStream file) throws InvalidAssemblyFileException, FileNotFoundException, ScriptException, IOException;

    public void setInputFile(String filename);

}
