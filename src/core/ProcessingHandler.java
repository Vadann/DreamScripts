package core;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;

public class ProcessingHandler {

    BankHandler bankHandler = new BankHandler();
    public int craft(String itemToWithdraw, String itemToDeposit) {
        if (!Inventory.isFull() && bankHandler.isOutOfStock(itemToWithdraw)) {
            if (Inventory.contains(itemToWithdraw)) {
                bankHandler.depositItem(Inventory.get(itemToWithdraw), true);
                Logger.log("Successfully deposited leftoever items.");
            }
            return -1; // Temporarily
            // startGE(itemToWithdraw, itemToSell);
        }

        // This block handles the case where we need to restock our inventory.
        else if (!Inventory.isFull()) {
            Logger.log("Inventory is not full");
            bankHandler.restock(itemToWithdraw);
        }

        Item knife = Inventory.get("Knife");
        Item fish = Inventory.getItemInSlot(26);

        // Ensures we have a full inventory and non null items before we can start the crafting process
        if (Inventory.isFull() && knife != null && fish != null) {

            // This starts the crafting process
            do {

                if (Inventory.getItemInSlot(26).getName().equals(knife.getName())) {
                    Inventory.drag(knife, 27);
                }
                knife.interact();
                fish = Inventory.getItemInSlot(26);  // Re-Sync the current status of the inventory to avoid a null after slot[26] is cut.

                // This if block prevents a duplicate cut of slot 26 sometimes it executes to fast.
                if(fish != null) {
                    fish.interact("Use");

                }
                else {
                    break;
                }

            } while (fish.getName().equals(itemToWithdraw));

            Logger.log("Finished Crafting...");

            // Crafting is done... So now it will deposit our depositItem into the bank
            Item depositItem = Inventory.get(itemToDeposit);
            bankHandler.depositAndWithdraw(depositItem, itemToWithdraw);
        }
        return 0;
    }
}



