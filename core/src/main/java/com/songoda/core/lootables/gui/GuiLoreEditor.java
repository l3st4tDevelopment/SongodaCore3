package com.songoda.core.lootables.gui;

import com.songoda.core.gui.Gui;
import com.songoda.core.lootables.loot.Loot;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public class GuiLoreEditor extends AbstractGuiListEditor {
    public GuiLoreEditor(Loot loot, Player player, Gui returnGui) {
        super(loot, player, Component.text("Lore Editor"), returnGui);
    }

    @Override
    protected List<String> getData() {
        return loot.getRawLore();
    }

    @Override
    protected void updateData(List<String> list) {
        loot.setLore(list);
    }

    @Override
    protected String validate(String line) {
        return line.trim();
    }
}
