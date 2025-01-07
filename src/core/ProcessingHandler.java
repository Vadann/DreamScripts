package core;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;

import GUI.CraftingGUI;

import java.util.HashMap;
import java.util.Map;

public class ProcessingHandler {

    BankHandler bankHandler = new BankHandler();
    public Map<String, String> crafterMap = new HashMap<>();

    public ProcessingHandler() {
        // Initialize the crafterMap with items and their crafted versions
        crafterMap.put("Leaping sturgeon", "Caviar");
        crafterMap.put("Chocolate bar", "Chocolate dust");
    }

    // Pass in CraftingGUI gui so that this method can read data strictly from the user event data.
    // Allows for me to handle swapping between craftable items dynamically
    public int craft(CraftingGUI gui) {
        if (gui.isRunning()) {

            Logger.log(gui.getCraftables().toString());
            ProcessingState processingState = null;

            if (gui.getCraftables() == null || gui.getCraftables().isEmpty()) {
                Logger.log("No craftables available. Exiting.");
                return -1;
            }

            String itemToWithdraw = gui.getCraftables().getFirst();
            String itemToDeposit = crafterMap.getOrDefault(itemToWithdraw, null);

            if (itemToDeposit == null) {
                Logger.log("No corresponding item to deposit for: " + itemToWithdraw);
                return -1;
            }

            Item knife = Inventory.contains("Knife") ? Inventory.get("Knife") : null;

            if (knife == null) {
                Logger.log("Knife is missing. Restocking is required.");
                // Add a restock method for the knife here.
                return -1;
            }

            if (!Inventory.getItemInSlot(27).getName().equals(knife.getName())) {
                Logger.log("Knife is in the wrong slot. Fixing inventory orientation.");
                Inventory.drag(knife, 27);
                Logger.log("Inventory orientation fixed.");
            }

            if (!Inventory.isFull() && bankHandler.isOutOfStock(itemToWithdraw)) {
                processingState = ProcessingState.OUT_OF_STOCK;
            } else if (!Inventory.isFull()) {
                processingState = ProcessingState.RESTOCKING;
            } else {
                processingState = ProcessingState.PROCESSING;
            }

            switch (processingState) {
                case OUT_OF_STOCK:
                    Logger.log("Handling state: OUT OF STOCK");
                    if (Inventory.contains(itemToWithdraw)) {
                        bankHandler.depositItem(Inventory.get(itemToWithdraw), true);
                        Logger.log("Successfully deposited leftover items.");
                    }
                    gui.getCraftables().removeFirst();
                    break;

                case RESTOCKING:
                    Logger.log("Handling state: RESTOCKING for " + itemToWithdraw);
                    bankHandler.restock(itemToWithdraw);
                    break;

                case PROCESSING:
                    Logger.log("Handling state: PROCESSING");
                    Item craftingItem = Inventory.getItemInSlot(26);

                    if (craftingItem == null || knife == null) {
                        Logger.log("Missing crafting items. Exiting processing.");
                        return -1;
                    }

                    int safeGuard = 0;
                    while (craftingItem.getName().equals(itemToWithdraw)) {
                        safeGuard++;

                        if (!craftingItem.getName().equals(itemToWithdraw)) {
                            break;
                        }

                        if (safeGuard > 50) {
                            Logger.log("Exceeded maximum crafting attempts. Exiting loop.");
                            break;
                        }

                        if (!Inventory.getItemInSlot(27).getName().equals(knife.getName())) {
                            Logger.log("Knife is in the wrong slot. Fixing inventory orientation.");
                            Inventory.drag(knife, 27);
                        }

                        knife.interact();

                        craftingItem = Inventory.getItemInSlot(26);
                        if(craftingItem != null) {
                            craftingItem.interact();
                        }

                    }

                    if (craftingItem.getName().equals(itemToDeposit)) {
                        Logger.log("Finished Crafting.");
                        Item depositItem = Inventory.get(itemToDeposit);
                        bankHandler.depositAndWithdraw(depositItem, itemToWithdraw);
                        Logger.log("Finished state: PROCESSING");
                    }
                    // break;

                default:
                    Logger.log("Unknown or null processing state.");
                    break;
            }

            return 0;
        }

        return 0;
    }

}



