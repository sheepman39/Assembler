// Class: DirectiveStatement
// Extends: Statement
// Handles the various assembly directives
package edu.iu.jrsalata;

public class DirectiveStatement extends Statement {
 
    protected String directive;
    protected HexNum size;

    // constructors
    public DirectiveStatement() {
        this.size = new HexNum(0);
        this.directive = "";
    }

    public DirectiveStatement(String directive) {
        this.size = new HexNum(0);
        this.directive = directive;
    }

    public DirectiveStatement(HexNum size, String directive) {
        this.size = size;
        this.directive = directive;
    }

    // getters
    public String getDirective() {
        return this.directive;
    }

    // setters
    public void setDirective(String directive) {
        this.directive = directive;
    }

    public void setSize(HexNum size) {
        this.size = size;
    }

    // assembler
    @Override
    public String assemble() {
        return "";
    }
}
