
# QApi MultiApi

A api system for creating a bukkit plugins and contribute it.



# Examples

*MainCLass*
```java
public final class MyAwesomePlugin extends AbstractPlugin {

    @Override 
    public void onEnable() {
            System.out.print("Your awesome plugin loaded and enabled")

            //registraction commands
            BaseCommandLoader.registerAllCommandsFromPackage("me.quiu.awesome.commands", commandMap);


    }


}
```

*YourAwesomeCommand*
```java

@CommandParametrs(name = "/test", description = "Omg your firs command", premisson = "qapi.awesomecommand", usage = "/test")
public class YourAwesomeCommand extends BaseCommand{

    @Override
    public void run(CommandSource commandSender, String[] commandArgs) {
        commandSender.sendMessage("Your awesome test command! Enjoy your code :D")
    }

}


I will updata my API and add new systems:
    - npc
    - listeners
    - databases(develop rn)
    - leveling
    - custom items
    - and more...



#Thanks for using this API enjoy coding :D
