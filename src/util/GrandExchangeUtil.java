package util;

import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.GrandExchangeItem;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.grandexchange.Status;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.wrappers.widgets.WidgetChild;

public class GrandExchangeUtil {

    private static final int MAX_BUY_PRICE_INCREASE = 10; // Percentage above market price
    private static final Area GE_AREA = new Area(3160, 3487, 3168, 3495); // Grand Exchange area

    /**
     * Buys missing gear from the Grand Exchange
     * @return true if all missing gear was purchased
     */
    public static boolean buyMissingGear(String[] requiredGear) {
        // First ensure we're at the GE and it's open
        if (!GrandExchange.isOpen()) {
            Walking.walk(GE_AREA.getCenter());

            if (!GrandExchange.open()) {
                Logger.log("Failed to open Grand Exchange");
                return false;
            }
            Sleep.sleepUntil(GrandExchange::isOpen, 3000);
        }

        boolean allPurchased = true;

        for (String gear : requiredGear) {
            // Skip if we already have it
            if (Equipment.contains(gear) || Inventory.contains(gear) ||
                    BankUtil.isOutOfStock(gear)) {
                continue;
            }

            // Try to buy the item
            if (!buyItem(gear, 1)) {
                allPurchased = false;
                Logger.log("Failed to buy: " + gear);
            }
        }

        return allPurchased;
    }

    /**
     * Attempts to buy an item from the GE with proper checks and validations
     */
    public static boolean buyItem(String itemName, int quantity) {
        // First ensure GE is open
        if (!GrandExchange.isOpen()) {
            if (!GrandExchange.open()) {
                Logger.log("Failed to open Grand Exchange");
                return false;
            }
            Sleep.sleepUntil(GrandExchange::isOpen, 3000);
        }

        // Get price
        int basePrice = LivePrices.getHigh(itemName);
        int buyPrice = (int)(basePrice * 1.1); // 10% above market price

        // Create buy offer
        if (GrandExchange.buyItem(itemName, quantity, buyPrice)) {
            Sleep.sleep(1000, 1500);

            // Wait for completion and collect using widget
            WidgetChild collectButton = Widgets.getWidgetChild(465, 6, 0);
            if (collectButton != null && collectButton.isVisible()) {
                collectButton.interact("Collect to bank");
                Sleep.sleep(600, 800);
                return true;
            }
        }

        return false;
    }
} 