import java.lang.reflect.Array;
import java.util.*;

public class InstructionConverter {
    public static ArrayList<String> standardMath =
            new ArrayList<String>(Arrays.asList("add", "adc", "sub", "sbc", "cmp", "and",
                    "or", "xor", "inc", "dec"));
    public static ArrayList<String> stragglers = new ArrayList<String>(Arrays.asList("ei", "di", "halt", "swap", "cpl", "bcd"));
    public static ArrayList<String> flowControl = new ArrayList<String>(Arrays.asList("jp", "call", "ret"));
    public static ArrayList<String> gpRegsAndHLPointer =
            new ArrayList<String>(Arrays.asList("a", "b", "c", "d", "e", "h", "l", "[hl]"));
    public static ArrayList<String> comboRegs = new ArrayList<String>(Arrays.asList("bc", "de", "hl"));
    public static ArrayList<String> comboRegsMath = new ArrayList<String>(Arrays.asList("bc", "de", "hl", "sp"));
    public static ArrayList<String> weirdPointers = new ArrayList<String>(Arrays.asList("[bc]","[de]","[hli]","[hld]"));
    public static ArrayList<String> flags = new ArrayList<String>(Arrays.asList("z","c","nz","nc"));

    public static int index;
    public static HashMap<String, Integer> labelMap = new HashMap<String, Integer>();

    public static int[] Convert(String command, String lhv, String rhv, boolean labelpass){
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
            //immediates
            else if ((gpRegsAndHLPointer.contains(lhv) && IsInteger(rhv))){
                int lhvOffset = gpRegsAndHLPointer.indexOf(lhv);
                int totalOffset = 0x90 + lhvOffset;
                opcode = new int[2];
                opcode[0] = totalOffset;
                opcode[1] = Integer.parseInt(rhv);
            }
            //16 bit immediates
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
            //ld ptr, a
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
        //the five rows of easily parsable math instructions
        else if (standardMath.contains(command)){
            int instructionOffset = 0;
            if (gpRegsAndHLPointer.contains(lhv)) {
                instructionOffset = 0x40 + standardMath.indexOf(command) * 8;
                instructionOffset += gpRegsAndHLPointer.indexOf(lhv);
            }
            else if (comboRegsMath.contains(lhv) && (command.equals("dec") || command.equals("inc"))){
                instructionOffset = 0xE0;
                instructionOffset = command.equals("inc") ? 0xe0 : 0xe4;
                instructionOffset += comboRegsMath.indexOf(lhv);
            }
            else{
                System.out.println("This is math that idk");
            }

            opcode = new int[1];
            opcode[0] = instructionOffset;
        }
        else if (flowControl.contains(command)){
            int offset = 0xC0;
            offset += (flowControl.indexOf(command)) * 8;
            //if there's multiple operands, then lhv is a flag, otherwise it's an address
            String address = lhv;
            if (rhv != null){
                offset+=(flags.indexOf(lhv) + 1);
                address = rhv;
            }
            opcode = new int[3];
            opcode[0] = offset;
            System.out.println("Searcing for address called " + address);
            System.out.println(labelMap.keySet());
            int addressInt = 0;
            if (!labelpass) addressInt = labelMap.get(address);
            int low = addressInt & 0xff;
            int high = (addressInt >> 8) & 0xff;
            opcode[1] = low;
            opcode[2] = high;
        }
        else if (stragglers.contains(command)){
            int offset = stragglers.indexOf(command) + 0xf0;
            opcode = new int[1];
            opcode[0] = offset;
        }
        else {
            System.out.println("Unknown operation " + command);
        }

        System.out.print("Opcode: ");
        for (int code : opcode){
            System.out.printf("0x%02X ", code);
        }
        System.out.println("");
        index += opcode.length;
        System.out.println("Index is now " + index);
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

    public static void AddLabel(String label){
        label = label.replaceAll(":","");
        System.out.println("Adding label " + label + " at index " + (index));
        labelMap.put(label, index);
    }

    public static int[] ExecuteDefinition(String command){
        command = command.replace("DB", "").trim();
        String[] literals = command.split(",");
        int[] bytes = new int[literals.length];
        for (int i = 0; i < literals.length; i++) {
            String literal = literals[i].replace("$", "").trim();
            bytes[i] = Integer.parseInt(literal, 16);
        }
        index += bytes.length;
        System.out.println("Definition is " + bytes.length + " bytes");
        return bytes;
    }
}
