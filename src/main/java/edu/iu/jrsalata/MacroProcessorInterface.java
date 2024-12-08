// Interface: MacroProcessorInterface
// Defines an interface for MacroProcessors
// and more flexibility in the future

package edu.iu.jrsalata;

import java.util.Queue;

public interface MacroProcessorInterface {
    

    public void defineArguments(String[] args);

    public void addLine(String line);

    public Queue<String> getLines() throws InvalidAssemblyFileException;
}
