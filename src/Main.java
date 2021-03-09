import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        //for now, just print the instruction to the screen
        ArrayList<Instruction> instructionSet = new ArrayList<Instruction>();
        instructionSet.addAll(InstructionChunk.generateChunk("ld",
                InstructionChunk.GeneralPurpose8BitRegistersAndHLPointer,
                InstructionChunk.GeneralPurpose8BitRegistersAndHLPointer));
        instructionSet.addAll(InstructionChunk.generateChunk("ld",
                InstructionChunk.GeneralPurpose8BitRegistersAndHLPointer, new String[]{"n8"}));
        instructionSet.addAll(InstructionChunk.generateFlippedChunk("ld",
                new String[]{"A", "A", "A", "A"}, new String[]{"*BC", "*DE", "*HL+", "*HL-"}));
        instructionSet.addAll(InstructionChunk.generateSymmetricalChunks(
                new String[]{"add", "adc", "sub", "sbc"},
                new String[]{"A"}, InstructionChunk.GeneralPurpose8BitRegistersAndHLPointer));
        instructionSet.addAll(InstructionChunk.generateSymmetricalChunks(new String[]{"inc", "dec"},
                 InstructionChunk.GeneralPurpose8BitRegistersAndHLPointer, new String[]{" "}));
        instructionSet.addAll(InstructionChunk.generateSymmetricalChunks(
                new String[]{"add", "sub", "adc", "sbc"},
                new String[]{"A"}, new String[]{"n8", "*HL"}));
        for (int i = 0; i < 4; i++) instructionSet.add(new Instruction("nop"));
        instructionSet.addAll(InstructionChunk.generateSymmetricalChunks(new String[]{"and", "or", "xor", "cmp"},
                 new String[]{"A"}, new String[]{"n8"}));
        instructionSet.addAll(InstructionChunk.generateSymmetricalChunks(new String[]{"and", "or", "xor", "cmp"},
                 new String[]{"A"}, InstructionChunk.GeneralPurpose8BitRegistersAndHLPointer));
        instructionSet.addAll(InstructionChunk.generateChunk("bit",  new String[]{"A"},
                new String[]{"0" , "1", "2", "3", "4", "5", "6", "7"}));
        instructionSet.addAll(InstructionChunk.generateSymmetricalChunks(
                new String[]{"rr", "rl", "rrc", "rlc", "sr", "sl"},  new String[]{"A"}, new String[]{" "}));
        for (int i = 0; i < 2; i++) instructionSet.add(new Instruction("nop"));
        instructionSet.addAll(InstructionChunk.generateFlaggedChunk(new String[]{"jp", "call", "ret"}));
        instructionSet.addAll(InstructionChunk.generateSymmetricalChunks(new String[]{"push", "pop"},
                new String[]{"AF", "BC", "DE", "HL"}, new String[]{" "}));
        instructionSet.addAll(InstructionChunk.generateSymmetricalChunks(new String[]{"inc", "dec"},
                new String[]{"BC", "DE", "HL", "SP"}, new String[]{" "}));
        instructionSet.addAll(InstructionChunk.generateChunk("add",  new String[]{"HL"},
                new String[]{"BC", "DE", "HL", "SP"}));
        instructionSet.addAll(InstructionChunk.generateChunk("ld",
                new String[]{"BC", "DE", "HL", "SP"}, new String[]{"n16"}));
        String[] outliers = new String[]{"jp", "call", "ret", "ei", "di", "halt", "ld a, *n16",
                "ld SP, HL", "nop", "nop", "swap A", "cpl A", "BCD", "nop", "nop", "nop"};
        for (String outlier : outliers){
            instructionSet.add(new Instruction(outlier));
        }

        for (int i = 0; i < 16; i++){
            for (int j = 0; j < 16; j++){
                   System.out.print(instructionSet.get((i*16) + j) + " ");
            }
            System.out.println("");
        }

        //open file
        File file = new File("test.txt");
        ArrayList<String> list = new ArrayList<String>();
        try {
            Scanner input = new Scanner(file);
            while(input.hasNextLine()){
                list.add(input.nextLine());
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        ArrayList<int[]> opcodes = new ArrayList<int[]>();
        for (String line : list){
            System.out.println("Parsing " + line);
            String cleanedLine = line.trim();
            if (cleanedLine.contains(":")){
                System.out.println("Label found");
                InstructionConverter.AddLabel(cleanedLine);
            }
            else {
                String[] split = line.replace(",", "").split(" ");
                String command = split[0];
                String lhv = null;
                String rhv = null;
                if (split.length > 1) lhv = split[1];
                if (split.length > 2) rhv = split[2];
                int[] opcode = InstructionConverter.Convert(command, lhv, rhv);
            }
        }

        try {
            OutputStream outputStream = new FileOutputStream("output.bin");
            for (int[] opcode : opcodes) {
                for (int code : opcode){
                    outputStream.write(code);
                }
            }
            outputStream.close();
        } catch (Exception e) {e.printStackTrace();}


    }
}
