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
}
