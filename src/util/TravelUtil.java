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
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.wrappers.interactive.Entity;
import java.util.Arrays;
import java.util.Comparator;

public class TravelUtil {
    
    private static final int DEFAULT_WALK_TIMEOUT = 5000; // 5 seconds timeout
    private static final int MAX_WALKABLE_ATTEMPTS = 50;

    /**
     * Walks to a random tile in an area
     * @param area The area to walk to
     * @return true if inside area already, otherwise walks to random tile and returns false
     */
    public static boolean walkToArea(Area area) {
        if (area.contains(Players.getLocal())) {
            return true;
        }
        
        Tile walkableTile = getWalkableTileInArea(area, MAX_WALKABLE_ATTEMPTS);
        if (walkableTile == null) {
            Logger.log("Could not find walkable tile in area");
            return false;
        }
        
        return walkToTile(walkableTile);
    }

    /**
     * Walks to an area around a tile with given radius
     */
    public static boolean walkToArea(Tile tile, int radius) {
        Area circle = tile.getArea(radius);
        return walkToArea(circle);
    }

    /**
     * Recursively tries to find a walkable tile within an area
     */
    private static Tile getWalkableTileInArea(Area area, int tries) {
        if (tries <= 0) {
            return area.getRandomTile();
        }

        Tile tile = Map.getWalkable(area.getRandomTile());
        if (tile == null) {
            return null;
        }

        if (!area.contains(tile)) {
            tile = getWalkableTileInArea(area, --tries);
        }

        return tile;
    }

    /**
     * Walks to a specific tile
     * @param destination The tile to walk to
     * @param exactLocation If true, requires exact tile match; if false, allows being near the tile
     * @return true if reached destination according to exactLocation parameter
     */
    public static boolean walkToTile(Tile destination, boolean exactLocation) {
        final int ACCEPTABLE_DISTANCE = 5;

        if (exactLocation) {
            // Check for exact match
            if (Players.getLocal().getTile().equals(destination)) {
                Logger.log("Already at exact destination");
                return true;
            }

            Walking.walk(destination);
            Logger.log("Walking to exact tile: " + destination.toString());
            return Sleep.sleepUntil(() -> Players.getLocal().getTile().equals(destination), DEFAULT_WALK_TIMEOUT);
        } else {
            // Check for nearby
            if (Players.getLocal().getTile().distance(destination) <= ACCEPTABLE_DISTANCE) {
                Logger.log("Already at or near destination");
                return true;
            }

            Walking.walk(destination);
            Logger.log("Walking near tile: " + destination.toString());
            return Sleep.sleepUntil(
                () -> Players.getLocal().getTile().distance(destination) <= ACCEPTABLE_DISTANCE,
                DEFAULT_WALK_TIMEOUT
            );
        }
    }

    /**
     * Walks to a specific tile (non-exact matching)
     * @param destination The tile to walk to
     * @return true if reached destination or close enough
     */
    public static boolean walkToTile(Tile destination) {
        // Default to non-exact matching for backward compatibility
        return walkToTile(destination, false);
    }

    /**
     * Walks to coordinates
     */
    public static boolean walkToTile(int x, int y, int z) {
        return walkToTile(new Tile(x, y, z));
    }

    /**
     * Walks to 2D coordinates (ground level)
     */
    public static boolean walkToTile(int x, int y) {
        return walkToTile(new Tile(x, y, 0));
    }

    /**
     * Walks to an entity's tile
     */
    public static boolean walkToEntity(Entity entity) {
        if (entity == null) {
            Logger.log("Entity is null");
            return false;
        }
        return walkToTile(entity.getTile());
    }

    /**
     * Gets the closest area from a list of areas
     */
    public static Area getClosestArea(Area... areas) {
        return Arrays.stream(areas)
            .min(Comparator.comparingInt(area -> 
                (int) area.getCenter().distance(Players.getLocal().getTile())))
            .orElse(null);
    }

} 