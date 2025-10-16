package net.pwing.brewmasters.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * Utility class for working with Adventure API text components
 * Supports legacy color codes, MiniMessage formatting, and RGB/hex colors
 */
public class TextUtils {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = 
            LegacyComponentSerializer.legacyAmpersand();
    
    private static final LegacyComponentSerializer SECTION_SERIALIZER = 
            LegacyComponentSerializer.legacySection();
    
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    
    private static final PlainTextComponentSerializer PLAIN_SERIALIZER = 
            PlainTextComponentSerializer.plainText();

    /**
     * Parse legacy color codes (&) to Adventure Component
     * @param text Text with & color codes
     * @return Component
     */
    public static Component parse(String text) {
        if (text == null) {
            return Component.empty();
        }
        return LEGACY_SERIALIZER.deserialize(text);
    }
    
    /**
     * Parse MiniMessage format to Adventure Component
     * Supports modern formatting including RGB/hex colors
     * Examples:
     * - "<red>Red text</red>"
     * - "<#FF5555>Custom hex color</#FF5555>"
     * - "<gradient:red:blue>Gradient text</gradient>"
     * - "<rainbow>Rainbow text</rainbow>"
     * - "<bold><green>Bold green</green></bold>"
     * 
     * @param miniMessageText Text in MiniMessage format
     * @return Component
     */
    public static Component miniMessage(String miniMessageText) {
        if (miniMessageText == null) {
            return Component.empty();
        }
        return MINI_MESSAGE.deserialize(miniMessageText);
    }
    
    /**
     * Parse text with automatic format detection
     * Tries MiniMessage first (if contains < >), falls back to legacy
     * 
     * @param text Text to parse
     * @return Component
     */
    public static Component parseAuto(String text) {
        if (text == null) {
            return Component.empty();
        }
        // If text contains MiniMessage tags, use MiniMessage
        if (text.contains("<") && text.contains(">")) {
            try {
                return MINI_MESSAGE.deserialize(text);
            } catch (Exception e) {
                // Fall back to legacy if MiniMessage parsing fails
                return LEGACY_SERIALIZER.deserialize(text);
            }
        }
        // Otherwise use legacy format
        return LEGACY_SERIALIZER.deserialize(text);
    }

    /**
     * Parse legacy color codes (§) to Adventure Component
     */
    public static Component parseSection(String text) {
        return SECTION_SERIALIZER.deserialize(text);
    }

    /**
     * Strip all color codes and formatting from text
     * Works with both legacy and MiniMessage formats
     */
    public static String stripColor(String text) {
        if (text == null) {
            return null;
        }
        // Try to parse as component and convert to plain text
        try {
            Component component = parseAuto(text);
            return PLAIN_SERIALIZER.serialize(component);
        } catch (Exception e) {
            // Fallback to regex stripping
            return text.replaceAll("§[0-9a-fk-or]", "")
                      .replaceAll("&[0-9a-fk-or]", "")
                      .replaceAll("<[^>]+>", "");
        }
    }

    /**
     * Create a colored text component with named color
     */
    public static Component colored(String text, NamedTextColor color) {
        return Component.text(text, color);
    }
    
    /**
     * Create a colored text component with hex color
     * @param text The text
     * @param hexColor Hex color (e.g., "#FF5555" or "FF5555")
     * @return Component
     */
    public static Component hex(String text, String hexColor) {
        if (hexColor == null) {
            return Component.text(text);
        }
        // Remove # if present
        String cleanHex = hexColor.startsWith("#") ? hexColor.substring(1) : hexColor;
        TextColor color = TextColor.fromHexString("#" + cleanHex);
        return Component.text(text, color);
    }
    
    /**
     * Create a gradient text component
     * @param text The text
     * @param startColor Starting hex color
     * @param endColor Ending hex color
     * @return Component
     */
    public static Component gradient(String text, String startColor, String endColor) {
        return miniMessage("<gradient:" + startColor + ":" + endColor + ">" + text + "</gradient>");
    }
    
    /**
     * Create a rainbow text component
     * @param text The text
     * @return Component
     */
    public static Component rainbow(String text) {
        return miniMessage("<rainbow>" + text + "</rainbow>");
    }

    /**
     * Create a text component with color and decoration
     */
    public static Component colored(String text, NamedTextColor color, TextDecoration... decorations) {
        Component component = Component.text(text, color);
        for (TextDecoration decoration : decorations) {
            component = component.decorate(decoration);
        }
        return component;
    }

    /**
     * Create an empty component
     */
    public static Component empty() {
        return Component.empty();
    }

    /**
     * Create a newline component
     */
    public static Component newline() {
        return Component.newline();
    }

    /**
     * Join multiple components
     */
    public static Component join(Component... components) {
        if (components.length == 0) {
            return Component.empty();
        }
        Component result = components[0];
        for (int i = 1; i < components.length; i++) {
            result = result.append(components[i]);
        }
        return result;
    }

    /**
     * Convert legacy string to plain text (no formatting)
     * @deprecated Use {@link #stripColor(String)} instead
     */
    @Deprecated
    public static String toPlainText(String legacyText) {
        return stripColor(legacyText);
    }
    
    /**
     * Convert Component to legacy string (for backwards compatibility)
     * Useful for inventory titles and item metadata
     */
    public static String toLegacy(Component component) {
        if (component == null) {
            return "";
        }
        return SECTION_SERIALIZER.serialize(component);
    }
    
    /**
     * Convert Component to plain text string
     */
    public static String toPlain(Component component) {
        if (component == null) {
            return "";
        }
        return PLAIN_SERIALIZER.serialize(component);
    }
}
