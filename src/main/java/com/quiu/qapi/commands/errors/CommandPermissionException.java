package com.quiu.qapi.commands.errors;

import org.bukkit.ChatColor;

public class CommandPermissionException extends RuntimeException
{
    public CommandPermissionException(String permission)
    {
        super(ChatColor.RED + "No permission. You need \"" + permission + "\"");
    }
}