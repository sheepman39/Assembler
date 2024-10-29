// Class: BaseStatement
// Extends: Statement
// This handles the base logic for the statements that assemble into object code
package edu.iu.jrsalata;

public abstract class BaseStatement extends Statement {
    protected HexNum opcode;
    protected HexNum location;
    protected int format;

    // constructors
    public BaseStatement() {
        this.location = new HexNum();
        this.opcode = new HexNum();
        this.format = 1;
        this.size = new HexNum(this.format);
    }

    public BaseStatement(HexNum location, HexNum opcode) {
        this.location = location;
        this.opcode = opcode;
        this.format = 1;
        this.size = new HexNum(this.format);
    }

    // getters
    public HexNum getLocation() {
        return this.location;
    }

    // setters
    public void setLocation(HexNum location) {
        this.location = location;
    }

}
