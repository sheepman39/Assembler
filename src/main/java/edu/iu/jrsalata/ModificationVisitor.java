// Class: ModificationVisitor
// Implements: VisitorInterface
// Handles visiting statements to retrieve modification information

package edu.iu.jrsalata;

import java.util.LinkedList;
import java.util.Queue;

public class ModificationVisitor implements VisitorInterface {

    // queue to store modification information
    protected Queue<String> modifications;

    // constructor
    public ModificationVisitor() {
        this.modifications = new LinkedList<>();
    }

    // visit methods
    @Override
    public void visit(Statement statement) {
        // do nothing
    }

    @Override
    public void visit(BaseStatement statement) {
        // do nothing
    }

    @Override
    public void visit(SicStatement statement) {
        // do nothing
    }

    @Override
    public void visit(SingleStatement statement) {
        // do nothing
    }

    // collect modification records from the ExtendedStatement
    @Override
    public void visit(ExtendedStatement statement) {
        String modification = statement.getModification();
        if (!modification.equals("")) {
            this.modifications.add(modification);
        }
    }

    @Override
    public void visit(DirectiveStatement statement) {
        // do nothing
    }

    @Override
    public void visit(RegisterStatement statement) {
        // do nothing
    }

    // return the modifications
    @Override
    public Queue<String> getStrings() {
        return this.modifications;
    }

}
