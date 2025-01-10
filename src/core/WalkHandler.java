package core;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Player;

import static org.dreambot.api.utilities.Sleep.sleep;
import static org.dreambot.api.utilities.Sleep.sleepUntil;

public class WalkHandler {
    public void moveToArea(Area area) {
        Player player = Players.getLocal();
        if (!area.contains(player)) {
            Tile targetTile = area.getRandomTile();

            if (targetTile.distance() > 5 && Walking.shouldWalk()) {
                Walking.walk(targetTile);
                sleepUntil(player::isMoving, 2500);
                sleep(Calculations.random(300, 600));
            }
        }
    }
}
