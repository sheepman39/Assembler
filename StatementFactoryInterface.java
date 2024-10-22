// Class: StatementFactoryInterface
// This is an interface that will define the methods that concretions will use to create statements
public interface StatementFactoryInterface {
    public Statement processStatement(String statement);
}
