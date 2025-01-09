package Quester.quests;

import lombok.extern.java.Log;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.quest.book.Quest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;

import java.util.HashMap;
import java.util.Map;


@ScriptManifest(name = "ETC", description = "My script description!", author = "Developer Name",
        version = 1.0, category = Category.WOODCUTTING, image = "")
public class ErnestTheChicken extends AbstractScript {

    Quest quest = FreeQuest.ERNEST_THE_CHICKEN;
    Map<String, Integer> dialogues = new HashMap<>();
    public void onStart() {
        dialogues.put("Aha, sounds like a quest. I'll help.", 1);
        if (quest.isFinished()) {
            Logger.log("Quest is already completed");
            stop();
        }
    }

    @Override
    public int onLoop() {
        if(!quest.isStarted()) {
            Logger.log("You need to start the quest");

            Area questStartArea = new Area(3107, 3328, 3112, 3334);

            if(!questStartArea.contains(Players.getLocal())) {
                Logger.log("Walking to the area");
                Walking.walk(questStartArea);
            }

            String npcName = "Veronica";
            if(NPCs.closest(npcName) != null) {
                NPCs.closest(npcName).interact("Talk-to");

            }

            if(Dialogues.inDialogue()) {

                if(Dialogues.canContinue()) {
                    Dialogues.spaceToContinue();
                }

                else if(Dialogues.areOptionsAvailable()) {
                    for(String option : Dialogues.getOptions()) {
                        if (option.equals("Yes")) {
                            Dialogues.chooseOption("Yes");
                        }
                    }
                }
            }
        }
        return 0;
    }




}
