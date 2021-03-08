import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class InstructionConverter {
    public static ArrayList<String> gpRegsAndHLPointer =
            new ArrayList<String>(Arrays.asList("a", "b", "c", "d", "e", "h", "l", "[hl]"));
    public static ArrayList<String> comboRegs = new ArrayList<String>(Arrays.asList("bc", "de", "hl"));
    public static ArrayList<String> weirdPointers = new ArrayList<String>(Arrays.asList("[bc]","[de]","[hli]","[hld]"));
    public static ArrayList<String> flags = new ArrayList<String>(Arrays.asList("z","c","nz","nc"));

    public static int[] Convert(String command, String lhv, String rhv){
        int[] opcode = {255};
        if (command.equals("ld")){
            System.out.println("Load found, lh rh: " + lhv + ", " + rhv);
            //if this is true, it fits in the first 4 rows of instructions and we
            //can programmatically determine the opcode
            if (gpRegsAndHLPointer.contains(lhv) && gpRegsAndHLPointer.contains(rhv)){
                int lhvOffset = gpRegsAndHLPointer.indexOf(lhv) * 8;
                int rhvOffset = gpRegsAndHLPointer.indexOf(rhv);
                int totalOffset = lhvOffset + rhvOffset;
                System.out.printf("Opcode: 0x%02X\n", totalOffset);
                opcode = new int[1];
                opcode[0] = totalOffset;
            }
            else if ((gpRegsAndHLPointer.contains(lhv) && IsInteger(rhv))){
                int lhvOffset = gpRegsAndHLPointer.indexOf(lhv);
                int totalOffset = 0x90 + lhvOffset;
                opcode = new int[2];
                opcode[0] = totalOffset;
                opcode[1] = Integer.parseInt(rhv);
            }
            else if ((comboRegs.contains(lhv)) && IsInteger(rhv)){
                System.out.println("16 bit load found");
                int lhvOffset = comboRegs.indexOf(lhv);
                int totalOffset = 0xEC + lhvOffset;
                opcode = new int[3];
                opcode[0] = totalOffset;
                int value = Integer.parseInt(rhv);
                int lowByte = value & 0xff;
                int highByte = (value >> 8) & 0xff;
                opcode[1] = lowByte;
                opcode[2] = highByte;
            }
            else if(weirdPointers.contains(lhv) || weirdPointers.contains(rhv)){
                int totalOffset = 0xa0;
                if (weirdPointers.contains(rhv)){
                    totalOffset += weirdPointers.indexOf(rhv);
                }
                else{
                    totalOffset += weirdPointers.indexOf(lhv) + 4;
                }
                opcode = new int[1];
                opcode[0] = totalOffset;
            }
        }
        else if (command.equals("add")){
            System.out.println("Add found");
        }
        else if (command.equals("dec")){
            if (gpRegsAndHLPointer.contains(lhv)){
                System.out.println("8bit dec found");
                int totalOffset = 0x88 + gpRegsAndHLPointer.indexOf(lhv);
                opcode = new int[1];
                opcode[0] = totalOffset;
            }
        }
        else if (command.equals("jp") && lhv != null){

        }
        else {
            System.out.println("Unknown operation " + command);
        }

        System.out.print("Opcode: ");
        for (int code : opcode){
            System.out.printf("0x%02X ", code);
        }
        System.out.println("");
        return opcode;
    }

    public static boolean IsInteger(String value){
        try{
            int x = Integer.parseInt(value);
        } catch (NumberFormatException e){
            return false;
        }
        return true;
    }
}
