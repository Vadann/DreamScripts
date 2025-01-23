package util;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.GrandExchangeItem;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.grandexchange.Status;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.ArrayList;
import java.util.List;

public class GrandExchangeUtil {

    private static final int MAX_BUY_PRICE_INCREASE = 10; // Percentage above market price
    private static final Area GE_AREA = new Area(3160, 3493, 3168, 3486); // Grand Exchange area
    private static final Area RING_OF_WEALTH_RADIUS = new Area(3145, 3458, 3190, 3515);
    private static final int GE_CLERK_ID = 2149;
    private static final Tile GE_CLERK_TILE = new Tile(3164, 3488, 0);

    /**
     * Buys missing gear from the Grand Exchange
     * @return true if all missing gear was purchased
     */
    public static boolean buyMissingGear(String[] requiredGear) {
        // First create list of items we actually need to buy
        List<String> itemsToBuy = new ArrayList<>();
        for (String gear : requiredGear) {
            if (!Equipment.contains(gear) && !Inventory.contains(gear) &&
                    !BankUtil.isOutOfStock(gear)) {
                itemsToBuy.add(gear);
            }
        }

        // If nothing to buy, return true
        if (itemsToBuy.isEmpty()) {
            Logger.log("No items to buy");
            return true;
        }

        // Now handle the GE operations
        if (!GrandExchange.isOpen()) {
            if (!travelToGE()) {
                Logger.log("Failed to reach Grand Exchange");
                return false;
            }
            Sleep.sleepUntil(GrandExchange::isOpen, 3000);
        }

        // Buy all items before collecting
        boolean allPurchased = true;
        for (String gear : itemsToBuy) {
            if (!createBuyOffer(gear, 1)) {
                allPurchased = false;
                Logger.log("Failed to buy: " + gear);
            }
        }

        // Collect all items at once
        Sleep.sleep(1000, 1500);
        WidgetChild collectButton = Widgets.getWidgetChild(465, 6, 0);
        if (collectButton != null && collectButton.isVisible()) {
            collectButton.interact("Collect to bank");
            Sleep.sleep(600, 800);
        }

        // Close GE after all operations
        GrandExchange.close();
        Sleep.sleep(600, 800);

        return allPurchased;
    }

    // Separated buy offer creation from collection
    private static boolean createBuyOffer(String itemName, int quantity) {
        // Get price
        int basePrice = LivePrices.getHigh(itemName);
        int buyPrice = (int)(basePrice * 1.1); // 10% above market price

        // Create buy offer
        return GrandExchange.buyItem(itemName, quantity, buyPrice);
    }

    // For single item purchases
    public static boolean buyItem(String itemName, int quantity) {
        if (!GrandExchange.isOpen()) {
            if (!travelToGE()) {
                Logger.log("Failed to reach Grand Exchange");
                return false;
            }
            Sleep.sleepUntil(GrandExchange::isOpen, 3000);
        }

        boolean success = createBuyOffer(itemName, quantity);

        if (success) {
            Sleep.sleep(1000, 1500);
            WidgetChild collectButton = Widgets.getWidgetChild(465, 6, 0);
            if (collectButton != null && collectButton.isVisible()) {
                collectButton.interact("Collect to bank");
                Sleep.sleep(600, 800);
            }
        }

        // Close GE after operation
        GrandExchange.close();
        Sleep.sleep(600, 800);

        return success;
    }

    public static boolean travelToGE() {

        // First try Ring of Wealth teleport if available
        if (!RING_OF_WEALTH_RADIUS.contains(Players.getLocal()) && EquipmentUtil.hasWealthRing() && EquipmentUtil.getWealthRingCharges() > 0) {
            Equipment.interact(EquipmentSlot.RING, "Grand Exchange");
            Sleep.sleepUntil(() -> GE_AREA.contains(Walking.getDestination()), 5000);

            Logger.log("Used Ring of Wealth to Travel to GE");
            // return true;
        }
        
        // If no ring or teleport failed, walk normally
        if (!GE_AREA.contains(Walking.getDestination())) {
            Walking.walk(GE_CLERK_TILE);
            Logger.log("Traveling to GE");
            Sleep.sleepUntil(() -> GE_AREA.contains(Walking.getDestination()), 5000);
        }
        
        // Interact with GE clerk
        NPC clerk = NPCs.closest(npc -> npc.getID() == GE_CLERK_ID && 
                               GE_CLERK_TILE.equals(npc.getTile()));
        return clerk != null && clerk.interact("Exchange");
    }
} 