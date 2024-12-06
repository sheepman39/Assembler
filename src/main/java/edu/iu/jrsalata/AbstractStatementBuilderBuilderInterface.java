package edu.iu.jrsalata;

import java.io.FileNotFoundException;
import java.util.Queue;

import javax.script.ScriptException;

public interface AbstractStatementBuilderBuilderInterface {
    
    public Queue<AbstractStatementBuilder> getBuilders();
    public void execute() throws InvalidAssemblyFileException, FileNotFoundException, ScriptException;
    public void setInputFile(String filename);

}
