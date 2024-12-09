package edu.iu.jrsalata;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class MacroProcessor implements MacroProcessorInterface {
    
    private String[] parameters;
    private ArrayList<String> definition;

    // constructors
    public MacroProcessor(){
        this.parameters = new String[0];
        this.definition = new ArrayList<>();
    }

    public MacroProcessor(String[] parameters){
        this.parameters = parameters;
        this.definition = new ArrayList<>();
    }

    @Override
    public void addLine(String line){
        
        // first we will find and replace each parameter with its respective position
        // placeholders will be in the format of 
        // {i} where i is the index of the parameter
        for(int i = 0; i < this.parameters.length; i++){
            line = line.replace(this.parameters[i], "{" + Integer.toString(i) + "}");
        }

        // once preprocessing is done, add it to this macro's definition
        this.definition.add(line);
    }

    @Override
    public Queue<String> getLines(String[] args) throws InvalidAssemblyFileException{

        Queue<String> returnQueue = new LinkedList<>();

        // ensure the number of args and parameters matches
        // if not, return an InvalidAssemblyFileException to 
        // let the user know their input is bad
        if(this.parameters.length != args.length){
            StringBuilder message = new StringBuilder();
            message.append("MACRO ERROR: Expected ");
            message.append(this.parameters.length);
            message.append(" parameters.  Found ");
            message.append(args.length);
            throw new InvalidAssemblyFileException(-1, message.toString());
        }

        // for every line in our definition,
        // find and replace the placeholders with the appropriate args
        for(String line : this.definition){
            for(int i = 0; i < this.parameters.length; i++){
                line = line.replace("{" + Integer.toString(i) + "}", args[i] );
            }
            returnQueue.add(line);
        }

        return returnQueue;
    }
}
