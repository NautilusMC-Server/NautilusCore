package nautiluscore.commands;

import nautiluscore.NautilusCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AfkCommand extends CommandStem {

    public AfkCommand(NautilusCore plugin) {
        super(plugin, "afk", "toggles afk status", "");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            if(sender.isOp() && !args[0].equals("")) {
                for(Player p : Bukkit.getOnlinePlayers()) if(p.getName().equals(args[0])) {
                    toggleAFK(p);
                    sender.sendMessage("Toggled AFK status of " + p.getName());
                    return true;
                }
                sender.sendMessage("There is no player with that name online.");
            }
            sender.sendMessage("You must be a player to execute this command.");
            return true;
        }

        toggleAFK((Player)sender);
        return false;
    }

    private void toggleAFK(Player p) {
        if(plugin.getAfkManager().isAfk(p)) plugin.getAfkManager().setNotAfk(p);
        else plugin.getAfkManager().setAfk(p);
    }
}

