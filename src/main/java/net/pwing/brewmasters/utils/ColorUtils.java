package net.pwing.brewmasters.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Legacy color utility class
 * 
 * @deprecated Use {@link TextUtils} for Adventure API components instead.
 * 
 * TextUtils now supports:
 * - MiniMessage format with RGB/hex colors
 * - Gradients and rainbow text
 * - Modern Component-based text
 * - Automatic format detection
 * 
 * Examples:
 * - TextUtils.miniMessage("<gradient:red:blue>Text</gradient>")
 * - TextUtils.hex("Text", "#FF5555")
 * - TextUtils.rainbow("Rainbow text")
 * 
 * See ADVENTURE_API_EXAMPLES.md for more information.
 */
@Deprecated
@SuppressWarnings("deprecation")
public class ColorUtils {
    
    /**
     * Translates color codes in a string
     * @param text The text to translate
     * @return The translated text
     * @deprecated Use {@link TextUtils#parse(String)} instead
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public static String translate(String text) {
        if (text == null) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    /**
     * Translates color codes in a list of strings
     * @param lines The lines to translate
     * @return The translated lines
     * @deprecated Use {@link TextUtils} methods instead
     */
    @Deprecated
    public static List<String> translate(List<String> lines) {
        if (lines == null) {
            return null;
        }
        
        List<String> translated = new ArrayList<>();
        for (String line : lines) {
            translated.add(translate(line));
        }
        return translated;
    }
}

