package core;

import org.dreambot.api.methods.dialogues.Dialogues;

import java.util.Map;

public class DialogueHandler {

    public void processDialogue(Map<String, Integer> dialogueMap) {
        while(Dialogues.inDialogue()) {
            if (Dialogues.canContinue()) {
                Dialogues.spaceToContinue();
            }

            else if (Dialogues.areOptionsAvailable()) {
                String[] options = Dialogues.getOptions();

                if (options != null) {
                    for (String option : options) {
                        if (dialogueMap.get(option) != null) {
                            Dialogues.chooseOption(dialogueMap.get(option));
                        }
                    }
                }
            }
        }
    }

}
