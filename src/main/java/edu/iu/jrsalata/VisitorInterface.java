// Interface: Visitor Interface
// Provides an interface for visitors to Statements
package edu.iu.jrsalata;

import java.util.Queue;

public interface VisitorInterface {
    
    // create a common interface for every visitor
    public void visit(Statement statement);
    public void visit(BaseStatement statement);
    public void visit(SicStatement statement);
    public void visit(SingleStatement statement);
    public void visit(ExtendedStatement statement);
    public void visit(DirectiveStatement statement);
    public void visit(RegisterStatement statement);
    public Queue<String> getStrings();
}