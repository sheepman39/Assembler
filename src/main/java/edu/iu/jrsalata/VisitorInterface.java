package edu.iu.jrsalata;

import java.util.Queue;

/**
 * VisitorInterface defines a common interface for various types of visitors
 * that can process different kinds of statements in an assembler.
 */
public interface VisitorInterface {

    /**
     * Visits the given statement.
     *
     * @param statement the statement to be visited
     */
    public void visit(Statement statement);

    public void visit(BaseStatement statement);

    public void visit(SicStatement statement);

    public void visit(SingleStatement statement);

    public void visit(ExtendedStatement statement);

    public void visit(DirectiveStatement statement);

    public void visit(RegisterStatement statement);

    public Queue<String> getStrings();
}