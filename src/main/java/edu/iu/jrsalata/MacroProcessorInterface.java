// Interface: MacroProcessorInterface
// Defines an interface for MacroProcessors
// and more flexibility in the future

package edu.iu.jrsalata;

import java.util.Queue;

public interface MacroProcessorInterface {

    public void addLine(String line);

    public void setLabel(String label);

    public Queue<String> getLines(String[] args) throws InvalidAssemblyFileException;
}
