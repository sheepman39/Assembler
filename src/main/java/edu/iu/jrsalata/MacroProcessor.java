package edu.iu.jrsalata;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The MacroProcessor class is responsible for processing macros in assembly language.
 * It implements the MacroProcessorInterface and provides methods to add lines to a macro,
 * set a label for a macro, and retrieve the processed lines with arguments.
 */
public class MacroProcessor implements MacroProcessorInterface {

    /**
     * Holds each of the defined parameters in an array for simplicity
     */
    private final String[] parameters;

    /**
     * Holds each line of assembly in an ArrayList for flexibility
     */
    private final ArrayList<String> definition;

    /**
     * Holds the name of the given macro
     */
    private String label;

    /**
     * Constructs a new MacroProcessor with default values.
     * Initializes the label to an empty string, parameters to an empty array,
     * and definition to an empty ArrayList.
     */
    public MacroProcessor() {
        this.label = "";
        this.parameters = new String[0];
        this.definition = new ArrayList<>();
    }

    /**
     * Constructs a new MacroProcessor with the specified parameters.
     *
     * @param parameters an array of strings representing the parameters for the macro processor
     */
    public MacroProcessor(String[] parameters) {
        this.label = "";
        this.parameters = parameters;
        this.definition = new ArrayList<>();
    }

    /**
     * Adds a line to the macro's definition after preprocessing it.
     * 
     * The preprocessing involves:
     * 1. Replacing each parameter in the line with its respective position placeholder.
     *    The placeholders are in the format of {i}, where i is the index of the parameter.
     * 2. If a label is available, it is prepended to the line followed by three spaces.
     *    The label is then reset to an empty string to ensure it is only used once.
     * 
     * @param line The line to be added to the macro's definition.
     */
    @Override
    public void addLine(String line) {

        for (int i = 0; i < this.parameters.length; i++) {
            line = line.replace(this.parameters[i], "{" + Integer.toString(i) + "}");
        }

        // if there is an available label, add it here
        if (!this.label.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append(this.label);
            builder.append("   ");
            builder.append(line);
            line = builder.toString();
            this.label = "";
        }

        // once preprocessing is done, add it to this macro's definition
        this.definition.add(line);
    }

    /**
     * Sets the label for the macro processor.
     * Note: This method must be called before adding any lines using addLine(String line).
     * 
     * @param label the label to set
     */
    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Processes the macro definition by replacing placeholders with the provided arguments.
     * 
     * @param args An array of strings representing the arguments to replace the placeholders in the macro definition.
     * @return A Queue of strings where each string is a line from the macro definition with placeholders replaced by the corresponding arguments.
     * @throws InvalidAssemblyFileException If the number of provided arguments does not match the number of parameters in the macro definition.
     */
    @Override
    public Queue<String> getLines(String[] args) throws InvalidAssemblyFileException {
        Queue<String> returnQueue = new LinkedList<>();

        if (this.parameters.length != args.length) {
            StringBuilder message = new StringBuilder();
            message.append("MACRO ERROR: Expected ");
            message.append(this.parameters.length);
            message.append(" parameters.  Found ");
            message.append(args.length);
            throw new InvalidAssemblyFileException(-1, message.toString());
        }

        for (String line : this.definition) {
            for (int i = 0; i < this.parameters.length; i++) {
                line = line.replace("{" + Integer.toString(i) + "}", args[i]);
            }
            returnQueue.add(line);
        }

        return returnQueue;
    }
}
