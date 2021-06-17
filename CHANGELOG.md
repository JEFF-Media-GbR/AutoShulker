## 1.3.4
- Fixed exception in 1.17

## 1.3.3
- Fixed potential StackOverflowException when using Airplane
- Moved to new UpdateChecker

## 1.3.2
- Fixed Bukkit not being able to properly deserialize JSON contents in ItemStack's PersistentDataContainer. Data will now be stored as plaintext instead of JSON

## 1.3.1
- Fixed compatibility with AutoShulker boxes and GarbageBoxes created in older versions

## 1.3.0
- Sound effects will now be played as "Block" sound
- Added permissions:
  - autoshulker.craft.autoshulker: Allows to craft AutoShulker boxes (default: true)
  - autoshulker.craft.garbagebox: Allows to craft GarbageBoxes (default: true)
  - autoshulker.use.autoshulker: Allows to use AutoShulker boxes (default: true)
  - autoshulker.use.garbagebox: Allows to use GarbageBoxes (default: true)

## 1.2.0
- Garbage Boxes can now destroy all items that you put into them manually as well (configurable, default: false)

## 1.1.2
- Fixed duplication bug using crafting recipe

## 1.1.0
- Added garbage boxes (use a lava bucket instead of a book)
  - Garbage boxes will destroy items instead of collecting them
- Fixed missing lore when picking up AutoShulkers again
- Prevents the book from being taken out of the Shulker
- Fixed update checker