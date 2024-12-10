package edu.iu.jrsalata;

/**
 * The HexNum class represents a hexadecimal number and provides methods for
 * converting between different number systems (decimal, binary, and hexadecimal),
 * performing arithmetic operations, and formatting the hexadecimal number.
 */
public class HexNum {

    /**
     * Holds the hex representation of this number
     */
    protected String value;

    /**
     * Constructs a new HexNum object with a default value of "0".
     */
    public HexNum() {
        this.value = "0";
    }

    /**
     * Constructs a HexNum object by converting an integer value to its hexadecimal string representation.
     *
     * @param value the integer value to be converted to hexadecimal
     */
    public HexNum(int value) {
        this.value = Integer.toHexString(value);
    }

    /**
     * Constructs a HexNum object by converting the given value from the specified number system to hexadecimal.
     *
     * @param value the value to be converted to hexadecimal
     * @param numSystem the number system enum of the given value (e.g., binary, decimal)
     */
    public HexNum(String value, NumSystem numSystem) {
        this.value = convertToHex(value, numSystem);
    }

    /**
     * Returns a string representation of the hexadecimal number.
     *
     * @return the string representation of this hexadecimal number.
     */
    @Override
    public String toString() {
        return this.value;
    }

    /**
     * Converts the hexadecimal number to a string representation with a specified number of digits.
     * If the number of digits is greater than the length of the hexadecimal value, the result is padded with leading zeros.
     * If the number of digits is less than the length of the hexadecimal value, the result is truncated to the specified number of digits.
     * 
     * @param digits the number of digits for the string representation
     * @return the string representation of the hexadecimal number with the specified number of digits
     */
    public String toString(int digits) {
        if (this.value.length() < digits) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digits - this.value.length(); i++) {
                sb.append("0");
            }
            sb.append(this.value);
            return sb.toString();
        } else if (this.value.length() > digits) {
            return this.value.substring(this.value.length() - digits);
        } else {
            return this.value;
        }
    }

    /**
     * Adds the specified integer value to the current value.
     *
     * @param value the integer value to be added to the current value.
     * @return a new HexNum object representing the sum of the current value and the specified integer value.
     */
    public HexNum add(int value) {
        int newVal = this.convertToDec(this.value, NumSystem.HEX) + value;
        return new HexNum(newVal);
    }

    /**
     * Adds the given HexNum value to this HexNum and returns the result as a new HexNum.
     *
     * @param value the HexNum to be added to this HexNum
     * @return a new HexNum representing the sum of this HexNum and the given value
     */
    public HexNum add(HexNum value) {
        int newVal = this.convertToDec(value.toString(), NumSystem.HEX);
        newVal += this.convertToDec(this.value, NumSystem.HEX);
        return new HexNum(newVal);
    }

    /**
     * Subtracts a given integer value from the current hexadecimal value.
     *
     * @param value the integer value to subtract from the current hexadecimal value.
     * @return a new HexNum object representing the result of the subtraction.
     */
    public HexNum subtract(int value) {
        int newVal = this.convertToDec(this.value, NumSystem.HEX) - value;
        return new HexNum(newVal);
    }

    /**
     * Subtracts the given HexNum value from this HexNum and returns the result as a new HexNum.
     *
     * @param value the HexNum to be subtracted from this HexNum
     * @return a new HexNum representing the result of the subtraction
     */
    public HexNum subtract(HexNum value) {
        int newVal = this.convertToDec(this.value, NumSystem.HEX);
        newVal -= this.convertToDec(value.toString(), NumSystem.HEX);
        return new HexNum(newVal);
    }

    /**
     * Sets the value of this HexNum object by converting the given decimal integer
     * to its hexadecimal representation.
     *
     * @param value the decimal integer to be converted and set as the value
     */
    public void set(int value) {
        this.value = this.convertToHex(Integer.toString(value), NumSystem.DEC);
    }

    /**
     * Sets the value of this HexNum object by converting the given value from the specified number system to hexadecimal.
     *
     * @param value the value to be converted and set
     * @param numSystem the number system of the given value (e.g., binary, decimal)
     */
    public void set(String value, NumSystem numSystem) {
        this.value = this.convertToHex(value, numSystem);
    }

    /**
     * Converts the hexadecimal value of this object to its decimal equivalent.
     *
     * @return the decimal equivalent of the hexadecimal value.
     */
    public int getDec() {
        return this.convertToDec(this.value, NumSystem.HEX);
    }

    /**
     * Converts the hexadecimal number to its binary representation.
     *
     * @return A string representing the binary equivalent of the hexadecimal number.
     */
    public String getBin() {
        return Integer.toBinaryString(this.getDec());
    }

    /**
     * Converts the hexadecimal number to its binary representation with a specified number of digits.
     * If the binary representation is shorter than the specified number of digits, it is left-padded with zeros.
     *
     * @param digits the number of digits for the binary representation
     * @return the binary representation of the hexadecimal number as a string
     */
    public String getBin(int digits) {
        String binary = Integer.toBinaryString(this.getDec());
        if (binary.length() < digits) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digits - binary.length(); i++) {
                sb.append("0");
            }
            sb.append(binary);
            return sb.toString();
        } else {
            return binary;
        }
    }

    
    /**
     * Converts a given string value from a specified numerical system to its hexadecimal representation.
     *
     * @param value the string representation of the number to be converted
     * @param numSystem the numerical system of the input value (HEX, DEC, or BIN)
     * @return the hexadecimal representation of the input value
     */
    private String convertToHex(String value, NumSystem numSystem) {

        String returnVal = "";
        switch (numSystem) {
            case HEX -> returnVal = value;
            case DEC -> returnVal = Integer.toHexString(Integer.parseInt(value));
            case BIN -> returnVal = Integer.toHexString(Integer.parseInt(value, 2));
        }
        return returnVal;
    }

    /**
     * Converts a string representation of a number from a specified numeral system to its decimal (base 10) equivalent.
     *
     * @param value the string representation of the number to be converted.
     * @param numSystem the numeral system of the input value. It can be HEX (hexadecimal), DEC (decimal), or BIN (binary).
     * @return the decimal (base 10) equivalent of the input value.
     * @throws NumberFormatException if the input value is not a valid number in the specified numeral system.
     */
    private int convertToDec(String value, NumSystem numSystem) {
        int returnVal = 0;
        switch (numSystem) {
            case HEX -> returnVal = Integer.parseInt(value, 16);
            case DEC -> returnVal = Integer.parseInt(value);
            case BIN -> returnVal = Integer.parseInt(value, 2);
        }
        return returnVal;
    }
}
