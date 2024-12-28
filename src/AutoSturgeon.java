import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.methods.container.impl.bank.Bank;


@ScriptManifest(name = "Script Name", description = "My script description!", author = "Developer Name",
        version = 1.0, category = Category.WOODCUTTING, image = "")
public class AutoSturgeon extends AbstractScript {

    @Override
    public int onLoop() {
        Item depositItem = Inventory.get("Caviar");
        Item withdrawItem = Bank.get("Leaping Sturgeon");

        for(int i = 1; i < Inventory.capacity(); i++) {
            if (Inventory.getItemInSlot(i) != null) {
                Item item = Inventory.getItemInSlot(i);
                useOnOtherItem(Inventory.get("Knife"), item);

                Logger.log("Successfully used Knife on " + item);

            }
        }

        bankHandler(depositItem, withdrawItem);


        return 5000; // Pause for 1 second
    }

    public void useOnOtherItem(Item one, Item two) {
        if (Inventory.contains(one) && Inventory.contains(two)) {
            one.useOn(two);
        }

        else {
            Logger.log("One of the items was not found in the inventory");
        }
    }

    public void bankHandler(Item deposit, Item withdraw) {
        // Ensure the bank is open
        if (!Bank.isOpen()) {
            Bank.open();
        }

        // Deposit the item if it's not null
        if (deposit != null) {
            Bank.depositAll(deposit);
        }

        // Check if the withdraw item is not null and exists in the bank
        if (withdraw != null && Bank.contains(withdraw.getName())) {
            Bank.withdrawAll(withdraw.getName());
        } else if (withdraw == null) {
            Logger.log("Cannot withdraw: Item is null.");
        } else if (!Bank.contains(withdraw.getName())) {
            Logger.log("The item to withdraw is not available in the bank.");
        }
    }


}
