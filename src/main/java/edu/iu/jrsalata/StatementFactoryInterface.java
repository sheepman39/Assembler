// Class: StatementFactoryInterface
// This is an interface that will define the methods that concretions will use to create statements
package edu.iu.jrsalata;

public interface StatementFactoryInterface {
    public Statement processStatement(String statement);
    public HexNum getStart();
    public HexNum getLen();
    public String getName();
}
