package nautiluscore.runnable;

import nautiluscore.NautilusCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SleepManager implements Listener {

    private NautilusCore plugin;

    public SleepManager(NautilusCore plugin) {
        this.plugin = plugin;
    }

    protected boolean sleepingCounter = false;
    protected BukkitTask sleepingTask;
    public long lastVoucher = 0L;

    private ArrayList<UUID> countsForSleep = new ArrayList<>();

    public void slow() { //if player is afk for 5-10 minutes, doesn't count / if not afk & in world, counts

        HashMap<UUID, Boolean> map = new HashMap<>(); // Player UUID, AFK T/F

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            public void run() {

                for (Player p : Bukkit.getOnlinePlayers()) {

                    boolean AFK = plugin.getAfkManager().isAfk(p);

                    if (map.containsKey(p.getUniqueId())) {
                        boolean prevAFK = map.get(p.getUniqueId());
                        //player afk, has been for >5 minutes
                        if (AFK && prevAFK) {
                            countsForSleep.remove(p.getUniqueId());
                        }
                    }
                    map.put(p.getUniqueId(), AFK);

                }
            }
        }, 20, 6000);
    }

    public void quick() { //if player is in nether or end, doesn't count for sleep

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            public void run() {

                for (Player p : Bukkit.getOnlinePlayers()) {

                    String world = p.getLocation().getWorld().getName();

                    //if in nether or end, doesn't count
                    if (world.equalsIgnoreCase("world_nether") || world.equalsIgnoreCase("world_the_end")) {
                        countsForSleep.remove(p.getUniqueId());
                        continue;
                    }

                    //if not afk and not in nether or end, counts
                    if (!plugin.getAfkManager().isAfk(p)) {
                        countsForSleep.add(p.getUniqueId());
                    }

                }
            }
        }, 80, 80);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        countsForSleep.add(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        countsForSleep.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onBedEnterEvent(PlayerBedEnterEvent e) {
        if (!sleepingCounter) {
            sleepingTask = plugin.getServer().getScheduler().runTask(plugin, new SleepRunnable());
            sleepingCounter = true;
        }
    }

    private class SleepRunnable implements Runnable {
        @Override
        public void run() {
            ArrayList<Player> sleeping = new ArrayList<>();
            int players = 0;
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (p.isSleeping()) sleeping.add(p);
                if (countsForSleep.contains(p.getUniqueId())) {
                    players++;
                }
            }

            if (sleeping.size() == 0) {
                sleepingTask.cancel();
                sleepingCounter = false;
                return;
            }

            int goal = (int) (players * plugin.d().getConfig().getDouble("sleepRatio") + .5);

            for (Player p : sleeping) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&',
                        "&b&lSLEEPING" + " &7- &f" + sleeping.size() + "&7/&e" + goal + " &7players required for day")));
            }

            if (sleeping.size() >= goal) {
                // Wait 1 second before turning to day
                plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        sleeping.get(0).getWorld().setTime(0);
                        if (Instant.now().toEpochMilli() - lastVoucher > 3600000) { // 1 hour
                            sleeping.get(0).getWorld().setThundering(false);
                            sleeping.get(0).getWorld().setStorm(false);
                        }
                        plugin.getServer().broadcastMessage(ChatColor.GREEN + "Night has been skipped as " + sleeping.size() + " people slept.");
                        sleepingCounter = false;
                        sleepingTask.cancel();
                    }
                }, 20);
            } else {
                plugin.getServer().getScheduler().runTaskLater(plugin, new SleepRunnable(), 20);
            }
        }
    }
}


