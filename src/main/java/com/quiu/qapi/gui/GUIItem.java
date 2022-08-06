package com.quiu.qapi.gui;

import com.quiu.qapi.utils.SUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface GUIItem
{
    int getSlot();
    default ItemStack getItem()
    {
        return new ItemStack(Material.AIR);
    }
    default boolean canPickup()
    {
        return false;
    }

    static GUIItem createLoadingItem(Material type, String name, int slot)
    {
        return new GUIItem()
        {
            @Override
            public int getSlot()
            {
                return slot;
            }

            @Override
            public ItemStack getItem()
            {
                return SUtils.getSingleLoreStack(name, type, (short) 0, 1, ChatColor.DARK_GRAY + "Loading...");
            }
        };
    }
}