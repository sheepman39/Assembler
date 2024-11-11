// Interface: ObjectWriterInterface
// This will be an interface that defines the methods needed for generating object files

package edu.iu.jrsalata;

import java.util.Queue;

public interface ObjectWriterInterface {
    public void setFileName(String fileName);

    public void setFactory(StatementFactoryInterface factory);

    public void setQueue(Queue<Statement> queue);

    public void execute();
}
