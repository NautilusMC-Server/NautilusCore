package nautiluscore.data;

import nautiluscore.NautilusCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

public class DataAccessor {

    private NautilusCore plugin;

    public DataAccessor(NautilusCore plugin) {
        this.plugin = plugin;
    }

    //users
    public void updatePlayer(UUID uuid) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("lastLogin", Instant.now().toEpochMilli());
        map.put("name", Bukkit.getOfflinePlayer(uuid).getName());
        map.put("ip", Bukkit.getPlayer(uuid).getAddress().getHostString());
        map.put("lastLocation", Bukkit.getPlayer(uuid).getLocation());

        plugin.getDataManager().getConfig().createSection("users." + uuid.toString(), map);
        plugin.getDataManager().saveConfig();
    }

    public void setLastLocation(Player p) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("users." + p.getUniqueId());
        cfg.set("lastLocation", p.getLocation());
        plugin.getDataManager().saveConfig();
    }

    //getters for .d().* calls
    //config
    public ConfigurationSection getConfig() {
        return plugin.getDataManager().getConfig().getConfigurationSection("config");
    }

    //users
    public long getLastLogin(UUID uuid) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("users." + uuid);
        return cfg.getLong("lastLogin");
    }

    public String getIp(UUID uuid) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("users." + uuid);
        return cfg.getString("ip");
    }

    public Location getLastLocation(UUID uuid) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("users." + uuid);
        return (Location)cfg.get("lastLocation");
    }

    public boolean hasData(UUID uuid) {
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("users");
        return cfg.contains(uuid.toString());
    }

    //general utility
    public HashMap<String, Object> getAllConfig() {
        HashMap<String, Object> r = new HashMap<>();
        ConfigurationSection cfg = plugin.getDataManager().getConfig().getConfigurationSection("config");
        for(String key : cfg.getKeys(false)) {
            r.put(key, cfg.get(key));
        }
        return r;
    }


}
