package core;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;

import GUI.CraftingGUI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessingHandler {

    BankHandler bankHandler = new BankHandler();
    public Map<String, String> crafterMap = new HashMap<>();

    public ProcessingHandler() {
        // Initialize the crafterMap with items and their crafted versions
        crafterMap.put("Leaping sturgeon", "Caviar");
        crafterMap.put("Chocolate bar", "Chocolate dust");
    }

    public int craft(CraftingGUI gui) {
        if (gui.isRunning()) {

            Logger.log(gui.getCraftables().toString());
            ProcessingState proccessingState = null;

            if (gui.getCraftables().isEmpty()) {
                return -1;
            }

            String itemToWithdraw = gui.getCraftables().getFirst();
            String itemToDeposit = crafterMap.get(itemToWithdraw);

            if (!Inventory.isFull() && bankHandler.isOutOfStock(itemToWithdraw)) {
                proccessingState = ProcessingState.OUT_OF_STOCK;
                //return -1; // Temporarily
                // startGE(itemToWithdraw, itemToSell);
            }

            // This block handles the case where we need to restock our inventory.
            else if (!Inventory.isFull()) {
                proccessingState = ProcessingState.RESTOCKING;
            }

            // Ensures we have a full inventory and non null items before we can start the crafting process
            if (Inventory.isFull()) {
                proccessingState = ProcessingState.PROCESSING;
            }


            switch(proccessingState) {
                case OUT_OF_STOCK:
                    Logger.log("Handling state: OUT OF STOCK");

                    if (Inventory.contains(itemToWithdraw)) {
                        bankHandler.depositItem(Inventory.get(itemToWithdraw), true);
                        Logger.log("Successfully deposited leftoever items.");
                    }

                    gui.getCraftables().removeFirst();
                    break;

                case RESTOCKING:
                    Logger.log("Handling state: RESTOCKING" + itemToWithdraw);
                    bankHandler.restock(itemToWithdraw);
                case PROCESSING:
                    Logger.log("Handling state: PROCESSING");
                    // This starts the crafting process
                    Item knife = Inventory.contains("Knife") ? Inventory.get("Knife") : null;

                    Item fish = Inventory.getItemInSlot(26);
                    if (knife != null && fish != null) {

                        while (fish.getName().equals(itemToWithdraw)) {
                            if (!Inventory.getItemInSlot(27).getName().equals(knife.getName())) {
                                Logger.log("Knife is in the wrong slot");
                                Inventory.drag(knife, 27);
                                Logger.log("Fixing inventory orientation and restarting state: PROCESSING");
                                break;
                            }
                            knife.interact();
                            // Logger.log("Interacted with Knife");

                            fish = Inventory.getItemInSlot(26); // Re-Sync the current status of the inventory to avoid a null after slot[26] is cut.

                            // Prevent duplicate cuts by ensuring `fish` is not null.
                            if (fish != null) {
                                fish.interact("Use");
                                // Logger.log("Interacted with fish");
                            } else {
                                Logger.log("Fish is null, breaking loop");
                                break;
                            }
                        }

                    }

                    if (fish != null && fish.getName().equals(itemToDeposit)) {
                        Logger.log("Finished Crafting...");
                        Item depositItem = Inventory.get(itemToDeposit);
                        bankHandler.depositAndWithdraw(depositItem, itemToWithdraw);
                        Logger.log("Finished state: PROCESSING");
                    }



                case null: {
                    break;
                }
            }
        }

        return 0;
    }

}



