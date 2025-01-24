package util;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;

public class Constant {
    // Areas
    public static final Area GE_AREA = new Area(3160, 3493, 3168, 3486);
    public static final Area RING_OF_WEALTH_RADIUS = new Area(3145, 3458, 3190, 3515);
    
    // Tiles
    public static final Tile GE_CLERK_TILE = new Tile(3164, 3488, 0);
    
    // NPCs
    public static final int GE_CLERK_ID = 2149;
    
    // Items
    public static final String RING_OF_WEALTH = "Ring of wealth";
    
    // Widgets
    public static final int GE_COLLECT_BUTTON_PARENT = 465;
    public static final int GE_COLLECT_BUTTON_GROUP = 6;
    public static final int GE_COLLECT_BUTTON_CHILD = 0;
}