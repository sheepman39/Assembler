package edu.iu.jrsalata;

import java.util.LinkedList;
import java.util.Queue;

/**
 * The ModificationVisitor class implements the VisitorInterface and is used to collect
 * modification records from ExtendedStatement objects. It maintains a queue to store
 * the modification information.
 * 
 * This visitor does not perform any actions on other types of statements as no other
 * type of statements generate modification records
 */
public class ModificationVisitor implements VisitorInterface {

    /**
     * queue to store modification information
     */
    protected Queue<String> modifications;


    /**
     * Constructs a new ModificationVisitor instance.
     * Initializes the modifications list as a LinkedList.
     */
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

    /**
     * Visits an ExtendedStatement and adds its modification to the list of modifications
     * if the modification is not an empty string.
     *
     * @param statement the ExtendedStatement to visit
     */
    @Override
    public void visit(ExtendedStatement statement) {
        String modification = statement.getModification();
        if (!modification.isEmpty()) {
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

    /**
     * Retrieves the queue of modification strings.
     *
     * @return a Queue containing the modification strings.
     */
    @Override
    public Queue<String> getStrings() {
        return this.modifications;
    }

}
