// Class: ModificationVisitor
// Implements: VisitorInterface
// Handles visiting statements to retrieve modification information

package edu.iu.jrsalata;

import java.util.LinkedList;
import java.util.Queue;

public class ModificationVisitor implements VisitorInterface{

    // queue to store modification information
    protected Queue<String> modifications;

    // constructor
    public ModificationVisitor(){
        this.modifications = new LinkedList<>();
    }

    // visit methods
    public void visit(Statement statement){
        // do nothing
    }

    public void visit(BaseStatement statement){
        // do nothing
    }

    public void visit(SicStatement statement){
        // do nothing
    }

    public void visit(SingleStatement statement){
        // do nothing
    }

    // collect modification records from the ExtendedStatement
    public void visit(ExtendedStatement statement){
        String modification = statement.getModification();
        if(!modification.equals("")){
            this.modifications.add(modification);
        }
    }

    public void visit(DirectiveStatement statement){
        // do nothing
    }

    public void visit(RegisterStatement statement){
        // do nothing
    }

    // return the modifications
    public Queue<String> getStrings(){
        return this.modifications;
    }
    
}
