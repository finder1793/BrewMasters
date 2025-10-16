package net.pwing.brewmasters.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ColorUtils {
    
    /**
     * Translates color codes in a string
     * @param text The text to translate
     * @return The translated text
     */
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
     */
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

