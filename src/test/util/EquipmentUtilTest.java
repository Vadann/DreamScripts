package test.util;

import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import util.EquipmentUtil;

@ScriptManifest(
    name = "Equipment Util Tester",
    description = "Tests EquipmentUtil functions",
    version = 1.0,
    category = Category.MISC,
    author = "Your Name"
)
public class EquipmentUtilTest extends AbstractScript {

    private enum TestState {
        CHECK_REQUIRED_GEAR,   // PASS
        EQUIP_FROM_INVENTORY,   // PASS
        GET_AND_EQUIP_FROM_BANK,    // PASS
        ENSURE_GEAR_EQUIPPED,
        CHECK_WEALTH_RING,
        FINISHED
    }

    private TestState currentTest = TestState.GET_AND_EQUIP_FROM_BANK;
    
    private static final String[] TEST_GEAR = {
        "Rune scimitar",
        "Amulet of strength",
        "Rune platebody"
    };

    @Override
    public void onStart() {
        Logger.log("Starting Equipment Util tests...");
    }

    @Override
    public int onLoop() {
        switch (currentTest) {
            case CHECK_REQUIRED_GEAR:
                Logger.log("Testing hasRequiredGear...");
                boolean hasGear = EquipmentUtil.hasRequiredGearEquipped(TEST_GEAR);
                Logger.log("Has required gear: " + hasGear);
                currentTest = TestState.FINISHED;
                return 1000;

            case EQUIP_FROM_INVENTORY:
                Logger.log("Testing equipFromInventory...");
                boolean equipped = EquipmentUtil.equipFromInventory(TEST_GEAR);
                Logger.log("Successfully equipped from inventory: " + equipped);
                currentTest = TestState.FINISHED;
                return 1000;

            case GET_AND_EQUIP_FROM_BANK:
                Logger.log("Testing getAndEquipFromBank...");
                boolean bankEquipped = EquipmentUtil.getAndEquipFromBank(TEST_GEAR);
                Logger.log("Successfully got and equipped from bank: " + bankEquipped);
                currentTest = TestState.FINISHED;
                return 1000;

            case ENSURE_GEAR_EQUIPPED:
                Logger.log("Testing ensureGearEquipped...");
                boolean ensured = EquipmentUtil.ensureGearEquipped(TEST_GEAR);
                Logger.log("Successfully ensured gear equipped: " + ensured);
                currentTest = TestState.FINISHED;
                return 1000;

            case CHECK_WEALTH_RING:
                Logger.log("Testing wealth ring functions...");
                Logger.log("Has wealth ring: " + EquipmentUtil.hasWealthRing());
                Logger.log("Wealth ring charges: " + EquipmentUtil.getWealthRingCharges());
                currentTest = TestState.FINISHED;
                return 1000;

            case FINISHED:
                Logger.log("All Equipment Util tests completed!");
                stop();
                return -1;
        }
        return 1000;
    }
} 