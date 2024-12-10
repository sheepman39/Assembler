package edu.iu.jrsalata;

/**
 * The {@code Format} enum represents the different formats in the SIC/XE instruction set
 * <ul>
 *   <li>{@link #ONE} - Format 1</li>
 *   <li>{@link #TWO} - Format 2</li>
 *   <li>{@link #THREE} - Format 3</li>
 *   <li>{@link #ASM} - Assembly format</li>
 *   <li>{@link #SIC} - Simplified Instructional Computer format</li>
 * </ul>
 */
public enum Format {
    /**
     * Represents format 1
     * 1 byte containing the opcode
     */
    ONE, 
    
    /**
     * Represents format 2
     * 2 bytes
     * 1 byte containing the opcode
     * 1/2 byte containing register 1
     * 1/2 byte containing register 2
     */
    TWO, 
    
    /**
     * Represents format 3 and 4
     * 3/4 bytes
     * 6 bits for opcode
     * 2 bits for addressing mode
     * 1/2 byte for other flags
     * 3/2 bytes for displacement
     * +1 byte if format 4
     */
    THREE, 
    
    /**
     * Represents assembler directives
     */
    ASM,
    
    /**
     * Represents SIC-only statements
     */
    SIC
}
