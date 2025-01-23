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
        Item ring = Equipment.getItemInSlot(EquipmentSlot.RING);
        if (ring != null) {
            return Equipment.contains(ring);
        }

        return false;

    }
    
    /**
     * Gets the number of charges remaining on equipped Ring of Wealth
     * @return number of charges, or 0 if no ring equipped
     */
    public static int getWealthRingCharges() {
        if (!hasWealthRing()) {
            return 0;
        }
        
        Item ring = Equipment.getItemInSlot(EquipmentSlot.RING.getSlot());
        if (ring == null) {
            return 0;
        }
        
        // Ring names are like "Ring of wealth (5)" where 5 is charges
        String name = ring.getName();
        if (name.contains("(") && name.contains(")")) {
            try {
                String chargesStr = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
                return Integer.parseInt(chargesStr);
            } catch (Exception e) {
                return 0;
            }
        }
        
        return 0;
    }
} 