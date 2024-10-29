// Class: SingleStatement
// Extends: Statement
// Represents a single opcode in Format 1
package edu.iu.jrsalata;

public class SingleStatement extends BaseStatement {
    
    // constructors
    public SingleStatement() {
        super();
    }
    public SingleStatement(HexNum location, HexNum opcode) {
        super(location, opcode);
        this.size = new HexNum(this.format);
    }

    // implementation of the assemble method
    @Override
    public String assemble() {
        return this.opcode.toString(2);
    }
}