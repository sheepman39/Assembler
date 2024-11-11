# SIC/XE Assembler

### Overview
This is an SIC/XE assembler that is currently a work in progress.  SIC/XE is a hypothetical instruction set designed to make it easier to understand assembly and use those concepts on real-world architectures.  More information can be found in [this article](https://www.geeksforgeeks.org/sic-xe-architecture/). 

#### SIC/XE Architecture
This section will contain some important details about the SIC/XE Architecture.
##### Formats
There are 4 formats in SIC/XE. 
1. Format 1
* 1 byte opcode
2. Format 2
* 2 bytes
* 1 byte opcode
* 1 byte for two registers
3. Format 3
* 3 bytes
* 1 byte opcode and NI flags
* 1/2 byte for BXPE flags
* 3/2 byte for address
4. Format 4
* 4 bytes
* 1 byte opcode and NI flags
* 1/2 byte for BXPE flags
* 5/2 byte for address


### Requirements
This project is written in Java 1.8 using Maven to help manage it.  In order to run this assembler, you must have java 1.8 installed.  You can check your current java version by running `java -version` in your terminal of choice.  You can install the JDK [here](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html).


### Structure

#### src/main/java/edu/iu/jrsalata
This is the main package for this project.  It contains all of the classes that are used in this project. The breakdown of the structure will be included later in this section.

#### src/main/resources
These are any files that are needed in the compiled jar file.  This includes test files, register files in `registers.txt`, and the valid operation codes in `instructions.txt`.

#### src/test/java/edu/iu/jrsalata
These contain test files for each concrete class in this project.  Each of the tests are written using junit 4.11.  You can run the tests by running `mvn test` in the root directory.  

### Classes

#### HexNum
The HexNum class is a widely used placeholder that makes it easy to store, convert, and do operations with hexadecimal numbers.  This makes it easy to identify which values are supposed to be stored in hex vs decimal.

#### Statement
##### Abstract Class
Statement is the base class implementing the Command pattern.  It contains the size of the statement in a HexNum format.  It contains a `getSize()` method that returns the HexNum size.  It has one abstract method that must be implemented by all concretions called `assemble()`, which will return the assembled object code in a String format.

#### Base Statement
##### Abstract Class
##### Extends Statement
Base statement is the basic statement for all opcodes and instructions.  It contains an opcode, location, and the format in an integer.  It contains some useful information that all assembled statements will have.  However, it does not contain the `assemble()` method, as that is left to the concrete classes.

#### SingleStatement
##### Extends BaseStatement
This is a concrete class that represents statements in Format 1. It contains only the opcode needed to assemble the statement.

#### RegisterStatement
##### Extends BaseStatement
This is a concrete class that represents statements in Format 2. It contains the opcode and the registers needed to assemble the statement.  If only one register is provided, then the second register is simply set to 0.

#### ExtendedStatement
##### Extends BaseStatement
This is a concrete class that represents statements in Format 3 and Format 4.  It contains the opcode, the flags, and the address needed to assemble the statement.  When assembling, it needs to look up the address in the symbol table stored in `SymTable` to get the address.

#### SicStatement
##### Extends BaseStatement
This is a concrete class that represents statements in the original SIC architecture.  It contains the opcode and the address needed to assemble the statement. While this could have been included in `ExtendedStatement`, it was separated since there are some major differences between SIC and SIC/XE statements, like the removal of the NI flags in the first byte.

#### DirectiveStatement
##### Extends Statement
This is a concrete class that represents statements that are assembler directives.  It contains the directive and the operand needed to assemble the statement.  If it generates object code, it will return the object code.  Otherwise, it will return an empty string.  Assembler directives have many different types of behaviors and this ensures that they are handled uniformly.

#### StatementFactoryInterface
##### Interface
The StatementFactoryInterface is an interface that contains the necessary methods to create and handle the creation of statements.  They include
* `createStatement(String statement) : Statement`: This method takes a string and returns a statement object.
* `getStart(void) : HexNum`: This method returns the starting address of the program.
* `getLen(void) : HexNum`: This method returns the length of the program.
* `getName(void) : String`: This method returns the name of the program.

#### StatementFactory
##### Implements StatementFactoryInterface
This is a concrete class that implements the StatementFactoryInterface.  It contains the complex logic needed to create statements and handle the assembler directives.  

#### ObjectWritterInterface
##### Interface
The ObjectWritterInterface is an interface that makes it easier writing to an object file. The methods it includes are
* `setFileName(String fileName)` : This method sets the filename of the output object file.
* `setFactory(StatementFactoryInterface factory)` : This method sets the factory that will be used to generate the object code. The factory is needed for the starting locations of the program.
* `setQueue(Queue<Statement> queue)` : This method sets the queue that will be used to write to the object file. The queue should be generated from the `Statement` objects produced by the factory.
* `execute(void)` : This method creates the object file

#### ObjectWritter
##### Implements ObjectWritterInterface
This is a concrete class that implements the ObjectWritterInterface.  It contains the logic needed to write to an object file.  The object file produced is written to the specifications down below in the "Output" section.

### Enumerations

#### Format
This enumeration represents the different SIC/XE formats.  It contains the values
* ONE
* TWO
* THREE
* ASM
* SIC
Note that FOUR is ommited as the process for handling Format 4 is the same as Format 3.  ASM is used for assembler directives and SIC is used for the original SIC architecture.

#### NumSystem
This is used to differentiate between the different number systems that a process is using. It contains the values
* HEX
* DEC
* BIN

### Usage
#### Expected Input
Right now, the input file is hardcoded to be `input.asm` in the root directory.  In the future, a flexible file selector will be used to allow different files to be used.  The file should look something like this:
```
. comments are marked by a period
. this is an example of an SIC program
NAME START 1000 . the left column represents the label, the middle column represents the operation, and the right column represents the operand
. it should be noted that the spacing is purely aesthetic
	LDS	#3	 .Initialize Register S to 3
	LDT	#300 .Initialize Register T to 300
	LDX	#0	 .Initialize Index Register to 0
ADDLP	LDA	ALPHA,X	.Load Word from ALPHA into Register A
	ADD	BETA,X	.Add Word From BETA
	STA	GAMMA,X	.Store the Result in a work in GAMMA
	ADDR	S,X	.ADD 3 to INDEX value
	COMPR	X,T	.Compare new INDEX value to 300
	JLT	ADDLP	.Loop if INDEX value is less than 300
ALPHA	RESW	100
BETA	RESW	100
GAMMA	RESW	100
```

#### Output
The output will be an object file called `output.obj`.  In the future, we will be able to select a filename.  The format of the object file will be
```
Header Record:
Col. 1:       H
Col. 2-7:    Program Name (START if none given)
Col. 8-13:   Starting address of object program (hex)
Col. 14-19: Length of object program in bytes (hex)

Text Record:
Col. 1:      T
Col. 2-7:   Starting address for object code (hex)
Col. 8-9:   Length of object code in this record in bytes (hex)
Col. 10-69: Object code (hex)

End Record:
Col. 1:  E
Col. 2-7: Address of first executable instruction in object program (hex)
```
and an example output will be
```
HCOPY  00100000107a
T0010001e1410334820390010362810303010154820613C100300102a0C103900102d
T00101e150C10364820610810334C0000454f46000003000000
T0020391e041030001030E0205d30203fD8205d2810303020575490392C205e38203f
T0020571c1010364C0000F1001000041030E02079302064509039DC20792C1036
T002073073820644C000005
E001000
```