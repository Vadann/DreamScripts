package util;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;

public class TravelUtil {
    
    private static final int DEFAULT_WALK_TIMEOUT = 5000; // 5 seconds timeout

    /**
     * Attempts to use a teleport tablet
     * @param tabletName Name of the teleport tablet (e.g., "Varrock teleport")
     * @return true if teleport was successful
     */
    public static boolean useTeleportationTablet(String tabletName) {
        if (!Inventory.contains(tabletName)) {
            Logger.log("No " + tabletName + " found in inventory");
            return false;
        }

        Tile beforeTele = Players.getLocal().getTile();
        if (Inventory.interact(tabletName, "Break")) {
            Sleep.sleepUntil(() -> !Players.getLocal().getTile().equals(beforeTele), 5000);
            Logger.log("Used " + tabletName);
            Sleep.sleep(600, 800); // Wait for teleport animation
            return true;
        }
        return false;
    }

    /**
     * Attempts to use equipped item teleport
     * @param slot Equipment slot to use
     * @param location Teleport option to select (e.g., "Grand Exchange")
     * @return true if teleport was successful
     */
    public static boolean useEquipmentTeleport(EquipmentSlot slot, String location) {
        if (!Equipment.contains(slot)) {
            Logger.log("No teleport equipment in " + slot.toString());
            return false;
        }

        Tile beforeTele = Players.getLocal().getTile();
        if (Equipment.interact(slot, location)) {
            Sleep.sleepUntil(() -> !Players.getLocal().getTile().equals(beforeTele), 5000);
            Logger.log("Used " + slot.toString() + " teleport to " + location);
            Sleep.sleep(600, 800); // Wait for teleport animation
            return true;
        }
        return false;
    }

    /**
     * Walks to a specific tile
     * @param destination The tile to walk to
     * @return true if reached destination
     */
    public static boolean walkToTile(Tile destination) {
        if (Players.getLocal().getTile().equals(destination)) {
            return true;
        }

        Walking.walk(destination);
        Logger.log("Walking to tile: " + destination.toString());
        return Sleep.sleepUntil(() -> Players.getLocal().getTile().equals(destination), DEFAULT_WALK_TIMEOUT);
    }

    /**
     * Walks to an area
     * @param area The area to walk to
     * @return true if reached the area
     */
    public static boolean walkToArea(Area area) {
        if (area.contains(Players.getLocal())) {
            return true;
        }

        Walking.walk(area.getCenter());
        Logger.log("Walking to area center: " + area.getCenter().toString());
        return Sleep.sleepUntil(() -> area.contains(Players.getLocal()), DEFAULT_WALK_TIMEOUT);
    }
} 