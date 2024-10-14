// Node Class
// This is the basic Node that all of our objects will be based on
// It only contains a visitor to enable the visitor pattern
// If more functionality is needed later, we can easily add it here
public class Node {
    public void accept(Visitor v){
        v.visit(this);
    }
}
