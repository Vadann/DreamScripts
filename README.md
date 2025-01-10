# TODO:

# AutoCrafting:
- (DONE) Fix issue relating to remaining items in inventory if out of stock. Need to clear out remaining withdrawable items (Leaping sturgeon chocolate)
- (DONE) Modularize the onLoop method
- (DONE / BUG TEST) Figure out a backend system that can dynamically change the processingItems (itemToWithdraw / sell) to change ingredients when out of stock and  GE limit
- (DONE) Fix bug where slot 26 is a knife due to an accidental drag.
- (WIP) Try to figure out how to handle restocking if enabled by user.
- (WIP) Make sure correct tab is open before starting crafting.

# AutoSkills:
- (NOT STARTED) Cooking
- (NOT STARTED) Fishing
- (NOT STARTED) Mining
- (NOT STARTED) Herblore

# GUI/AutoCrafter:

- (DONE)Fix bug where it only crafts the selected items. Currently it only does leaping sturgeon by default when you press start.
    -  Created a list to store all values when selected and a map to get the value associated with it, but need to implement a system that manages handling all selected craftables


- MORE TO COME :)

# Core:
- Pathfinding
- GrandExchange Handler
- Combat Handler
  ...
