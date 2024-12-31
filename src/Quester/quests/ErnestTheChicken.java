package Quester.quests;

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


@ScriptManifest(name = "ETC", description = "My script description!", author = "Developer Name",
        version = 1.0, category = Category.WOODCUTTING, image = "")
public class ErnestTheChicken extends AbstractScript {

    @Override
    public int onLoop() {
        Quest quest = FreeQuest.ERNEST_THE_CHICKEN;
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
