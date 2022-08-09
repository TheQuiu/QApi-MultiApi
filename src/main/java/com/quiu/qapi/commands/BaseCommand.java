package com.quiu.qapi.commands;

import com.quiu.qapi.commands.errors.CommandArgumentException;
import com.quiu.qapi.commands.errors.CommandFailException;
import com.quiu.qapi.commands.errors.CommandPermissionException;
import com.quiu.qapi.commands.errors.PlayerNotFoundException;
import com.quiu.qapi.utils.SUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public abstract class BaseCommand implements CommandExecutor, TabCompleter
{
    public static final String COMMAND_SUFFIX = "Command";

    private CommandParameters params;
    private String name;
    private String description;
    private String usage;
    private List<String> aliases;
    private String permission;
    private SECommand command;

    private CommandSource commandSender;

    protected BaseCommand()
    {
        this.params = this.getClass().getAnnotation(CommandParameters.class);
        this.name = this.params.name();
        this.description = this.params.description();
        this.usage = this.params.usage();
        this.aliases = Arrays.asList(this.params.aliases().split(","));
        this.permission = this.params.permission();
        this.command = new SECommand(this);
    }

    /**
     * Executes the given command
     * @param commandSender Source of the command
     * @param commandArgs Passed command arguments
     */
    abstract void run(CommandSource commandSender, String[] commandArgs);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        return null;
    }

    public void register(CommandMap commandMap)
    {
        commandMap.register("", this.command);
    }
    private static class SECommand extends Command
    {
        private final BaseCommand sc;

        public SECommand(BaseCommand xc)
        {
            super(xc.name, xc.description, xc.usage, xc.aliases);
            this.setPermission(xc.permission);
            this.setPermissionMessage(ChatColor.RED + "No permission. You need \"" + xc.permission + "\"");
            this.sc = xc;
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args)
        {
            sc.commandSender = new CommandSource(sender);
            try
            {
                sc.run(sc.commandSender, args);
                return true;
            }
            catch (CommandFailException | CommandPermissionException | PlayerNotFoundException ex)
            {
                sender.sendMessage("Error while executing command!");
                return true;
            }
            catch (CommandArgumentException ex)
            {
                return false;
            }
            catch (Exception ex)
            {
                sender.sendMessage(ChatColor.RED + "Error: " + ex.getMessage());
                ex.printStackTrace();
                return true;
            }
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        {
            List<String> tc = sc.onTabComplete(sender, this, alias, args);
            if (tc != null)
                return tc;
            return SUtils.getPlayerNameList();
        }
    }

    public void send(String message, CommandSource sender)
    {
        sender.send(ChatColor.GRAY + message);
    }

    public void send(String message)
    {
        send(message, commandSender);
    }

    public void send(String message, Player player)
    {
        player.sendMessage(ChatColor.GRAY + message);
    }
    public void checkPermission(String permission)
    {
        if (!commandSender.getSender().hasPermission(permission))
            throw new CommandPermissionException(permission);
    }

    public Player getNonNullPlayer(String name)
    {
        Player player = Bukkit.getPlayer(name);
        if (player == null)
            throw new PlayerNotFoundException();
        return player;
    }
}