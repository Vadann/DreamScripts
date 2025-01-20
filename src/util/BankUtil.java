package util;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.methods.map.Area;

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
} 