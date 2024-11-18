// Abstract Class: Statement
// Represents a given line in the program that needs to be assembled
// Handles storing its location, its OP code, and its size
package edu.iu.jrsalata;

public abstract class Statement {
    protected HexNum size;

    // constructors
    protected Statement() {
        this.size = new HexNum(0);
    }

    protected Statement(HexNum size) {
        this.size = size;
    }

    public HexNum getSize() {
        return this.size;
    }

    // entry point for visitors
    public void accept(VisitorInterface visitor){
        visitor.visit(this);
    }

    // this will be overridden by the subclasses
    // to assemble based on different formats
    public abstract String assemble();
}
