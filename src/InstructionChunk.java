import java.lang.reflect.Array;
import java.util.ArrayList;

public class InstructionChunk {
    public static String[] GeneralPurpose8BitRegisters = {"A", "B", "C", "D", "E", "H", "L"};
    public static String[] GeneralPurpose8BitRegistersAndHLPointer = {"A", "B", "C", "D", "E", "H", "L", "*HL"};
    public static String[] GeneralPurpose8BitRegistersAndHLPointerAndImmediate8BitValue =
            {"A", "B", "C", "D", "E", "H", "L", "*HL", "n8"};

    public static String[] RegisterCombos = {"AF", "BC", "DE", "HL"};
    public static String[] Assignable16BitRegisters = {"BC", "DE", "HL", "SP"};

    public static ArrayList<Instruction> generateChunk(String command, String[] leftHandValues,
                                           String[] rightHandValues){
        ArrayList<Instruction> toReturn = new ArrayList<Instruction>();
        for (String leftHandValue : leftHandValues){
            for (String rightHandValue : rightHandValues){
                boolean leftHandIsPointer = leftHandValue.contains("*");
                boolean rightHandIsPointer = rightHandValue.contains("*");
                String lhv = leftHandValue.replace("*", "");
                String rhv = rightHandValue.replace("*", "");
                toReturn.add(new Instruction(command, lhv, rhv, leftHandIsPointer, rightHandIsPointer));
            }
        }
        return toReturn;
    }

    public static ArrayList<Instruction> generateFlippedChunk(String command,
                                                              String[] leftHandValues, String[] rightHandValues){
        ArrayList<Instruction> toReturn = new ArrayList<Instruction>();
        for (int i = 0; i < leftHandValues.length; i++){
            String leftHandValue = leftHandValues[i];
            String rightHandValue = rightHandValues[i];
            boolean leftHandIsPointer = leftHandValue.contains("*");
            boolean rightHandIsPointer = rightHandValue.contains("*");
            String lhv = leftHandValue.replace("*", "");
            String rhv = rightHandValue.replace("*", "");
            toReturn.add(new Instruction(command, lhv, rhv, leftHandIsPointer, rightHandIsPointer));
            toReturn.add(new Instruction(command, rhv, lhv, rightHandIsPointer, leftHandIsPointer));
        }
        return toReturn;
    }

    public static ArrayList<Instruction> generateSymmetricalChunks(String[] commands,
                                                                   String[] leftHandValues, String[] rightHandValues){
        ArrayList<Instruction> toReturn = new ArrayList<Instruction>();
        for (String command : commands){
            toReturn.addAll(generateChunk(command, leftHandValues, rightHandValues));
        }
        return toReturn;
    }

    public static ArrayList<Instruction> generateFlaggedChunk(String[] commands){
        ArrayList<Instruction> toReturn = new ArrayList<Instruction>();
        String[] flags = {"Z", "N", "H", "C"};
        for (String command : commands){
            for (int i = 0; i < 4; i++) {
                toReturn.add(new Instruction(command + " " + flags[i]));
            }
            for (int i = 0; i < 4; i++){
                toReturn.add(new Instruction(command + " N" + flags[i]));
            }
        }
        return toReturn;
    }
}
