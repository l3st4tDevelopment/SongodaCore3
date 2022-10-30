package com.songoda.core.gui.components.util;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class LegacySerializer {

    /**
     * Since old versions and Spigot not using the components, we use the serializer
     * with the stupid format to guarantee compatibility
     */
    public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private LegacySerializer() {
        throw new UnsupportedOperationException("Class should not be instantiated!");
    }

}
