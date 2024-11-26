package de.jeff_media.autoshulker.utils;

import com.google.common.base.Enums;
import com.jeff_media.jefflib.EnumUtils;
import de.jeff_media.autoshulker.Main;
import de.jeff_media.autoshulker.config.Config;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class SoundUtils {

    private final Main main;
    private Sound sound;
    private boolean soundEnabled;
    private boolean soundGlobal;
    private float soundVolume;
    private float soundPitch;
    private SoundCategory soundCategory = SoundCategory.BLOCKS;

    public SoundUtils() {
        this.main=Main.getInstance();
        String soundName = main.getConfig().getString(Config.SOUND_EFFECT);
        sound = EnumUtils.getIfPresent(Sound.class,soundName).orElse(null);
        if(sound==null) {
            main.getLogger().warning("Unknown sound effect: "+soundName);
        }
        soundEnabled = main.getConfig().getBoolean(Config.SOUND_ENABLED);
        soundGlobal = main.getConfig().getBoolean(Config.SOUND_GLOBAL);
        soundVolume = (float) main.getConfig().getDouble(Config.SOUND_VOLUME);
        soundPitch = (float) main.getConfig().getDouble(Config.SOUND_PITCH);

    }

    public void playPickupSound(Player player) {
        if(!soundEnabled) {
            return;
        }
        if(sound==null) return;
        if(soundGlobal) {
            player.getWorld().playSound(player.getLocation(),sound,soundCategory,soundVolume,soundPitch);
        } else {
            player.playSound(player.getLocation(),sound,soundCategory,soundVolume,soundPitch);
        }
    }

}
