// Class: DirectiveStatement
// Extends: Statement
// Handles the various assembly directives
package edu.iu.jrsalata;

public class DirectiveStatement extends Statement {
 
    protected String directive;

    // constructors
    public DirectiveStatement() {
        super();
        this.directive = "";
    }

    public DirectiveStatement(String directive) {
        super();
        this.directive = directive;
    }

    public DirectiveStatement(HexNum size, String directive) {
        super(size);
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
