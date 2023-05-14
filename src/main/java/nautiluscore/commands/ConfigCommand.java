package nautiluscore.commands;

import nautiluscore.NautilusCore;
import nautiluscore.util.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class ConfigCommand extends CommandStem {
    public ConfigCommand(NautilusCore plugin) {
        super(plugin, "nconfig", "Configures server configurable features", "");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission("group.staff")) {
            sender.sendMessage(Text.c("&7You don't have permission to use this command."));
            return true;
        }
        if(args.length == 0 || args.length > 2) {
            sendStatus(sender);
            return true;
        }

        if(args.length == 1) {
            sendStatus(sender, args[0]);
            sender.sendMessage(Text.c("&7To change value: /nconfig " + args[0] + " [new value]"));
            return true;
        }

        if(!plugin.d().getAllConfig().keySet().contains(args[0])) {
            sendStatus(sender);
            return true;
        }
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("config");
        Object prev = cfg.get(args[0]);
        try {
            if (prev instanceof Boolean) {
                cfg.set(args[0], Boolean.parseBoolean(args[1]));
            } else if(prev instanceof Double) {
                cfg.set(args[0], Double.parseDouble(args[1]));
            } else {
                cfg.set(args[0], Integer.parseInt(args[1]));
            }
        } catch (Exception e) {
            sender.sendMessage(Text.c("&7Type could not be parsed."));
            return true;
        }
        sender.sendMessage(Text.c("&7Config value updated."));
        plugin.getDataManager().saveConfig();
        return true;

    }

    private void sendStatus(CommandSender s, String...specific) {
        s.sendMessage(Text.c("&3&l-- NautilusCore Config Options --"));
        if(specific.length == 0) s.sendMessage(Text.c("&7Usage: /nconfig [option] [value]"));
        HashMap<String, Object> h = plugin.d().getAllConfig();
        for(String str : h.keySet()) {
            if(specific.length != 0 && !str.equalsIgnoreCase(specific[0])) continue;
            Object b = h.get(str);
            String right = "";
            if(b instanceof Boolean) {
                boolean r = (boolean) b;
                right += Text.c((r ? "&2true" : "&4false"));
            }
            else {
                right += Text.c("&6" + b);
            }
            s.sendMessage(Text.c("&f" + str + "&7: ") + right);
        }
    }
}
