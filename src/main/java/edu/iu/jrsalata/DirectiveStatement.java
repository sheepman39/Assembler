package edu.iu.jrsalata;

/**
 * The DirectiveStatement class represents a directive statement in an assembler.
 * It extends the Statement class and includes additional fields for the directive
 * and the object code.
 * 
 * This class provides constructors for creating directive statements with
 * different initial values, as well as getters and setters for accessing and
 * modifying the directive, size, and object code. It also includes methods for
 * assembling the directive statement and accepting a visitor.
 * 
 * @see Statement
 * @see HexNum
 * @see VisitorInterface
 */
public class DirectiveStatement extends Statement {

    /**
     * name of the directive this statement represents
     */
    protected String directive;

    /**
     * the object code this statement generates, if any
     */
    protected String objectCode;

    /**
     * Constructs a new DirectiveStatement with default values.
     * Initializes the directive and objectCode fields to empty strings.
     */
    public DirectiveStatement() {
        super();
        this.directive = "";
        this.objectCode = "";
    }

    /**
     * Constructs a new DirectiveStatement with the specified directive.
     *
     * @param directive the directive to be used for this statement
     */
    public DirectiveStatement(String directive) {
        super();
        this.directive = directive;
        this.objectCode = "";
    }

    /**
     * Constructs a DirectiveStatement with the specified size and directive.
     *
     * @param size the size of the directive statement as a HexNum
     * @param directive the directive as a String
     */
    public DirectiveStatement(HexNum size, String directive) {
        super(size);
        this.directive = directive;
        this.objectCode = "";
    }

    /**
     * Retrieves the directive associated with this statement.
     *
     * @return the directive as a String.
     */
    public String getDirective() {
        return this.directive;
    }

    /**
     * Sets the directive for this DirectiveStatement.
     *
     * @param directive the directive to set
     */
    public void setDirective(String directive) {
        this.directive = directive;
    }

    /**
     * Sets the size of the directive statement.
     *
     * @param size the size to set, represented as a HexNum object
     */
    public void setSize(HexNum size) {
        this.size = size;
    }

    /**
     * Sets the object code for this directive statement.
     *
     * @param code the object code to set
     */
    public void setObjCode(String code) {
        this.objectCode = code;
    }

    /**
     * Returns the object code belonging to this directive, if any
     *
     * @return the assembled object code as a String.
     */
    @Override
    public String assemble() {
        return this.objectCode;
    }

    /**
     * Accepts a visitor object and allows it to visit this directive statement.
     * This method is part of the Visitor design pattern.
     *
     * @param visitor the visitor object that will visit this directive statement
     */
    @Override
    public void accept(VisitorInterface visitor) {
        visitor.visit(this);
    }
}
