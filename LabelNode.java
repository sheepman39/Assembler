// Class: LabelNode
// Extends: NamedNode
// This class is for all of the labels 
// Main difference is that we need certain error flags
// during assembling
public class LabelNode extends NamedNode {
    
    // This is a generic 'we messed up' flag
    // more specific flags will be built later
    boolean error = false;

    public LabelNode(){
        super();
    }
    public LabelNode(String name){
        super(name);
    }
    public void accept(Visitor v){
        v.visit(this);
    }

    // For now, let's assume that if there is any error
    // we are going to set it to true
    public void setError(){
        this.error = true;
    }
}
