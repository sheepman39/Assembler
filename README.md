# SIC/XE Assembler

## Overview
This is an SIC/XE assembler that is currently a work in progress.  SIC/XE is a hypothetical instruction set designed to make it easier to understand assembly and use those concepts on real-world architectures.  More information can be found in [this article](https://www.geeksforgeeks.org/sic-xe-architecture/). 

## Documentation
All documentation and implementation information can be found on [my website](https://salata.software/projects/assembler/documentation).  That will have the most in-depth and formal documentation of this project.

## Requirements
This project is built with Java version 17.0.7 and managed using [Maven](https://maven.apache.org/index.html).  Please check their [downloads](https://maven.apache.org/download.cgi) page for installation steps.

### Building and Running
To run tests, run
```
mvn test
```

To build a jar file to execute, run
```
mvn package
```

The jar file will be in `target/`.  There will be two files. Please run the file that ends with `jar-with-dependencies.jar`.  Ex) `java -jar target/assembler-1.2-SNAPSHOT-jar-with-dependencies.jar`

The expected file name for any input is `input.asm` in your current directory.  There will be two output files generated.
1) output.obj - which contains the object file
2) output.obj.txt - which contains the assembled line next to each of the inputs

To generate javadocs, run
```
mvn javadocs:javadocs
```
NOTE: the pom.xml file is currently configured so the javadocs are produced in the apidocs/ directory in the root of the project instead of in target/.  