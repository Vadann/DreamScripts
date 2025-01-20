package util;

import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;

public class EquipmentUtil {
    
    /**
     * Checks if all required gear is equipped
     */
    public static boolean hasRequiredGearEquipped(String[] requiredGear) {
        for (String gear : requiredGear) {
            if (!Equipment.contains(gear)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Attempts to equip all required gear from inventory
     * @return true if all gear was equipped, false if some items are missing
     */
    public static boolean equipFromInventory(String[] requiredGear) {
        boolean allEquipped = true;
        
        for (String gear : requiredGear) {
            if (!Equipment.contains(gear) && Inventory.contains(gear)) {
                Item item = Inventory.get(gear);
                if (item != null) {
                    // Try each possible action one at a time
                    if (item.interact("Wield") || item.interact("Wear") || item.interact("Equip")) {
                        Sleep.sleepUntil(() -> Equipment.contains(gear), 3000);
                    } else {
                        allEquipped = false;
                        Logger.log("Failed to equip: " + gear);
                    }
                }
            } else if (!Equipment.contains(gear) && !Inventory.contains(gear)) {
                allEquipped = false;
            }
        }
        
        return allEquipped;
    }

    /**
     * Gets required gear from bank and equips it
     * @return true if all gear was obtained and equipped
     */
    public static boolean getAndEquipFromBank(String[] requiredGear) {
        // First check what we're missing
        for (String gear : requiredGear) {
            if (!Equipment.contains(gear) && !Inventory.contains(gear)) {
                if (!BankUtil.getItemFromBank(gear, 1)) {
                    Logger.log("Failed to get from bank: " + gear);
                    return false;
                }
            }
        }
        
        // Now try to equip everything
        return equipFromInventory(requiredGear);
    }

    /**
     * Comprehensive method to ensure all required gear is equipped
     * Will try inventory -> bank -> GE in that order
     */
    public static boolean ensureGearEquipped(String[] requiredGear) {
        // First check if we already have everything equipped
        if (hasRequiredGearEquipped(requiredGear)) {
            return true;
        }

        // Try to equip from inventory
        if (equipFromInventory(requiredGear)) {
            return true;
        }

        // Try to get from bank and equip
        if (getAndEquipFromBank(requiredGear)) {
            return true;
        }

        // If all else fails, try to buy from GE
        Logger.log("Missing gear - attempting to buy from Grand Exchange");
        if (GrandExchangeUtil.buyMissingGear(requiredGear)) {
            return getAndEquipFromBank(requiredGear);
        }

        return false;
    }

    /**
     * Checks if wealth ring is equipped
     */
    public static boolean hasWealthRing() {
        return Equipment.contains("Ring of wealth");
    }
    
    /**
     * Gets wealth ring charges
     */
    public static int getWealthRingCharges() {
        // Implementation to check charges
        return -1; // TODO: Implement charge checking
    }
} 