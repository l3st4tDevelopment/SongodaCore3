/**
 * MIT License
 * <p>
 * Copyright (c) 2021 TriumphTeam
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.songoda.core.gui.builder.item;

import com.songoda.core.gui.GuiItem;
import com.songoda.core.gui.components.GuiAction;
import com.songoda.core.gui.components.exception.GuiException;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Contains all the common methods for the future ItemBuilders
 *
 * @param <B> The ItemBuilder type so the methods can cast to the subtype
 */
@SuppressWarnings("unchecked")
public abstract class BaseItemBuilder<B extends BaseItemBuilder<B>> {

    private static final EnumSet<Material> LEATHER_ARMOR = EnumSet.of(
            Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS
    );

    private static final GsonComponentSerializer GSON = GsonComponentSerializer.gson();
    private static final Field DISPLAY_NAME_FIELD;
    private static final Field LORE_FIELD;

    static {
        String version = Bukkit.getServer().getClass().getPackage().getName();
        version = version.substring(version.lastIndexOf('.') + 1);

        try {
            final Class<?> metaClass = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftMetaItem");

            DISPLAY_NAME_FIELD = metaClass.getDeclaredField("displayName");
            DISPLAY_NAME_FIELD.setAccessible(true);

            LORE_FIELD = metaClass.getDeclaredField("lore");
            LORE_FIELD.setAccessible(true);
        } catch (NoSuchFieldException | ClassNotFoundException exception) {
            exception.printStackTrace();
            throw new GuiException("Could not retrieve displayName nor lore field for ItemBuilder.");
        }
    }

    private ItemStack itemStack;
    private ItemMeta meta;

