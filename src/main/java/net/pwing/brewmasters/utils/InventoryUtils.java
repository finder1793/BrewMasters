package net.pwing.brewmasters.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Modern utility class for creating inventories with Adventure API support
 * Provides methods to work with both legacy and modern text formats
 */
public class InventoryUtils {

    /**
     * Create an inventory with a Component title (modern approach)
     * Uses Paper's native Component support for inventory titles
     * 
     * @param size Inventory size (must be multiple of 9)
     * @param title Component title
     * @return Inventory
     */
    public static Inventory createInventory(int size, Component title) {
        // Paper API supports Component titles directly
        return Bukkit.createInventory(null, size, title);
    }

    /**
     * Create an inventory with MiniMessage title
     * 
     * @param size Inventory size
     * @param miniMessageTitle Title in MiniMessage format
     * @return Inventory
     */
    public static Inventory createInventoryMini(int size, String miniMessageTitle) {
        Component title = TextUtils.miniMessage(miniMessageTitle);
        return createInventory(size, title);
    }

    /**
     * Create an ItemStack with Component display name
     * Uses Paper's modern ItemMeta methods when available
     * 
     * @param material Material type
     * @param displayName Component display name
     * @return ItemStack
     */
    public static ItemStack createItem(Material material, Component displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(displayName);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create an ItemStack with Component display name and lore
     * 
     * @param material Material type
     * @param displayName Component display name
     * @param lore Component lore lines
     * @return ItemStack
     */
    public static ItemStack createItem(Material material, Component displayName, Component... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(displayName);
            if (lore != null && lore.length > 0) {
                meta.lore(Arrays.asList(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create an ItemStack with Component display name and lore list
     * 
     * @param material Material type
     * @param displayName Component display name
     * @param lore List of Component lore lines
     * @return ItemStack
     */
    public static ItemStack createItem(Material material, Component displayName, List<Component> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(displayName);
            if (lore != null && !lore.isEmpty()) {
                meta.lore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create an ItemStack with MiniMessage display name
     * 
     * @param material Material type
     * @param miniMessageName Display name in MiniMessage format
     * @return ItemStack
     */
    public static ItemStack createItemMini(Material material, String miniMessageName) {
        return createItem(material, TextUtils.miniMessage(miniMessageName));
    }

    /**
     * Create an ItemStack with MiniMessage display name and lore
     * 
     * @param material Material type
     * @param miniMessageName Display name in MiniMessage format
     * @param miniMessageLore Lore lines in MiniMessage format
     * @return ItemStack
     */
    public static ItemStack createItemMini(Material material, String miniMessageName, String... miniMessageLore) {
        Component displayName = TextUtils.miniMessage(miniMessageName);
        List<Component> lore = new ArrayList<>();
        if (miniMessageLore != null) {
            for (String line : miniMessageLore) {
                lore.add(TextUtils.miniMessage(line));
            }
        }
        return createItem(material, displayName, lore);
    }

    /**
     * Set display name on an existing ItemStack using Component
     * 
     * @param item ItemStack to modify
     * @param displayName Component display name
     * @return Modified ItemStack
     */
    public static ItemStack setDisplayName(ItemStack item, Component displayName) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(displayName);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Set lore on an existing ItemStack using Components
     * 
     * @param item ItemStack to modify
     * @param lore Component lore lines
     * @return Modified ItemStack
     */
    public static ItemStack setLore(ItemStack item, Component... lore) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.lore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Set lore on an existing ItemStack using Components list
     * 
     * @param item ItemStack to modify
     * @param lore List of Component lore lines
     * @return Modified ItemStack
     */
    public static ItemStack setLore(ItemStack item, List<Component> lore) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Add lore lines to an existing ItemStack
     * 
     * @param item ItemStack to modify
     * @param additionalLore Component lore lines to add
     * @return Modified ItemStack
     */
    public static ItemStack addLore(ItemStack item, Component... additionalLore) {
        if (item == null || additionalLore == null) return item;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<Component> currentLore = meta.lore();
            if (currentLore == null) {
                currentLore = new ArrayList<>();
            }
            currentLore.addAll(Arrays.asList(additionalLore));
            meta.lore(currentLore);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Get display name from ItemStack as Component
     * 
     * @param item ItemStack
     * @return Component display name, or null if none
     */
    public static Component getDisplayName(ItemStack item) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            return meta.displayName();
        }
        return null;
    }

    /**
     * Get lore from ItemStack as Component list
     * 
     * @param item ItemStack
     * @return List of Component lore lines, or null if none
     */
    public static List<Component> getLore(ItemStack item) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            return meta.lore();
        }
        return null;
    }

    /**
     * Create a builder for ItemStack with fluent API
     * 
     * @param material Material type
     * @return ItemBuilder
     */
    public static ItemBuilder builder(Material material) {
        return new ItemBuilder(material);
    }

    /**
     * Fluent builder for creating ItemStacks with Adventure API
     */
    public static class ItemBuilder {
        private final ItemStack item;
        private final ItemMeta meta;

        public ItemBuilder(Material material) {
            this.item = new ItemStack(material);
            this.meta = item.getItemMeta();
        }

        public ItemBuilder amount(int amount) {
            item.setAmount(amount);
            return this;
        }

        public ItemBuilder displayName(Component name) {
            if (meta != null) {
                meta.displayName(name);
            }
            return this;
        }

        public ItemBuilder displayNameMini(String miniMessage) {
            return displayName(TextUtils.miniMessage(miniMessage));
        }

        public ItemBuilder lore(Component... lore) {
            if (meta != null) {
                meta.lore(Arrays.asList(lore));
            }
            return this;
        }

        public ItemBuilder lore(List<Component> lore) {
            if (meta != null) {
                meta.lore(lore);
            }
            return this;
        }

        public ItemBuilder loreMini(String... miniMessageLore) {
            List<Component> lore = new ArrayList<>();
            for (String line : miniMessageLore) {
                lore.add(TextUtils.miniMessage(line));
            }
            return lore(lore);
        }

        public ItemBuilder addLore(Component... additionalLore) {
            if (meta != null) {
                List<Component> currentLore = meta.lore();
                if (currentLore == null) {
                    currentLore = new ArrayList<>();
                }
                currentLore.addAll(Arrays.asList(additionalLore));
                meta.lore(currentLore);
            }
            return this;
        }

        public ItemBuilder customModelData(int data) {
            if (meta != null && data > 0) {
                meta.setCustomModelData(data);
            }
            return this;
        }

        public ItemStack build() {
            if (meta != null) {
                item.setItemMeta(meta);
            }
            return item;
        }
    }
}
