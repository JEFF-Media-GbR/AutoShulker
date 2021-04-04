package de.jeff_media.autoshulker.enums;


//import de.jeff_media.autoshulker.config.Config;

import static de.jeff_media.autoshulker.config.Config.*;

public enum ShulkerType {

    AUTOSHULKER(ITEM_NAME, ITEM_LORE), GARBAGEBOX(ITEM_NAME_GARBAGE, ITEM_LORE_GARBAGE);

    private String configName, configLore;

    ShulkerType(String configName, String configLore) {
        this.configLore = configLore;
        this.configName = configName;
    }

    public String getConfigName() {
        return configName;
    }

    public String getConfigLore() {
        return configLore;
    }
}
