// Class: HexNum
// Purpose: This will be a wrapper for all of the logic needed
// for hex numbers.
public class HexNum {
    
    // value holds the hex number
    protected String value;
    public static void main(String[] args) {
        // test suite
        HexNum hexNum = new HexNum();
        hexNum.set(10);
        assert(hexNum.toString().equals("a"));
        hexNum.add(5);
        assert(hexNum.toString().equals("f"));
        hexNum.set("1010", NumSystem.BIN);
        assert(hexNum.toString().equals("a"));
        hexNum.add(5);
        assert(hexNum.toString().equals("f"));
        hexNum.set("a", NumSystem.HEX);
        assert(hexNum.getDec() == 10);
        hexNum.add(5);
        assert(hexNum.getDec() == 15);
        hexNum.set("1010", NumSystem.BIN);
        assert(hexNum.getDec() == 10);


        HexNum hexNum2 = new HexNum("FF", NumSystem.HEX);
        assert(hexNum2.getDec() == 255);
        assert(hexNum2.toString().equals("FF"));
        System.out.println("Success!");
    }

    // constructors
    public HexNum(){
        // default value is 0
        this.value = "0";
    }

    // by default, assume provided ints are in decimal
    public HexNum(int value){
        this.value = Integer.toHexString(value);
    }

    // given a number system and string, convert it to hex
    public HexNum(String value, NumSystem numSystem){
        this.value = convertToHex(value, numSystem);
    }

    public String toString(){
        return this.value;
    }

    // assume int input is in decimal
    public void add(int value){
        int newVal = this.convertToDec(this.value, NumSystem.HEX) + value;
        this.value = this.convertToHex(Integer.toString(newVal), NumSystem.DEC);
    }

    // convert hex values to decimal, then back to hex
    public void add(HexNum value){
        int newVal = this.convertToDec(value.toString(), NumSystem.HEX);
        newVal += this.convertToDec(this.value, NumSystem.HEX);
        this.value = this.convertToHex(Integer.toString(newVal), NumSystem.DEC);
    }

    public void set(int value){
        this.value = this.convertToHex(Integer.toString(value), NumSystem.DEC);
    }

    public void set(String value, NumSystem numSystem){
        this.value = this.convertToHex(value, numSystem);
    }

    public int getDec(){
        return this.convertToDec(this.value, NumSystem.HEX);
    }

    // private method for converting values to hex
    private String convertToHex(String value, NumSystem numSystem){
        
        String returnVal = "";
        if(numSystem == NumSystem.HEX){
            returnVal = value;
        } else if(numSystem == NumSystem.DEC){
            returnVal = Integer.toHexString(Integer.parseInt(value));
        } else if(numSystem == NumSystem.BIN){
            returnVal = Integer.toHexString(Integer.parseInt(value, 2));
        }
        return returnVal;
    }

    // private method for converting values to decimal
    private int convertToDec(String value, NumSystem numSystem){
        int returnVal = 0;
        if(numSystem == NumSystem.HEX){
            returnVal = Integer.parseInt(value, 16);
        } else if(numSystem == NumSystem.DEC){
            returnVal = Integer.parseInt(value);
        } else if(numSystem == NumSystem.BIN){
            returnVal = Integer.parseInt(value, 2);
        }
        return returnVal;
    }
}
