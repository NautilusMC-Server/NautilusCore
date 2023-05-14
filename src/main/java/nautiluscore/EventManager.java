package nautiluscore;

import nautiluscore.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventManager implements Listener {

    private NautilusCore plugin;

    public EventManager(NautilusCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.d().updatePlayer(e.getPlayer().getUniqueId());
        //new players
        if(!plugin.d().hasData(e.getPlayer().getUniqueId())) {
            for(Player p: Bukkit.getOnlinePlayers()) {
                if(p.equals(e.getPlayer())) continue;
                p.playSound(p.getLocation(),Sound.BLOCK_NOTE_BLOCK_BELL,10,1);
                p.sendMessage(Text.c("&3Welcome &b" + e.getPlayer().getName() + "&3 to Nautilus&b&lMC&r&3!"));
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        plugin.d().setLastLocation(e.getPlayer());
    }

    @EventHandler
    // on message
    public void onPlayerMessage(PlayerChatEvent e) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(e.getMessage().toLowerCase().contains(p.displayName().toString().toLowerCase()) ||
                    e.getMessage().toLowerCase().contains(p.getName().toLowerCase())) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10, 2);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10, 4F);
                    }
                }, 2);
            }
        }
    }

}
