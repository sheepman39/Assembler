// Class: DirectiveStatement
// Extends: Statement
// Handles the various assembly directives
package edu.iu.jrsalata;

public class DirectiveStatement extends Statement {
 
    protected String directive;
    protected String objectCode;

    // constructors
    public DirectiveStatement() {
        super();
        this.directive = "";
        this.objectCode = "";
    }

    public DirectiveStatement(String directive) {
        super();
        this.directive = directive;
        this.objectCode = "";
    }

    public DirectiveStatement(HexNum size, String directive) {
        super(size);
        this.directive = directive;
        this.objectCode = "";
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

    public void setObjCode(String code){
        this.objectCode = code;
    }

    // assembler
    @Override
    public String assemble() {
        return this.objectCode;
    }
}
