# SIC/XE Assembler

## Overview
This is an SIC/XE assembler written in Java. SIC/XE is a hypothetical instruction set designed to make it easier to understand assembly and use those concepts on real-world architectures. More information can be found in [this article](https://www.geeksforgeeks.org/sic-xe-architecture/). 

## Documentation
All documentation and implementation information can be found on [my website](https://salata.software/projects/assembler/documentation). That will have the most in-depth and formal documentation of this project. The documentation is also in the `apidocs/` directory of this repo as well.

## Requirements
This project is built with Java version 17.0.7 and managed using [Maven](https://maven.apache.org/index.html). Please check their [downloads](https://maven.apache.org/download.cgi) page for installation steps.

## Building and Running
For simplicity, it is recommended to use a [dev container](https://code.visualstudio.com/docs/devcontainers/containers) using your tool of choice. GitHub Codespaces were used for this project, but you can host your own containers with the provided configuration in `.devcontainer`. The image includes Java 17 and maven so nothing else is needed to be installed.

To install and run locally without a dev container, ensure Java 17 and Maven is installed.

To run tests, run
```
mvn test
```

To build a jar file to execute, run
```
mvn package
```

The jar file will be in `target/`. There will be two files. Please run the file that ends with `jar-with-dependencies.jar`. 
Ex) `java -jar target/assembler-1.2-SNAPSHOT-jar-with-dependencies.jar`

The expected file name for any input is `input.asm` in your current directory. There will be two output files generated.
1) output.obj - which contains the object file
2) output.obj.txt - which contains the assembled line next to each of the inputs

To generate javadocs, run
```
mvn javadocs:javadocs
```
NOTE: the pom.xml file is currently configured so the javadocs are produced in the apidocs/ directory in the root of the project instead of in target/. 

# Structure
## Overview
The Main class creates an Abstract Statement Builder Builder and gives it an input file. When the Builder Builder is executed, it reads the file and creates Abstract Statement Builders for each control section. If needed, the Abstract Statement Builder Builder creates Macro Processors to hold the macro definitions. The input line is then fed into the Abstract Statement Builder to create different types of statements. Each builder handles their program blocks, literals, symbols, and location counters. Each statement created is fed into a queue. Once the Builder is done, the Builder Builder creates a queue of Builders. This queue is then given back to the Main class. The Main class then creates an Object Writer and gives it the output file name and a builder. The writer uses the given builder to generate the appropriate object file for it. Once all builders are written, the program ends. 

## Base Classes
As this project is written in Java, it is highly object oriented. I decided to try and take advantage of this by using various software design patterns to make the program more flexible and adaptable in the future.

As all of the official documentation is linked above, this will be a brief overview of some of the classes and the structure.

### Statement
For this project, we treat each line of assembly as a statement. The parent of all statements contains its size, program block, control section, and line number. It also has two abstract methods called accept for visitors and assemble. Assemble returns a String that represents the given statement's object code. 

Because we have many different kinds of statements for different formats, the common assemble method allows us to utilize the command pattern and let each individual statement handle its own logic and processing

### Abstract Statement Builder
Abstract Statement Builders are responsible for an individual control section. They contain various tables, location counters, and external modifications. The Abstract Statement Builder handles most of the machine-independent logic like expressions, literals, and program blocks. It has an abstract method called processStatement that takes in a string. Each concretion has to process the statement and choose what to do with it. This is important because there is a difference between how SIC and SIC/XE machines process assembly. 

As the name implies, it is a builder. It takes in input, processes it, and builds a queue of statements. This queue of statements contains every necessary statement to properly produce object code. 

Originally, this was a factory where a given line would produce a given statement. However, program blocks and literals required a change in structure. Literals are stored in literal pools which are placed at arbitrary sections of the program. There was no way to return multiple statements representing multiple literals. It was also necessary for program blocks as the start of each bock is not known until after everything is loaded in and processed.

### Abstract Statement Builder Builder
As the name implies, this class builds Abstract Statement Builders. This was needed in order to properly implement control sections and macro processing.

Control sections essentially generate separate programs that are linked together in the linker/loader of the device. Because of this, it didn't make any sense to try and incorporate this logic within one builder. Each control section has their own builder in order to keep everything separate.

Macros also needed this functionality as macros work differently. They are not built, but rather processed. Their definition needed to be put into a separate location so builders can use their definitions later on.

The Abstract Statement Builder Builder also selects which concrete Statement Builder to use. It is expected that any files that use the SIC instruction set start with `!USE SIC` at the top of the .asm file. If this flag is there, it will use the SIC builder instead of the SIC/XE builder.

Another responsibility of the builder builder is to handle file input. A client can give either an InputStream or a file name to read and manage the rest of the process. 

While the name may make it seem like this is an unnecessary class, it helps simplify the process for clients to use and generate statement builders.

### Object Writer
The Object Writer class is responsible for using the builders to write object code. It uses the generated queues of statements from concrete builders to generate each of the needed fields in an object file.

### SymTable
This is a static class that holds tables to make it easier for every class to access data. It held Macro Processors, program block names, and symbol names. By making everything in this class static, it allows for every class to access it when needed.

### Utility
This class does not really have a purpose other than to contain useful methods that help with small steps. It can make lines easier to work with, return a string with a specific length, or split a line into an array.

# Implementation Phases
There were two main phases with this project.
1) Base Assembler
2) Advanced Assembler

