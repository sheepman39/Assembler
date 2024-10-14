// Class: NamedNode
// Extends: Node
// This adds a name to the basic Node
// Names are not strictly needed for parts of the project,
// but it helps keep certain pieces of data together
public class NamedNode extends Node {
    protected String name;

    public NamedNode(){
        this.name = "";
    }
    public NamedNode(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

    public String toString(){
        return "Name: " + this.name;
    }
    
}
