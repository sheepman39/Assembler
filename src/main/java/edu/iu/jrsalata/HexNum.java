// Class: HexNum
// Purpose: This will be a wrapper for all of the logic needed
// for hex numbers.
package edu.iu.jrsalata;

public class HexNum {

    // value holds the hex number
    protected String value;

    // constructors
    public HexNum() {
        // default value is 0
        this.value = "0";
    }

    // by default, assume provided ints are in decimal
    public HexNum(int value) {
        this.value = Integer.toHexString(value);
    }

    // given a number system and string, convert it to hex
    public HexNum(String value, NumSystem numSystem) {
        this.value = convertToHex(value, numSystem);
    }

    @Override
    public String toString() {
        return this.value;
    }

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

    // returns a HexNum with a new value
    // assume int input is in decimal
    public HexNum add(int value) {
        int newVal = this.convertToDec(this.value, NumSystem.HEX) + value;
        return new HexNum(newVal);
    }

    // convert hex values to decimal, then back to hex
    public HexNum add(HexNum value) {
        int newVal = this.convertToDec(value.toString(), NumSystem.HEX);
        newVal += this.convertToDec(this.value, NumSystem.HEX);
        return new HexNum(newVal);
    }

    // returns a HexNum with a new value
    // assume int input is in decimal
    public HexNum subtract(int value) {
        int newVal = this.convertToDec(this.value, NumSystem.HEX) - value;
        return new HexNum(newVal);
    }

    // convert hex values to decimal, then back to hex
    public HexNum subtract(HexNum value) {

        int newVal = this.convertToDec(this.value, NumSystem.HEX);
        newVal -= this.convertToDec(value.toString(), NumSystem.HEX);
        return new HexNum(newVal);
    }

    public void set(int value) {
        this.value = this.convertToHex(Integer.toString(value), NumSystem.DEC);
    }

    public void set(String value, NumSystem numSystem) {
        this.value = this.convertToHex(value, numSystem);
    }

    public int getDec() {
        return this.convertToDec(this.value, NumSystem.HEX);
    }

    public String getBin() {
        return Integer.toBinaryString(this.getDec());
    }

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

    // private method for converting values to hex
    private String convertToHex(String value, NumSystem numSystem) {

        String returnVal = "";
        if (null != numSystem)
            switch (numSystem) {
                case HEX -> returnVal = value;
                case DEC -> returnVal = Integer.toHexString(Integer.parseInt(value));
                case BIN -> returnVal = Integer.toHexString(Integer.parseInt(value, 2));
            }
        return returnVal;
    }

    // private method for converting values to decimal
    private int convertToDec(String value, NumSystem numSystem) {
        int returnVal = 0;
        if (null != numSystem)
            switch (numSystem) {
                case HEX -> returnVal = Integer.parseInt(value, 16);
                case DEC -> returnVal = Integer.parseInt(value);
                case BIN -> returnVal = Integer.parseInt(value, 2);
            }
        return returnVal;
    }
}
