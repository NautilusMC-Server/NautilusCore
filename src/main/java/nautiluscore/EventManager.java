package nautiluscore;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class EventManager implements Listener {

    private NautilusCore plugin;

    public EventManager(NautilusCore plugin) {
        this.plugin = plugin;
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
