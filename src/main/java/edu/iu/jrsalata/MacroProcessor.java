package edu.iu.jrsalata;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class MacroProcessor implements MacroProcessorInterface {
    
    private final String[] parameters;
    private final ArrayList<String> definition;
    private String label;

    // constructors
    public MacroProcessor(){
        this.label = "";
        this.parameters = new String[0];
        this.definition = new ArrayList<>();
    }

    public MacroProcessor(String[] parameters){
        this.label = "";
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

        // if there is an available label, add it here
        if(!this.label.isEmpty()){
            StringBuilder builder = new StringBuilder();
            builder.append(this.label);
            builder.append("   ");
            builder.append(line);
            line = builder.toString();

            // since we only want this to work once, reset it here
            this.label = "";
        }
        // once preprocessing is done, add it to this macro's definition
        this.definition.add(line);
    }

    @Override
    public void setLabel(String label){
        // NOTE: setLabel must be ran BEFORE you add any lines
        this.label = label;
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
