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
        TEST_EQUIPMENT_TP,
        TEST_TP_TAB,    // PASS
        VERIFY_POSITION,
        FINISHED
    }

    private TestState currentTest = TestState.TEST_TP_TAB;
    // private static final int POSITION_CHECK_TIMEOUT = 5000;

    @Override
    public int onLoop() {
        switch (currentTest) {

            case TEST_EQUIPMENT_TP:
                Logger.log("=== Testing: Ring of Wealth Teleport ===");
                if (TravelUtil.useEquipmentTeleport(EquipmentSlot.RING, "Grand Exchange")) {
                    Logger.log("Successfully used Ring of Wealth teleport");
                    currentTest = TestState.FINISHED;
                } else {
                    Logger.log("Failed to use Ring of Wealth teleport");
                    currentTest = TestState.FINISHED;
                }
                return 1000;

            case TEST_TP_TAB:
                Logger.log("=== Testing: Varrock Teleport Tablet ===");
                if (TravelUtil.useTeleportationTablet("Varrock teleport")) {
                    Logger.log("Successfully used Varrock teleport");
                    currentTest = TestState.FINISHED;
                } else {
                    Logger.log("Failed to use Varrock teleport");
                    currentTest = TestState.FINISHED;
                }
                return 1000;

            case VERIFY_POSITION:
                Logger.log("=== Verifying Final Position ===");
                logCurrentPosition();
                currentTest = TestState.FINISHED;
                return 1000;

            case FINISHED:
                Logger.log("All travel util tests completed!");
                stop();
                return -1;
        }
        return 1000;
    }

    private void logCurrentPosition() {
        Logger.log("Current position: " + Players.getLocal().getTile().toString());
    }

    @Override
    public void onStart() {
        Logger.log("Starting Travel Util Tests");
        logCurrentPosition();
    }

    @Override
    public void onExit() {
        Logger.log("Travel Util Tests completed");
        logCurrentPosition();
    }
} 