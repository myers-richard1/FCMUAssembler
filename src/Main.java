import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        //open file
        File file = new File("test.txt");

        ArrayList<String> linesOfFile = new ArrayList<String>();
        try {
            Scanner input;
            String fileString = Files.readString(Path.of("test.txt"));
            if (fileString.contains("~")) input = new Scanner(fileString.split("~")[1]);
            else input = new Scanner(file);
            while(input.hasNextLine()){
                linesOfFile.add(input.nextLine());
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        //generate address map
        System.out.println("=======================Label pass");
        parse(linesOfFile, true);
        //generate opcodes
        System.out.println("====================Op pass");
        ArrayList<int[]> opcodes = parse(linesOfFile, false);

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

    public static ArrayList<int[]> parse(ArrayList<String> linesOfFile, boolean labelpass){
        ArrayList<int[]> opcodes = new ArrayList<int[]>();
        for (String line : linesOfFile){
            System.out.println("Parsing " + line);
            String cleanedLine = line.trim();
            if (cleanedLine.equals("")) continue;
            if (cleanedLine.contains(":")){
                if (!labelpass) continue;
                System.out.println("Label found");
                InstructionConverter.AddLabel(cleanedLine);
            }
            else if (cleanedLine.contains("DB")){
                int[] opcode = InstructionConverter.ExecuteDefinition(cleanedLine);
                opcodes.add(opcode);
            }
            else {
                String[] split = line.replace(",", "").split(" ");
                String command = split[0];
                String lhv = null;
                String rhv = null;
                if (split.length > 1) lhv = split[1];
                if (split.length > 2) rhv = split[2];
                int[] opcode = InstructionConverter.Convert(command, lhv, rhv, labelpass);
                opcodes.add(opcode);
            }
        }
        return opcodes;
    }
}
