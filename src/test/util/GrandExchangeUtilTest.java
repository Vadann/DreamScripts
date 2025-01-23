package test.util;

import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import util.GrandExchangeUtil;

@ScriptManifest(
    name = "GE Util Tester",
    description = "Tests GrandExchangeUtil functions",
    version = 1.0,
    category = Category.MISC,
    author = "Your Name"
)
public class GrandExchangeUtilTest extends AbstractScript {

    private enum TestState {
        BUY_ITEM,   // SEMI - PASS need to add validation
        BUY_SINGLE_ITEM,    // ^^
        BUY_MULTIPLE_ITEMS, // ^^^^
        TRAVEL_TO_GE,   // PASS
        FINISHED
    }

    private TestState currentTest = TestState.TRAVEL_TO_GE;
    
    private static final String TEST_ITEM = "Rune scimitar";
    private static final String[] TEST_ITEMS = {
        "Rune scimitar",
        "Amulet of strength",
        "Rune platebody"
    };

    @Override
    public void onStart() {
        Logger.log("Starting Grand Exchange Util tests...");
    }

    @Override
    public int onLoop() {
        switch (currentTest) {
            case BUY_ITEM:
                Logger.log("Testing buy item");
                boolean boughtItem = GrandExchangeUtil.buyItem(TEST_ITEM, 1);
                Logger.log("Finished buying " + TEST_ITEM);
                currentTest = TestState.FINISHED;
                return 1000;
            case BUY_SINGLE_ITEM:
                Logger.log("Testing buying single item...");
                boolean singleBought = GrandExchangeUtil.buyMissingGear(new String[]{TEST_ITEM});
                Logger.log("Successfully bought single item: " + singleBought);
                currentTest = TestState.FINISHED;
                return 1000;

            case BUY_MULTIPLE_ITEMS:
                Logger.log("Testing buying multiple items...");
                boolean multipleBought = GrandExchangeUtil.buyMissingGear(TEST_ITEMS);
                Logger.log("Successfully bought multiple items: " + multipleBought);
                currentTest = TestState.FINISHED;
                return 1000;

            case TRAVEL_TO_GE:
                Logger.log("Testing Traveling to GE");
                boolean traveled = GrandExchangeUtil.travelToGE();
                Logger.log("Finished traveling to GE" + traveled);
                currentTest = TestState.FINISHED;
                return 1000;

            case FINISHED:
                Logger.log("All Grand Exchange Util tests completed!");
                stop();
                return -1;
        }
        return 1000;
    }

    @Override
    public void onExit() {
        Logger.log("Grand Exchange testing completed!");
    }
} 