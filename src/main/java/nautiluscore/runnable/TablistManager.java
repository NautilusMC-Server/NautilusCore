package nautiluscore.runnable;

import nautiluscore.NautilusCore;
import nautiluscore.util.Text;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

public class TablistManager {

    private NautilusCore plugin;

    public TablistManager(NautilusCore plugin) {
        this.plugin = plugin;
    }

    public void onTab() {

        HashMap<UUID, Double> map = new HashMap<>(); // Player UUID, Health

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    if(!plugin.d().getRunTablist()) break;

                    String name = p.getDisplayName() + " ";
                    String realName = "";
                    if(!getTextContent(p.displayName()).equals(p.getName())) {
                        realName = Text.c("&7(" + p.getName() + ") ");
                    }
                    String h = String.valueOf(Math.round(p.getHealth()));
                    double health = p.getHealth();
                    String afk = "";

                    // changes nametag for afk etc.
                    if(plugin.getAfkManager().isAfk(p)) {
                        name = ChatColor.GRAY + name;
                        afk = ChatColor.GRAY + "AFK ";
                    }

                    /*if(isPlayerInGroup(p, "owner")) {
                        name = ChatColor.of(new Color(252, 153, 145)) + name;
                    } else if (isPlayerInGroup(p, "staff")) {
                        name = ChatColor.of(new Color(252, 183, 92)) + name;
                    } else if (isPlayerInGroup(p, "youtube")) {
                        name = ChatColor.RED + "{TW} " + ChatColor.of(new Color(130, 252, 130)) + name;
                    } else if (isPlayerInGroup(p, "sponsor")) {
                        name = ChatColor.of(new Color(144, 231, 252)) + name;
                    } else {
                        name = ChatColor.of(new Color(185, 200, 200)) + name;
                    }
                     */

                    // obscure public health if invisible
                    if(p.hasPotionEffect(PotionEffectType.INVISIBILITY)) { h = "20"; }

                    String healthvalue = ChatColor.RED + h;
                    String healthheart = ChatColor.DARK_RED + "♥";

                    // changes health display colors if player has effects
                    if (p.hasPotionEffect(PotionEffectType.POISON)) {
                        healthvalue = ChatColor.of(new Color(187, 183, 66)) + h;
                        healthheart = ChatColor.of(new Color(139, 135, 18)) + "♥";
                    }
                    else if (p.hasPotionEffect(PotionEffectType.WITHER)) {
                        healthvalue = ChatColor.DARK_GRAY + h;
                        healthheart = ChatColor.BLACK + "♥";
                    }
                    else if (p.isFrozen()) {
                        healthvalue = ChatColor.AQUA + h;
                        healthheart = ChatColor.DARK_AQUA + "♥";
                    }

                    // makes health display flash if damage taken
                    if (map.containsKey(p.getUniqueId()) && !(p.hasPotionEffect(PotionEffectType.INVISIBILITY))) {
                        double oldhealth = map.get(p.getUniqueId());
                        if (oldhealth > health) {
                            healthvalue = ChatColor.of(new Color(180, 180, 180)) + h;
                            healthheart = ChatColor.of(new Color(210, 210, 210)) + "♥";
                        }
                    }
                    map.put(p.getUniqueId(), health);

                    // changes tab list display
                    p.setPlayerListName(afk + name + realName + healthvalue + healthheart);
                }
            }
        }, 0, 20);
    }

    public static boolean isPlayerInGroup(Player player, String group) {
        return player.hasPermission("group." + group);
    }

    public static String getTextContent(net.kyori.adventure.text.Component component) {
        String out = "";

        if (component instanceof net.kyori.adventure.text.TextComponent text) out += text.content();
        for (Component child : component.children()) out += getTextContent(child);

        return out;
    }

}
