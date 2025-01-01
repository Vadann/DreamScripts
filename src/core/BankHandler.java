package core;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.utilities.Logger;

public class BankHandler {

    /*
        Checks if the givem item is in the bank
        Returns a boolean
     */
    public boolean isOutOfStock(String itemName) {
        if (!Bank.isOpen()) {
            Bank.open();
        }
        boolean outOfStock = !Bank.contains(itemName);
        Logger.log("Item \"" + itemName + "\" is out of stock: " + outOfStock);
        Bank.close();
        return outOfStock;
    }

    /*
        Method to handle if inventory needs to be restocked with any item

     */
    public void restock(String itemToWithdraw) {
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

    /*
        Method to handle only depositing an item
     */
    public void depositItem(Item item) {
        if (!Bank.isOpen()) {
            Bank.open();
        }

        if (Inventory.contains(item)) {
            Bank.depositAll(item);
        }

        Bank.close();
    }

    /*
        Method to see if the bank needs to be closed after depositing
     */
    public void depositItem(Item item, boolean close) {
        if (!Bank.isOpen()) {
            Bank.open();
        }

        if (Inventory.contains(item)) {
            Bank.depositAll(item);
        }

        if(close) {
            Bank.close();
        }

    }

    /*
        Method to handle only withdrawing an item
     */
    public void withdrawItem(String itemName) {
        if (!Bank.isOpen()) {
            Bank.open();
        }
        if (Bank.contains(itemName)) {
            Bank.withdrawAll(itemName);
            Logger.log("Withdrew all: " + itemName);
        } else {
            Logger.log("Item not found in bank: " + itemName);
        }
        Bank.close();
    }

    /*
        Method to see if the bank needs to be closed after withdrawing
     */
    public void withdrawItem(String itemName, boolean close) {
        if (!Bank.isOpen()) {
            Bank.open();
        }
        if (Bank.contains(itemName)) {
            Bank.withdrawAll(itemName);
            Logger.log("Withdrew all: " + itemName);
        } else {
            Logger.log("Item not found in bank: " + itemName);
        }

        if(close) {
            Bank.close();
        }
    }

    /*
        Method uses previous helper methods to deposit and withdraw
     */
    public void depositAndWithdraw(Item depositItem, String withdrawItem) {
        if (!Bank.isOpen()) {
            Bank.open();
        }
        depositItem(depositItem, false);
        withdrawItem(withdrawItem, true);
    }
}
