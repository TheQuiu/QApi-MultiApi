package com.quiu.qapi.commands;


import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BaseCommandLoader
{

    private final List<BaseCommand> commands;

    public BaseCommandLoader()
    {
        this.commands = new ArrayList<>();
    }

    public void register(BaseCommand command, CommandMap commandMap)
    {
        commands.add(command);
        command.register(commandMap);
    }

    public int getCommandAmount()
    {
        return commands.size();
    }

    public void registerAllCommandsFromPackage(String packagePath, CommandMap commandMap){
        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(Bukkit.getServer());
            Bukkit.getConsoleSender().sendMessage("Loaded!");
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        BaseCommandLoader loader = new BaseCommandLoader();
        Reflections reflection = new Reflections(packagePath);
        for (Class<? extends BaseCommand> l : reflection.getSubTypesOf(BaseCommand.class)) {
            try {
                BaseCommand command = l.newInstance();
                loader.register(command, commandMap);
                Bukkit.getConsoleSender().sendMessage("Loaded: " + command.getClass().getName());

            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        Bukkit.getConsoleSender().sendMessage("Commands amount: " + loader.getCommandAmount());
    }
}