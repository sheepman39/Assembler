// Abstract Class: Statement
// Represents a given line in the program that needs to be assembled
// Handles storing its location, its OP code, and its size
package edu.iu.jrsalata;

public abstract class Statement {
    protected HexNum opcode;
    protected HexNum location;
    protected HexNum size;
    protected int format;

    // constructors
    public Statement() {
        this.location = new HexNum();
        this.opcode = new HexNum();
        this.format = 1;
        this.size = new HexNum(this.format);
    }

    public Statement(HexNum location, HexNum opcode) {
        this.location = location;
        this.opcode = opcode;
        this.format = 1;
        this.size = new HexNum(this.format);
    }

    // getters
    public HexNum getSize() {
        return this.size;
    }

    public HexNum getLocation() {
        return this.location;
    }

    // setters
    public void setLocation(HexNum location) {
        this.location = location;
    }

    // this will be overridden by the subclasses
    // to assemble based on different formats
    abstract public String assemble();
}
