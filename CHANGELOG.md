## 1.9.1
- When you used the translation feature from 1.9.0, the action bar message will be translated too

## 1.9.0
- Added possibility to use your client's translation files to change the lore of AutoShulkers
  - See config.yml for more information. If you need help, contact me on my Discord please at https://discord.jeff-media.com

## 1.8.0
- Improved material names in Shulker lores

## 1.7.0
- You can now turn Autoshulkers back into regular shulker boxes by just placing them on an empty crafting table

## 1.6.1
- Fixed discord-verification.html file being invalid sometimes

## 1.6.0
- You can now change shulker types of an existing autoshulker box

## 1.5.0
- Added Discord verification system (voluntarily, of course)

## 1.4.1
- Removed debug messages on startup
- Fixed UpdateChecker not showing correct download link
- Removed update checker message when using the latest version

## 1.4.0
- Added recipe to remove items from the autoshulker again
  - Just put the book, the autoshulker box and an item that the box already collects into a crafting grid
  - All items that are already contained will no longer be collected
  - You will not get the items back, though

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