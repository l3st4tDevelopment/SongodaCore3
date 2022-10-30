package com.songoda.core.lootables.gui;

import com.songoda.core.gui.Gui;
import com.songoda.core.lootables.loot.Loot;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class GuiEntityEditor extends AbstractGuiListEditor {
    public GuiEntityEditor(Loot loot, Player player, Gui returnGui) {
        super(loot, player, Component.text("Entity Editor"), returnGui);
    }

    @Override
    protected List<String> getData() {
        return loot.getOnlyDropFor().stream().map(Enum::name).collect(Collectors.toList());
    }

    @Override
    protected void updateData(List<String> list) {
        loot.setOnlyDropFor(list.stream().map(EntityType::valueOf).collect(Collectors.toList()));
    }

    @Override
    protected String validate(String line) {
        line = line.toUpperCase().trim();

        try {
            EntityType.valueOf(line);
            return line;
        } catch (IllegalArgumentException ignore) {
        }

        return null;
    }
}
