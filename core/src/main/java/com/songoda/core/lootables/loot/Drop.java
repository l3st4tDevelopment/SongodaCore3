package com.songoda.core.lootables.loot;

import org.bukkit.inventory.ItemStack;

public class Drop {
    private ItemStack itemStack;

    private String command;

    private int xp;

    public Drop(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public Drop(String command) {
        this.command = command;
    }

    public Drop(int xp) {
        this.xp = xp;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}
