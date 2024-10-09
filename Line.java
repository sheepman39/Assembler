public class Line {
    public String label;
    public String keyword;
    public String argument;

    public Line(){
        this.label = "";
        this.keyword = "";
        this.argument = "";
    }
    public Line(String label, String keyword, String argument){
        this.label = label;
        this.keyword = keyword;
        this.argument = argument;
    }

    // add a method that allows this object to be printed
    public String toString(){
        return "Label: " + this.label + " Keyword: " + this.keyword + " Argument: " + this.argument;
    }
}
