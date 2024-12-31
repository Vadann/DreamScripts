package AutoCraft;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.GrandExchangeItem;
import org.dreambot.api.methods.grandexchange.LivePrices;




@ScriptManifest(name = "Auto Craft", description = "My script description!", author = "Developer Name",
        version = 1.0, category = Category.WOODCUTTING, image = "")
public class AutoSturgeon extends AbstractScript {

    @Override
    public int onLoop() {

        String itemToWithdraw = "Leaping sturgeon";
        String itemToSell = "Caviar";

        // This block checks if we are out of stock, and if so sells what we have and buys more stock.
        if (!Inventory.isFull() && bankIsOutOfStock(itemToWithdraw)) {
            sleep(250);
            stop(); // Temporarily
            // startGE(itemToWithdraw, itemToSell);
        }

        // This block handles the case where we need to restock our inventory.
        else if (!Inventory.isFull()) {
            Logger.log("Inventory is not full");
            bankHandlerEmptyInventory(itemToWithdraw);
        }

        Item knife = Inventory.get("Knife");
        Item fish = Inventory.getItemInSlot(26);

        // Ensures we have a full inventory and non null items before we can start the crafting process
        if (Inventory.isFull() && knife != null && fish != null) {

            // This starts the crafting process
            do {
                knife.interact();
                fish = Inventory.getItemInSlot(26);  // Re-Sync the current status of the inventory to avoid a null after slot[26] is cut.

                // This if block prevents a duplicate cut of slot 26 sometimes it executes to fast.
                if(fish != null) {
                    fish.interact("Use");
                }

            } while (Inventory.contains("Leaping sturgeon"));

            Logger.log("Finished Crafting...");
            sleep(500);

            // Crafting is done... So now it will deposit our depositItem into the bank
            Item depositItem = Inventory.get("Caviar");
            if (!Inventory.contains("Leaping sturgeon")) {
                bankHandler(depositItem, itemToWithdraw);
            }
        }


        return 1000; // Pause for 1 second
    }


    public Boolean bankIsOutOfStock(String checkFor) {
        if (!Bank.isOpen()) {
            Bank.open();
        }

        boolean res = !Bank.contains(checkFor);
        Logger.log("Item you are looking for: " + checkFor + " is out of stock = " + res);
        sleep(2000);
        return res;
    }
    public void bankHandlerEmptyInventory(String itemToWithdraw) {
        if (!Bank.open()) {
            Bank.open();
        }

        Item withdraw = null;

        if (Bank.contains(itemToWithdraw)) {
            withdraw = Bank.get(itemToWithdraw);
        }


        if (withdraw != null) {
            Bank.withdrawAll(withdraw.getName());
        }

        Bank.close();
    }
    public void bankHandler(Item deposit, String itemToWithdraw) {
        // Ensure the bank is open
        Item withdraw = null;

        if (!Bank.isOpen()) {
            Bank.open();

            if (Bank.contains(itemToWithdraw)) {
                withdraw = Bank.get(itemToWithdraw);
            }
        }

        if (deposit != null) {

            Logger.log("Item found depositing all in bank");
            Bank.depositAll(deposit);
        }

        Logger.log("Checking bank to see if withdrawable item is available... ");
        // sleep(1000);
        if (withdraw != null && Bank.contains(withdraw.getName())) {

            Logger.log("Item to withdraw found... withdrawing all");
            Bank.withdrawAll(withdraw.getName());
            // sleep(500);
        } else if (withdraw == null) {
            Logger.log("Cannot withdraw: Item is null.");
            // sleep(5000);
        } else if (!Bank.contains(withdraw.getName())) {
            Logger.log("The item to withdraw is not available in the bank.");
            // sleep(5000);
        }

        Bank.close();
    }

    // This method handles resupplying gold, and restocking item to process.


    public void startGE(String sellingItem, String buyingItem) {
        int MAX_CAP = 1000000;
        int coins = 0;
        int lowPrice = 1000;



        if (!GrandExchange.isOpen()) {
            // Process to SELL item

            Logger.log("Initiating sell process ...");
            sleep(1500);
            Bank.open();

            Logger.log("Checking if the bank contains your selling item");
            sleep(1500);
            if (Bank.contains(sellingItem)) {
                Logger.log("Successfully found selling item... Now withdrawing as a note");
                sleep(1500);
                Bank.setWithdrawMode(BankMode.NOTE);
                sleep(1500);
                Bank.withdrawAll(sellingItem);
                sleep(1500);
                Bank.setWithdrawMode(BankMode.ITEM);

            }
            else {
                Logger.log("You have nothing to sell");
            }

            // Also checks coins while in this menu to make buying easier later.
            Logger.log("Checking bank to see how much coins you have!");
            sleep(250);
            Bank.open();
            if (Bank.contains("Coins")) {
                Logger.log("Coins found");
                sleep(250);
                coins = Bank.get("Coins").getAmount();
                Logger.log(coins);
            }

            else {
                Logger.log("You have no monay :(");
            }

            Logger.log("Closing bank...");
            sleep(250);
            Bank.close();

            GrandExchange.open();
            if (Inventory.contains(sellingItem)) {
                Logger.log("Initiating GE sell process..");
                sleep(1500);


                GrandExchange.sellItem(sellingItem, Inventory.get(sellingItem).getAmount(), lowPrice);

                Logger.log("Successfully listed the offer to sell");
                sleep(1500);

            }

            Logger.log("Initiating buy process... ");

            if (GrandExchange.contains(buyingItem)) {
                Logger.log("Order for this item is already placed");

                Logger.log("Waiting to collect...");
                if(!GrandExchange.isReadyToCollect()) {
                    sleep(5000);
                    Logger.log("Im waiting");
                    GrandExchange.collectToBank();
                }
            }

            else if (coins >= MAX_CAP) {
                GrandExchange.buyItem(
                        buyingItem,
                        (MAX_CAP / LivePrices.getHigh(buyingItem)) / 2,
                        LivePrices.getHigh(buyingItem)
                );

                Logger.log("Successfully placed buy order via ln 358");
            }
            else if (coins > LivePrices.getHigh(buyingItem)) {
                GrandExchange.buyItem(
                        buyingItem,
                        (coins / LivePrices.get(buyingItem)) / 2,
                        LivePrices.getHigh(buyingItem)
                );
                Logger.log("Successfully placed buy order via Ln 367");
            }

            else {
                Logger.log("You cant afford the item you are trying to buy");

            }

            GrandExchange.close();
        }
    }
}
