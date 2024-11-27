// Interface: ObjectWriterInterface
// This will be an interface that defines the methods needed for generating object files

package edu.iu.jrsalata;

import java.io.IOException;
import java.util.Queue;

public interface ObjectWriterInterface {
    public void setFileName(String fileName);

    public void setBuilder(AbstractStatementBuilder builder);

    public void setQueue(Queue<Statement> queue);

    public void execute() throws InvalidAssemblyFileException, IOException;
}
