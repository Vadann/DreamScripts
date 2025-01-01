package AutoCraft;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.input.mouse.MouseSettings;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;

import core.BankHandler;
import org.dreambot.api.wrappers.widgets.Menu;

import java.util.ArrayList;
import java.util.List;


@ScriptManifest(name = "Auto Craft", description = "My script description!", author = "Developer Name",
        version = 1.0, category = Category.WOODCUTTING, image = "")
public class AutoSturgeon extends AbstractScript {
    BankHandler bankHandler = new BankHandler();
    Area area = new Area(3161, 3493, 3167, 3487);
    int currentItemIndex = 0;
    public void onStart() {
        Logger.log(MouseSettings.getSpeed());
        MouseSettings.setSpeed(75);

        if (!area.contains(Players.getLocal())) {
            Walking.walk(area.getRandomTile());

        }

        if (Bank.isOpen()) {
            Bank.close();
            Logger.log("Closing Bank");
        }

        if (GrandExchange.isOpen()) {
            GrandExchange.close();
        }
    }
    @Override
    public int onLoop() {

        String itemToWithdraw = "Leaping sturgeon";
        String itemToSell = "Caviar";

        // This block checks if we are out of stock, and if so sells what we have and buys more stock.
        if (!Inventory.isFull() && bankHandler.isOutOfStock(itemToWithdraw)) {
            sleep(250);
            stop(); // Temporarily
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
            sleep(500);

            // Crafting is done... So now it will deposit our depositItem into the bank
            Item depositItem = Inventory.get("Caviar");
            bankHandler.depositAndWithdraw(depositItem, itemToWithdraw);
        }

        return 1000; // Pause for 1 second
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
