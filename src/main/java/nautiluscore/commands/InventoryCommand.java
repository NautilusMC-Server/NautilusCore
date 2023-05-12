package nautiluscore.commands;

import nautiluscore.NautilusCore;
import nautiluscore.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InventoryCommand extends CommandStem {
    public InventoryCommand(NautilusCore plugin) {
        super(plugin, "inventory", "Allow opening of inventories", "");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to run this command.");
            return true;
        }
        Player p = (Player) sender;
        if(!sender.hasPermission("group.staff")) {
            p.openInventory(p.getInventory());
            return true;
        }
        if(args.length == 0) {
            sender.sendMessage(Text.c("&cUsage: /inventory [online player]"));
            return true;
        }

        for(Player target : Bukkit.getOnlinePlayers()) {
            if(target.getName().equalsIgnoreCase(args[0])) {
                p.openInventory(target.getInventory());
                p.sendMessage("Opened inventory of " + target.getName());
                return true;
            }
        }
        p.sendMessage("Player " + args[0] + " is not on the server.");

        return false;
    }
}
