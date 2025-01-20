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
        DUMP_ITEMS, // PASS
        VERIFY_DUMP,    // PASS
        FINISHED
    }

    private TestState currentTest = TestState.DUMP_ITEMS;

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

            case FINISHED:
                Logger.log("Bank util test completed!");
                stop();
                return -1;
        }
        return 1000;
    }
} 