package nautiluscore.runnable;

import nautiluscore.NautilusCore;
import nautiluscore.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class AFKManager implements Listener {

    private NautilusCore plugin;
    private HashMap<Player, Location> afk; //player afk, where they were when they went afk
    private HashMap<Player, Location> all; //player, location (continuous)

    public AFKManager(NautilusCore plugin) {
        this.plugin = plugin;
        this.afk = new HashMap<>();
        this.all = new HashMap<>();
    }

    public void afk() {

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            public void run() {

                for(Player p : Bukkit.getOnlinePlayers()) {

                    if(all.get(p).distance(p.getLocation()) < 5) {
                        setAfk(p);
                    }

                    all.put(p, p.getLocation());

                }
            }
        }, 20, 3600); //every 3 minutes todo config
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        all.put(e.getPlayer(), e.getPlayer().getLocation());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        all.remove(e.getPlayer());
    }

    @EventHandler
    public void onMoveOrient(PlayerMoveEvent e) {
        if(e.hasChangedOrientation()) {
            setNotAfk(e.getPlayer());
        }
    }

    public void setAfk(Player p) {
        p.sendMessage(Text.c("&7You are now AFK."));
        //todo implement command
        afk.put(p, p.getLocation());
    }

    public void setNotAfk(Player p) {
        if(!isAfk(p)) return;
        p.sendMessage(Text.c("&7You are no longer AFK."));
        afk.remove(p);
    }

    public boolean isAfk(Player p) {
        return afk.containsKey(p);
    }

}
