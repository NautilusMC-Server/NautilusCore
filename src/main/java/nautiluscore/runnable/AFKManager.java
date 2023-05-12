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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class AFKManager implements Listener {

    private NautilusCore plugin;
    private ArrayList<UUID> afk; //player afk
    private HashMap<UUID, Location> all; //player, location (continuous)

    public AFKManager(NautilusCore plugin) {
        this.plugin = plugin;
        this.afk = new ArrayList<>();
        this.all = new HashMap<>();
    }

    public void afk() {

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            public void run() {

                for(Player p : Bukkit.getOnlinePlayers()) {

                    if(!all.containsKey(p.getUniqueId())) {
                        all.put(p.getUniqueId(), p.getLocation());
                        continue;
                    }

                    if(all.get(p.getUniqueId()).distance(p.getLocation()) < 3 &&
                        all.get(p.getUniqueId()).getDirection().equals(p.getLocation().getDirection())) {
                        setAfk(p);
                    }

                    all.put(p.getUniqueId(), p.getLocation());

                }
            }
        }, 20, 3600); //every 3 minutes todo config
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        all.put(e.getPlayer().getUniqueId(), e.getPlayer().getLocation());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        all.remove(e.getPlayer().getUniqueId());
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
        afk.add(p.getUniqueId());
    }

    public void setNotAfk(Player p) {
        if(!isAfk(p)) return;
        p.sendMessage(Text.c("&7You are no longer AFK."));
        afk.remove(p.getUniqueId());
    }

    public boolean isAfk(Player p) {
        return afk.contains(p.getUniqueId());
    }

}
