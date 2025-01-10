package core;

import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.utilities.Logger;

import java.util.Map;

public class DialogueHandler {

    public void processDialogueNoOptions() {
        while(Dialogues.inDialogue()) {
            Dialogues.spaceToContinue();
        }
    }

    public void testDialogue(Map<String, Integer> dialogueMap) {
        while (Dialogues.inDialogue()) {

        }
    }
    public void processDialogue(Map<String, Integer> dialogueMap) {
        while(Dialogues.inDialogue()) {
            if (Dialogues.canContinue()) {
                Logger.log("Pressing space to continue");
                Dialogues.spaceToContinue();
                continue;  // Skip to the next iteration of the loop
            }

            if (Dialogues.areOptionsAvailable()) {
                String[] options = Dialogues.getOptions();

                assert options != null;  // Ensure options are not null
                for (String option : options) {
                    Logger.log(option);
                    if (dialogueMap.containsKey(option)) {

                        Logger.log("Choosing correct option.");
                        Dialogues.chooseOption(dialogueMap.get(option));
                        break;  // Choose the first matching option and exit the loop
                    }
                }
            }
        }
    }

}
