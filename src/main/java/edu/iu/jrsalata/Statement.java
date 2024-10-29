// Abstract Class: Statement
// Represents a given line in the program that needs to be assembled
// Handles storing its location, its OP code, and its size
package edu.iu.jrsalata;

public abstract class Statement {
    protected HexNum size;

    public HexNum getSize() {
        return this.size;
    }

    // this will be overridden by the subclasses
    // to assemble based on different formats
    abstract public String assemble();
}
