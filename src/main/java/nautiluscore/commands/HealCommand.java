package nautiluscore.commands;

import nautiluscore.NautilusCore;
import nautiluscore.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand extends CommandStem {
    public HealCommand(NautilusCore plugin) {
        super(plugin, "heal", "Heal players", "", "");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!sender.isOp()) {
            sender.sendMessage(Text.c("&7You must be a server operator to run this command."));
            return true;
        }
        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(Text.c("&7Please specify a player to heal."));
                return true;
            }
            heal((Player) sender);
            return true;
        }
        if(args[0].equals("*")) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                heal(p);
            }
            sender.sendMessage(Text.c("&aHealed all players!"));
            return true;
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.getName().equalsIgnoreCase(args[0])) {
                heal(p);
                sender.sendMessage(Text.c("&aHealed " + args[0] + "!"));
                return true;
            }
            sender.sendMessage(Text.c("&7Could not find player " + args[0] + " online."));
        }

        return true;
    }

    private void heal(Player p) {
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setSaturation(20);
        p.sendMessage(Text.c("&aYou have been healed!"));
    }
}
