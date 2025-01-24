package test.util;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import util.TravelUtil;
import util.Constant;

@ScriptManifest(
    name = "Travel Util Tester",
    description = "Tests TravelUtil functions",
    version = 1.0,
    category = Category.MISC,
    author = "Your Name"
)
public class TravelUtilTest extends AbstractScript {

    private enum TestState {
        TEST_TRAVEL_TO_TILE,    // PASS
        TEST_TRAVEL_TO_AREA,
        TEST_TP_TAB,
        FINISHED
    }

    private TestState currentTest = TestState.TEST_TRAVEL_TO_TILE;
    // private static final int POSITION_CHECK_TIMEOUT = 5000;

    @Override
    public int onLoop() {
        switch (currentTest) {
            case TEST_TRAVEL_TO_TILE:
                Logger.log("=== TESTING: Traveling to tile " + Constant.GE_CLERK_TILE.toString());
                boolean traveledToTile = TravelUtil.walkToTile(Constant.GE_CLERK_TILE);
                Logger.log("TESTING: Traveling to tile test result " + traveledToTile);
                currentTest = TestState.FINISHED;
                return 1000;


            case FINISHED:
                Logger.log("All travel util tests completed!");
                stop();
                return -1;
        }
        return 1000;
    }

    @Override
    public void onStart() {
        Logger.log("Starting Travel Util Tests");
        Logger.log("Current position: " + Players.getLocal().getTile().toString());
    }

    @Override
    public void onExit() {
        Logger.log("Travel Util Tests completed");
        Logger.log("Final position: " + Players.getLocal().getTile().toString());
    }
} 