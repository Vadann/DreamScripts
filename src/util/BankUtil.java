package util;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.items.Item;

public class BankUtil {

    private static final int BANK_INTERACTION_RADIUS = 5;

    public static boolean dumpAllToBank() {
        if (!Bank.isOpen()) {
            if (!walkToNearestBank()) {
                Logger.log("Failed to reach bank");
                return false;
            }
            
            if (!Bank.open()) {
                Logger.log("Failed to open bank");
                return false;
            }
            
            Sleep.sleepUntil(Bank::isOpen, 5000);
        }

        // Deposit inventory first
        if (Inventory.contains(item -> true)) {  // Check if inventory has any items
            Bank.depositAllItems();  // Changed from depositAllItems to depositAll
            Sleep.sleepUntil(() -> !Inventory.contains(item -> true), 3000);
            
            if (Inventory.contains(item -> true)) {
                Logger.log("Failed to deposit inventory items");
                return false;
            }
        }

        // Now deposit equipment
        if (!Equipment.isEmpty()) {
            Bank.depositAllEquipment();
            Sleep.sleepUntil(Equipment::isEmpty, 3000);
            
            if (!Equipment.isEmpty()) {
                Logger.log("Failed to deposit equipment");
                return false;
            }
        }

        // Logger.log("Successfully deposited all items and equipment");
        return true;
    }

    private static boolean walkToNearestBank() {
        BankLocation nearestBank = BankLocation.getNearest();
        if (nearestBank == null) {
            Logger.log("No bank found nearby");
            return false;
        }

        Area bankArea = nearestBank.getArea(BANK_INTERACTION_RADIUS);
        if (!bankArea.contains(Walking.getDestination())) {
            Walking.walk(nearestBank.getCenter());
        }

        return Sleep.sleepUntil(() -> bankArea.contains(Walking.getDestination()), 10000);
    }

    public static boolean hasDepositedEverything() {
        return !Inventory.contains(item -> true) && Equipment.isEmpty();
    }

    /**
     * Checks if an item is out of stock in the bank
     * @param itemName The name of the item to check
     * @return true if the item count in bank is 0
     */
    public static boolean isOutOfStock(String itemName) {
        if (!Bank.isOpen() && !Bank.open()) {
            return true; // Consider out of stock if we can't check bank
        }
        return Bank.count(itemName) == 0;
    }

    /**
     * Checks if player has enough coins in bank or inventory
     * @param amount The amount of coins needed
     * @return true if player has enough coins
     */
    public static boolean hasEnoughCoins(int amount) {
        int inventoryCoins = Inventory.count("Coins");
        if (inventoryCoins >= amount) {
            return true;
        }

        if (!Bank.isOpen() && !Bank.open()) {
            return false;
        }

        int bankCoins = Bank.count("Coins");
        return (inventoryCoins + bankCoins) >= amount;
    }

    /**
     * Gets specified amount of an item from bank
     * @param itemName The name of the item to get
     * @param amount The amount to withdraw
     * @return true if successfully retrieved the item
     */
    public static boolean getItemFromBank(String itemName, int amount) {
        if (!Bank.isOpen() && !Bank.open()) {
            Logger.log("Failed to open bank");
            return false;
        }

        // Check if we have enough of the item
        if (Bank.count(itemName) < amount) {
            Logger.log("Not enough " + itemName + " in bank");
            return false;
        }

        // If inventory is full, deposit everything first
        if (Inventory.isFull() && !itemName.equals("Coins")) {
            Bank.depositAllItems();
            Sleep.sleepUntil(() -> !Inventory.isFull(), 3000);
        }

        // Withdraw the item
        boolean success = Bank.withdraw(itemName, amount);
        Sleep.sleepUntil(() -> Inventory.contains(itemName), 3000);

        return success && Inventory.contains(itemName);
    }

    /**
     * Gets as many of the specified item from bank as possible
     * @param itemName The name of the item to get
     * @return true if successfully retrieved any amount of the item
     */
    public static boolean getItemFromBank(String itemName) {
        if (!Bank.isOpen() && !Bank.open()) {
            Logger.log("Failed to open bank");
            return false;
        }

        // Check if the item exists in bank
        if (Bank.count(itemName) == 0) {
            Logger.log("No " + itemName + " found in bank");
            return false;
        }

        // If inventory is full, deposit everything first (except coins for convenience)
        if (Inventory.isFull() && !itemName.equals("Coins")) {
            Bank.depositAllItems();
            Sleep.sleepUntil(() -> !Inventory.isFull(), 3000);
        }

        // Withdraw as many as possible
        boolean success = Bank.withdrawAll(itemName);
        Sleep.sleepUntil(() -> Inventory.contains(itemName), 3000);

        return success && Inventory.contains(itemName);
    }
} 