// Class: InvalidAssemblyFileException
// Extends: Exception
// Provides more details for when there is an invalid line in 
// a provided assembly program
// Credit to link below for custom exceptions in java
// https://medium.com/@nweligalla/creating-custom-exceptions-in-java-ea77a61fcaf4#:~:text=To%20make%20your%20own%20exception,your%20custom%20exceptions%20in%20Java.
package edu.iu.jrsalata;

public class InvalidAssemblyFileException extends Exception {
    
    // constructors
    public InvalidAssemblyFileException(){
        super("Error with input file");
    }

    public InvalidAssemblyFileException(int lineNum){
        super("Error on line " + lineNum);
    }

    public InvalidAssemblyFileException(int lineNum, String msg){
        super("Error on line " + lineNum + ": " + msg);
    }
}
