package nautiluscore.commands;

import nautiluscore.NautilusCore;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public abstract class CommandStem {

    protected NautilusCore plugin;
    protected String command, description;
    protected List<String> aliases;

    public CommandStem(NautilusCore plugin, String command, String description, String... aliases){
        this.plugin = plugin;
        this.command = command;
        this.description = description;
        if(aliases!=null) this.aliases = Arrays.asList(aliases);
    }

    public abstract boolean execute(CommandSender sender, String[] args);

    public String getCommand() {
        return command;
    }

    public List<String> getAliases() {
        return aliases;
    }

}
