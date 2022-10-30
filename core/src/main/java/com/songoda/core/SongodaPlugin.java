package com.songoda.core;

import co.aikar.commands.PaperCommandManager;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public abstract class SongodaPlugin extends JavaPlugin {

    static {
        /* NBT-API */
        MinecraftVersion.getLogger().setLevel(Level.WARNING);
        MinecraftVersion.disableUpdateCheck();
    }

    private PaperCommandManager commandManager;
    private BukkitAudiences adventure;

    public abstract void onPluginEnable();
    public abstract void onPluginDisable();

    protected abstract int getPluginId();
    protected abstract String getPluginIcon();

    @Override
    public final void onEnable() {
        SongodaCore.getInstance().registerPlugin(this, getPluginId(), getPluginIcon());

        this.commandManager = new PaperCommandManager(this);
        this.adventure = BukkitAudiences.create(this);

        onPluginEnable();
    }

    @Override
    public final void onDisable() {
        onPluginDisable();
    }

    /**
     * Use {@link dev.dejvokep.boostedyaml.YamlDocument} instead.
     */
    @Deprecated
    @Override
    public @NotNull FileConfiguration getConfig() {
        return super.getConfig();
    }

    /**
     * Use {@link dev.dejvokep.boostedyaml.YamlDocument} instead.
     */
    @Deprecated
    @Override
    public void reloadConfig() {
    }

    /**
     * Use {@link dev.dejvokep.boostedyaml.YamlDocument} instead.
     */
    @Deprecated
    @Override
    public void saveConfig() {
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    public BukkitAudiences getAdventure() {
        return adventure;
    }
}
