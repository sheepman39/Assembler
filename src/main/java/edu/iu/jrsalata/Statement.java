// Abstract Class: Statement
// Represents a given line in the program that needs to be assembled
// Handles storing its location, its OP code, and its size
package edu.iu.jrsalata;

public abstract class Statement {
    protected HexNum size;
    protected String block;

    // constructors
    protected Statement() {
        this.size = new HexNum(0);
        this.block = "";
    }

    protected Statement(HexNum size) {
        this.size = size;
        this.block = "";
    }

    protected Statement(HexNum size, String block) {
        this.size = size;
        this.block = block;
    }

    public HexNum getSize() {
        return this.size;
    }

    public String getBlock() {
        return this.block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    // entry point for visitors
    public abstract void accept(VisitorInterface visitor);

    // this will be overridden by the subclasses
    // to assemble based on different formats
    public abstract String assemble() throws InvalidAssemblyFileException;
}
