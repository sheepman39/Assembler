// Class: RegisterStatement
// Extends: Statement
// Handles a statement in Format 2, which contains 1 or 2 registers
package edu.iu.jrsalata;

public class RegisterStatement extends BaseStatement {
    HexNum reg1, reg2;

    // constructors
    public RegisterStatement() {
        super();
        this.format = 2;
        this.size.set(this.format);
        this.reg1 = new HexNum(0);
        this.reg2 = new HexNum(0);
    }

    public RegisterStatement(HexNum location, HexNum opcode, HexNum reg1) {
        super(location, opcode);
        this.format = 2;
        this.size.set(this.format);
        this.reg1 = reg1;
        this.reg2 = new HexNum(0);
    }

    public RegisterStatement(HexNum location, HexNum opcode, HexNum reg1, HexNum reg2) {
        super(location, opcode);
        this.format = 2;
        this.size.set(this.format);
        this.reg1 = reg1;
        this.reg2 = reg2;
    }

    // setters
    public void setReg1(HexNum reg1) {
        if (reg1 != null) {
            this.reg1 = reg1;
        }
    }

    public void setReg2(HexNum reg2) {
        if (reg2 != null) {
            this.reg2 = reg2;
        }
    }

    public void setOpcode(HexNum opcode) {
        this.opcode = opcode;
    }

    // assembler
    @Override
    public String assemble() {
        return this.opcode.toString(2) + this.reg1.toString(1) + this.reg2.toString(1);
    }
}
