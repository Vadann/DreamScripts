package test.util;

import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import util.BankUtil;

@ScriptManifest(
    name = "Bank Util Tester",
    description = "Tests BankUtil functions",
    version = 1.0,
    category = Category.MISC,
    author = "Your Name"
)
public class BankUtilTest extends AbstractScript {

    private enum TestState {
        DUMP_ITEMS,     // PASS
        VERIFY_DUMP,    // PASS
        CHECK_STOCK,    // PASS
        CHECK_COINS,    // PASS
        GET_ITEMS,      // PASS
        FINISHED
    }

    private TestState currentTest = TestState.CHECK_STOCK;

    private static final String TEST_ITEM = "Chocolate dust";
    private static final int TEST_AMOUNT = 5;
    private static final int TEST_COINS = 10000;

    @Override
    public int onLoop() {
        switch (currentTest) {
            case DUMP_ITEMS:
                Logger.log("Testing bank dumping...");
                if (BankUtil.dumpAllToBank()) {
                    Logger.log("Successfully initiated dump");
                    currentTest = TestState.VERIFY_DUMP;
                } else {
                    Logger.log("Failed to dump items");
                    currentTest = TestState.FINISHED;
                }
                return 1000;

            case VERIFY_DUMP:
                Logger.log("Verifying all items were deposited...");
                if (BankUtil.hasDepositedEverything()) {
                    Logger.log("All items successfully deposited!");
                } else {
                    Logger.log("Some items failed to deposit");
                }
                currentTest = TestState.FINISHED;
                return 1000;

            case CHECK_STOCK:
                Logger.log("Testing isOutOfStock for " + TEST_ITEM);
                boolean outOfStock = BankUtil.isOutOfStock(TEST_ITEM);
                Logger.log(TEST_ITEM + " is " + (outOfStock ? "out of stock" : "in stock"));
                currentTest = TestState.CHECK_COINS;
                return 1000;

            case CHECK_COINS:
                Logger.log("Testing hasEnoughCoins for " + TEST_COINS + " coins");
                boolean hasCoins = BankUtil.hasEnoughCoins(TEST_COINS);
                Logger.log((hasCoins ? "Has enough" : "Not enough") + " coins");
                currentTest = TestState.GET_ITEMS;
                return 1000;

            case GET_ITEMS:
                Logger.log("Testing getItemFromBank for " + TEST_AMOUNT + " " + TEST_ITEM);
                boolean gotItems = BankUtil.getItemFromBank(TEST_ITEM, TEST_AMOUNT);
                Logger.log((gotItems ? "Successfully got" : "Failed to get") + " items from bank");
                currentTest = TestState.FINISHED;
                return 1000;

            case FINISHED:
                Logger.log("All bank util extended tests completed!");
                stop();
                return -1;
        }
        return 1000;
    }
} 