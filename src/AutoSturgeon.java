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




@ScriptManifest(name = "Script Name", description = "My script description!", author = "Developer Name",
        version = 1.0, category = Category.WOODCUTTING, image = "")
public class AutoSturgeon extends AbstractScript {

    @Override
    public int onLoop() {


        String itemToWithdraw = "Chocolate bar";

        if (!Inventory.isFull() && bankHandlerOutOfStock(itemToWithdraw)) {

            Logger.log("Ln 26");
            sleep(1500);
            buyItemHandler(itemToWithdraw);
        }
        else if (!Inventory.isFull()) {
            Logger.log("Inventory is not full");
            bankHandlerEmptyInventory(itemToWithdraw);
        }

        Item knife = Inventory.get("Knife");
        Item chocolate = Inventory.getItemInSlot(26);

        if (Inventory.isFull() && knife != null && chocolate != null) {
            // Use knife on chocolate bar while inventory contains chocolate bar
            do {
                // Use the knife
                knife.interact();

                // Use the chocolate bar
                chocolate = Inventory.getItemInSlot(26);  // Re-fetch chocolate to ensure it's updated
                chocolate.interact("Use");

            } while (Inventory.contains("Chocolate bar"));

            Logger.log("Finished Crafting...");
            sleep(500);
            // Once chocolate becomes chocolate dust, deposit the item
            Item depositItem = Inventory.get("Chocolate dust");
            if (chocolate != null && chocolate.getName().equals("Chocolate dust")) {
                bankHandler(depositItem, itemToWithdraw);
            }
        }




        /*

        THIS IS A BRAINSTORMED VERSION OF HOW I WILL HANDLE THE GRAND EXCHANGE WHEN I RUN OUT OF ITEMS FROM THE BANK
        Bank.open();

        if (!Bank.contains("Chocolate bar")) {
            Bank.setWithdrawMode(BankMode.NOTE);
            Bank.withdrawAll("Chocolate dust");
            Bank.setWithdrawMode(BankMode.ITEM);
        }

        Bank.close();

        GrandExchange.open();

        if(Inventory.contains("Chocolate dust")) {
            Item item = Inventory.get("Chocolate dust");

            if (item != null) {

                int lowPrice = LivePrices.getLow(item);
                Logger.log(lowPrice);

                GrandExchange.sellItem(Inventory.get("Chocolate dust").getID(), item.getAmount(), lowPrice );
            }

        }

         */

        return 1000; // Pause for 1 second
    }


    public void useOnOtherItem(Item one, Item two) {
        if (Inventory.contains(one) && Inventory.contains(two)) {
            one.useOn(two);
        }

        else {
            Logger.log("One of the items was not found in the inventory");
        }
    }

    public Boolean bankHandlerOutOfStock(String checkFor) {
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

        else {
            Logger.log("No more instances of the item you want to withdraw are left");
            stop();
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
        sleep(1000);
        if (withdraw != null && Bank.contains(withdraw.getName())) {

            Logger.log("Item to withdraw found... withdrawing all");
            Bank.withdrawAll(withdraw.getName());
            sleep(500);
        } else if (withdraw == null) {
            Logger.log("Cannot withdraw: Item is null.");
            sleep(5000);
        } else if (!Bank.contains(withdraw.getName())) {
            Logger.log("The item to withdraw is not available in the bank.");
            sleep(5000);
        }

        Bank.close();
    }

    public void buyItemHandler(String item) {
        int MAX_CAP = 2000000;
        int coins = 0;
        int finalAmount = 0;
        int highPrice = LivePrices.getHigh(item);

        if (!GrandExchange.isOpen()) {

            Logger.log("Checking bank to see how much coins you have!");
            sleep(2000);
            Bank.open();
            if (Bank.contains("Coins")) {
                Logger.log("Coins found");
                sleep(2000);
                coins = Bank.get("Coins").getAmount();
            }

            else {
                Logger.log("You have no monay :(");
            }

            Logger.log("You have this many coins: " + coins);
            sleep(2000);
            Bank.close();
            GrandExchange.open();
        }

        Logger.log("Initiating buy process");
        if (GrandExchange.contains(item)) {
            Logger.log("Order for this item is already placed");
        }
        else if (coins >= MAX_CAP) {
            finalAmount = MAX_CAP / highPrice;
            GrandExchange.buyItem(
                    item,
                    finalAmount,
                    highPrice
            );

            Logger.log("Successfully placed buy order");
        }
        else if (coins > highPrice) {
            finalAmount = coins / highPrice;
            GrandExchange.buyItem(
                    item,
                    finalAmount,
                    highPrice
            );
            Logger.log("Successfully placed buy order");
        }

        else {
            Logger.log("You cant afford the item you are trying to buy");

            // CAN PASS IN A SELL HANDLER
        }

        while (GrandExchange.isOpen()) {
            if (GrandExchange.isReadyToCollect()) {
                Logger.log("Ready to collect items from Grand Exchange.");
                GrandExchange.collectToBank();
                sleep(1000); // Ensure proper collection before closing
                break;
            } else {
                Logger.log("Waiting for the order to complete...");
                sleep(5000); // Sleep and recheck
            }
        }

        GrandExchange.close();
    }


}
