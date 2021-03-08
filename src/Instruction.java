public class Instruction {
    private String command;
    private boolean flagged;
    private String leftHandValue;
    private String rightHandValue;
    private boolean leftHandIsPointer;
    private boolean rightHandIsPointer;
    private String finalValue;

    public Instruction(String command, String leftHandValue, String rightHandValue,
                       boolean leftHandIsPointer, boolean rightHandIsPointer){
        this.command = command;
        this.flagged = flagged;
        this.leftHandValue = leftHandValue;
        this.rightHandValue = rightHandValue;
        this.leftHandIsPointer = leftHandIsPointer;
        this.rightHandIsPointer = rightHandIsPointer;
        finalValue = command;
        if (leftHandIsPointer) this.leftHandValue = "[" + this.leftHandValue + "]";
        if (rightHandIsPointer) this.rightHandValue = "[" + this.rightHandValue + "]";
        if (rightHandValue.trim().isEmpty()) finalValue = finalValue + this.leftHandValue;
        else finalValue += this.leftHandValue + "," + this.rightHandValue;
        finalValue = finalValue.trim();
    }

    public Instruction(String command){
        finalValue = command;
    }

    @Override
    public String toString(){
        return finalValue + ";";
    }
}