    protected BaseItemBuilder(@NotNull final ItemStack itemStack) {
        Validate.notNull(itemStack, "Item can't be null!");

        this.itemStack = itemStack;
        meta = itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType());
    }

    /**
     * Sets the display name of the item using {@link Component}
     *
     * @param name The {@link Component} name
     * @return {@link ItemBuilder}
     * @since 3.0.0
     */
    @NotNull
    @Contract("_ -> this")
    public B name(@NotNull final Component name) {
        if (meta == null) return (B) this;

        try {
            DISPLAY_NAME_FIELD.set(meta, GSON.serialize(name.applyFallbackStyle(TextDecoration.ITALIC.withState(false))));
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }

        return (B) this;
    }

    /**
     * Sets the amount of items
     *
     * @param amount the amount of items
     * @return {@link ItemBuilder}
     * @since 3.0.0
     */
    @NotNull
    @Contract("_ -> this")
    public B amount(final int amount) {
        itemStack.setAmount(amount);
        return (B) this;
    }

    /**
     * Set the lore lines of an item
     *
     * @param lore Lore lines as varargs
     * @return {@link ItemBuilder}
     * @since 3.0.0
     */
    @NotNull
    @Contract("_ -> this")
    public B lore(@Nullable final Component @NotNull ... lore) {
        return lore(Arrays.asList(lore));
    }

    /**
     * Set the lore lines of an item
     *
     * @param lore A {@link List} with the lore lines
     * @return {@link ItemBuilder}
     * @since 3.0.0
     */
    @NotNull
    @Contract("_ -> this")
    public B lore(@NotNull final List<@Nullable Component> lore) {
        if (meta == null) return (B) this;

        final List<String> jsonLore = lore.stream()
                .filter(Objects::nonNull)
                .map(component -> GSON.serialize(component.applyFallbackStyle(TextDecoration.ITALIC.withState(false))))
                .collect(Collectors.toList());

        try {
            LORE_FIELD.set(meta, jsonLore);
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }

        return (B) this;
    }

    /**
     * Enchants the {@link ItemStack}
     *
     * @param enchantment            The {@link Enchantment} to add
     * @param level                  The level of the {@link Enchantment}
     * @param ignoreLevelRestriction If should or not ignore it
     * @return {@link ItemBuilder}
     * @since 3.0.0
     */
    @NotNull
    @Contract("_, _, _ -> this")
    public B enchant(@NotNull final Enchantment enchantment, final int level, final boolean ignoreLevelRestriction) {
        meta.addEnchant(enchantment, level, ignoreLevelRestriction);
        return (B) this;
    }

    /**
     * Enchants the {@link ItemStack}
     *
     * @param enchantment The {@link Enchantment} to add
     * @param level       The level of the {@link Enchantment}
     * @return {@link ItemBuilder}
     * @since 3.0.0
     */
    @NotNull
    @Contract("_, _ -> this")
    public B enchant(@NotNull final Enchantment enchantment, final int level) {
        return enchant(enchantment, level, true);
    }

    /**
     * Enchants the {@link ItemStack}
     *
     * @param enchantment The {@link Enchantment} to add
     * @return {@link ItemBuilder}
     * @since 3.0.0
     */
    @NotNull
    @Contract("_ -> this")
    public B enchant(@NotNull final Enchantment enchantment) {
        return enchant(enchantment, 1, true);
    }

    /**
     * Enchants the {@link ItemStack} with the specified map where the value
     * is the level of the key's enchantment
     *
     * @param enchantments Enchantments to add
     * @param ignoreLevelRestriction If level restriction should be ignored
     * @return {@link ItemBuilder}
     * @since 3.1.2
     */
    @NotNull
    @Contract("_, _ -> this")
    public B enchant(@NotNull final Map<Enchantment, Integer> enchantments, final boolean ignoreLevelRestriction) {
        enchantments.forEach((enchantment, level) -> this.enchant(enchantment, level, ignoreLevelRestriction));
        return (B) this;
    }

    /**
     * Enchants the {@link ItemStack} with the specified map where the value
     * is the level of the key's enchantment
     *
     * @param enchantments Enchantments to add
     * @return {@link ItemBuilder}
     * @since 3.1.2
     */
    @NotNull
    @Contract("_ -> this")
    public B enchant(@NotNull final Map<Enchantment, Integer> enchantments) {
        return enchant(enchantments, true);
    }

    /**
     * Disenchants a certain {@link Enchantment} from the {@link ItemStack}
     *
     * @param enchantment The {@link Enchantment} to remove
     * @return {@link ItemBuilder}
     * @since 3.0.0
     */
    @NotNull
    @Contract("_ -> this")
    public B disenchant(@NotNull final Enchantment enchantment) {
        itemStack.removeEnchantment(enchantment);
        return (B) this;
    }

    /**
     * Add an {@link ItemFlag} to the item
     *
     * @param flags The {@link ItemFlag} to add
     * @return {@link ItemBuilder}
     * @since 3.0.0
     */
    @NotNull
    @Contract("_ -> this")
    public B flags(@NotNull final ItemFlag... flags) {
        meta.addItemFlags(flags);
        return (B) this;
    }

    /**
     * Makes the {@link ItemStack} glow
     *
     * @return {@link ItemBuilder}
     * @since 3.0.0
     */
    @NotNull
    @Contract(" -> this")
    public B glow() {
        return glow(true);
    }

    /**
     * Adds or removes the {@link ItemStack} glow
     *
     * @param glow Should the item glow
     * @return {@link ItemBuilder}
     */
    @NotNull
    @Contract("_ -> this")
    public B glow(boolean glow) {
        if (glow) {
            meta.addEnchant(Enchantment.LURE, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            return (B) this;
        }

        for (final Enchantment enchantment : meta.getEnchants().keySet()) {
            meta.removeEnchant(enchantment);
        }

        return (B) this;
    }

    /**
     * Color an {@link ItemStack}
     *
     * @param color color
     * @return {@link B}
     * @see LeatherArmorMeta#setColor(Color)
     * @since 3.0.3
     */
    @NotNull
    @Contract("_ -> this")
    public B color(@NotNull final Color color) {
        if (LEATHER_ARMOR.contains(itemStack.getType())) {
            final LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) getMeta();

            leatherArmorMeta.setColor(color);
            setMeta(leatherArmorMeta);
        }

        return (B) this;
    }

    /**
     * Sets NBT tag to the {@link ItemStack}
     *
     * @param key   The NBT key
     * @param value The NBT value
     * @return {@link ItemBuilder}
     */
    @NotNull
    @Contract("_, _ -> this")
    public B setNbt(@NotNull final String key, @NotNull final String value) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString(key, value);

        itemStack = nbtItem.getItem();
        meta = itemStack.getItemMeta();
        return (B) this;
    }

    /**
     * Sets NBT tag to the {@link ItemStack}
     *
     * @param key   The NBT key
     * @param value The NBT value
     * @return {@link ItemBuilder}
     */
    @NotNull
    @Contract("_, _ -> this")
    public B setNbt(@NotNull final String key, final boolean value) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setBoolean(key, value);

        itemStack = nbtItem.getItem();
        meta = itemStack.getItemMeta();
        return (B) this;
    }

    /**
     * Sets NBT tag to the {@link ItemStack}
     *
     * @param key   The NBT key
     * @param value The NBT value
     * @return {@link ItemBuilder}
     */
    @NotNull
    @Contract("_, _ -> this")
    public B setNbt(@NotNull final String key, final int value) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setInteger(key, value);

        itemStack = nbtItem.getItem();
        meta = itemStack.getItemMeta();
        return (B) this;
    }

    /**
     * Removes NBT tag from the {@link ItemStack}
     *
     * @param key The NBT key
     * @return {@link ItemBuilder}
     */
    @NotNull
    @Contract("_ -> this")
    public B removeNbt(@NotNull final String key) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.removeKey(key);

        itemStack = nbtItem.getItem();
        meta = itemStack.getItemMeta();
        return (B) this;
    }

    /**
     * Builds the item into {@link ItemStack}
     *
     * @return The fully built {@link ItemStack}
     */
    @NotNull
    public ItemStack build() {
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Creates a {@link GuiItem} instead of an {@link ItemStack}
     *
     * @return A {@link GuiItem} with no {@link GuiAction}
     */
    @NotNull
    @Contract(" -> new")
    public GuiItem asGuiItem() {
        return new GuiItem(build());
    }

    /**
     * Creates a {@link GuiItem} instead of an {@link ItemStack}
     *
     * @param action The {@link GuiAction} to apply to the item
     * @return A {@link GuiItem} with {@link GuiAction}
     */
    @NotNull
    @Contract("_ -> new")
    public GuiItem asGuiItem(@NotNull final GuiAction<InventoryClickEvent> action) {
        return new GuiItem(build(), action);
    }

    /**
     * Package private getter for extended builders
     *
     * @return The ItemStack
     */
    @NotNull
    protected ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Package private setter for the extended builders
     *
     * @param itemStack The ItemStack
     */
    protected void setItemStack(@NotNull final ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /**
     * Package private getter for extended builders
     *
     * @return The ItemMeta
     */
    @NotNull
    protected ItemMeta getMeta() {
        return meta;
    }

    /**
     * Package private setter for the extended builders
     *
     * @param meta The ItemMeta
     */
    protected void setMeta(@NotNull final ItemMeta meta) {
        this.meta = meta;
    }

    public Component getName() {
        try {
            return GSON.deserialize((String)DISPLAY_NAME_FIELD.get(meta));
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public List<Component> getLore() {
        List<Component> components;
        try {
            final List<String> jsonLore = (List<String>) LORE_FIELD.get(meta);
            // The field is null by default ._.
            components = (jsonLore == null) ? new ArrayList<>() : jsonLore.stream().map(GSON::deserialize).collect(Collectors.toList());
        } catch (IllegalAccessException exception) {
            components = new ArrayList<>();
            exception.printStackTrace();
        }

        return components;
    }
}