The Base Assembler was meant to get the framework of the project going and handle basic programs. It could handle very few assembler directives and focused on generating correct SIC object code.

The Advanced Assembler implemented control sections, program blocks, expressions, literals, and anything else that was needed. 

# Issues
## Tracking
I heavily relied on GitHub issues to help me keep track of every requirement and step that was needed. There were also a few times that a rewrite or refactor was needed in order to implement a given feature. As of writing this report, there were [34 total issues](https://github.com/sheepman39/Assembler/issues) that included new features and improved old ones.

## Building
In the first week or so of the project, I was relying on makefiles to help me compile and run each of my files. However, I quickly realized that would not scale easily as I added more and more complexity. I started to use maven to help me manage the building and testing of this project. I also used a code analysis tool called Sonarcloud that would detect issues that are more language specific and helped me follow best practices. 

## Debugging
When implementing the assembly method for the extended statement, there were a lot of issues trying to figure out why exactly a certain value was calculated. My tests were failing since the generated code did not match up with what was provided by the book. 

It was a strange issue and one that required me to use the java debugger to figure out what was going on. I was able to set different break points and watch the value of different variables to try and figure it out.

Eventually, I discovered that I was miscalculating the displacement and needed to rework how I handled Base relative and PC relative addressing. 

There were many issues like this that required me to use the debugger and track down where an issue was occurring and what exactly was causing it. With each new class or method, the complexity of the project grew. Sometimes, it would be an easy fix. I forgot to set a value in a table or forgot to call a method. Other times, it was difficult to find out where an issue was coming from. Stepping back and coming at the problem from a fresh perspective was helpful. Other times, I would have to do some more research and take it step by step.

## Testing
This was the first project that I used tests to ensure the correctness of my code. In previous projects, I ran a bunch of assertions to ensure it was correct, but never used a proper tool to facilitate it. For this project, I decided to write tests for different classes that had a high potential of failing, such as the Statements, Builders, and Object Writers. 

The most important tests were for the builders and writers. I used the generated object code from the book and compared them against that. If the output matched, it worked. If it didn't match, the test failed and provided feedback on where the difference was. This was extremely important as small changes would have big consequences. One change that fixed a bug would cause a seemingly unrelated feature to break. These tests saved me a lot of time figuring out where an issue could be.

However, there were some issues with my tests. Before I made the builder builder and utility files, I had to manually copy and paste similar logic between different files. One change in one file required all of them to be changed. This was tedious and the odds of me breaking the tests were high. There were times I purposefully changed the output and no failures were reported, which was concerning. I had to ensure that my tests actually worked in order for me to rely on them. 

Overall, it was an insightful time testing and I wish I wrote more tests.

## Skills Learned
Overall I developed and grew in this project include but not limited to:
- Test Driven Development
- Project management with Maven
- Using GitHub Actions to test and analyze code
- Issue management with GitHub Issues
- Documentation using javadocs
- Implementation of software design patterns

This was a very insightful project and a fun challenge. I only had to completely rewrite the project once at the very beginning when I realized my original design made no sense. Everything that I created was easily adaptable and flexible to new and expanding designs thanks to good practices with flexibility in mind. 
