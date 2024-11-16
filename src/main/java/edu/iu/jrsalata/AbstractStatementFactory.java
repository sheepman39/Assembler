// Class: StatementFactoryInterface
// This is an interface that will define the methods that concretions will use to create statements
package edu.iu.jrsalata;

public abstract class AbstractStatementFactory {

    // locctr keeps track of the current location of each statement
    protected HexNum locctr;
    protected HexNum start = new HexNum(0);
    protected String name = "";

    // constructor
    public AbstractStatementFactory() {
        this.locctr = new HexNum(0);
        this.start = new HexNum();
        this.name = "";
    }

    // get the start location
    public HexNum getStart() {
        return this.start;
    }

    // get the length of the program
    public HexNum getLen() {
        int lenStart = this.start.getDec();
        int lenEnd = this.locctr.getDec();
        return new HexNum(lenEnd - lenStart);
    }

    public String getName() {

        // name needs to be exactly six characters long
        // if we have no name, default is OBJECT
        // if the name is longer than 6, truncate it
        // if the name is shorter than 6, pad it with spaces at the end
        // if the name is exactly 6, return it
        if (this.name.equals("")) {
            return "OUTPUT";
        } else if (this.name.length() > 6) {
            return this.name.substring(0, 6).toUpperCase();
        } else if (this.name.length() < 6) {
            StringBuilder sb = new StringBuilder(this.name);
            for (int i = 0; i < 6 - this.name.length(); i++) {
                sb.append(" ");
            }
            return sb.toString().toUpperCase();
        } else {
            return this.name.toUpperCase();
        }
    }

    public abstract Statement processStatement(String statement) throws InvalidAssemblyFileException;

}
