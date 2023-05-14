package nautiluscore.commands;

import nautiluscore.NautilusCore;
import nautiluscore.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;

public class WhoIsCommand extends CommandStem {
    public WhoIsCommand(NautilusCore plugin) {
        super(plugin, "whois", "See player info", "", "");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Text.c("&7Usage: /whois [player name]"));
            return true;
        }
        sendReport(sender, Bukkit.getOfflinePlayer(args[0]), sender.hasPermission("group.staff"));
        return true;
    }

    private void sendReport(CommandSender s, OfflinePlayer p, boolean staff) {
        s.sendMessage(Text.c("&3-- Player report: &b" + p.getName() + " &3--"));
        boolean wo = plugin.d().hasData(p.getUniqueId());
        s.sendMessage(Text.c("&fOnline&7: " + (p.isOnline() ? "&2yes" : "&4no &7(last seen: " + Instant.ofEpochMilli(p.getLastSeen()).toString() + ")")));
        if(!staff) return;
        if(!wo) s.sendMessage(Text.c("&fIP Address&7: &e" + plugin.d().getIp(p.getUniqueId())));
        s.sendMessage(Text.c("&fLocation&7: &e" + (p.isOnline() ?
                "(" + ((Player)p).getLocation().getX() + ", " + ((Player)p).getLocation().getX() + ", " + ((Player)p).getLocation().getZ() + ") in " + ((Player)p).getLocation().getWorld().getName()
                : (wo ? plugin.d().getLastLocation(p.getUniqueId()) : "not here"))));
    }
}
