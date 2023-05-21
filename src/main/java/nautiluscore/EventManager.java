package nautiluscore;

import nautiluscore.util.Text;
import net.minecraft.world.entity.item.ItemEntity;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.*;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.UUID;

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
    public void onAnvil(PrepareAnvilEvent e) {
        //for renaming
        if(e.getInventory().getFirstItem() != null &&
            e.getInventory().getSecondItem() == null) {
            e.getInventory().setRepairCost(0);
        }
    }

    @EventHandler
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

//    private static final EntityDataAccessor<Integer> ITEM_SLOT = SynchedEntityData.defineId(ItemEntity.class, EntityDataSerializers.INT);
    private static final String OWNER_KEY = "owner";
    private static final String SLOT_KEY = "slot";
    private static final Map<UUID, ItemStack[]> DEATH_ITEMS = new java.util.HashMap<>();

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Inventory inv = e.getPlayer().getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, SLOT_KEY), PersistentDataType.INTEGER, i);
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, OWNER_KEY), PersistentDataType.STRING, e.getPlayer().getUniqueId().toString());
            item.setItemMeta(meta);
        }
    }

    @EventHandler
    public void itemSpawnEvent(ItemSpawnEvent e) {
        if (e.getEntity().getItemStack().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, OWNER_KEY))) {
            ItemEntity nms = (ItemEntity) ((CraftItem) e.getEntity()).getHandle();
            // copied from ItemEntity#setItem
            int defaultDespawnTime = nms.level.paperConfig().entities.spawning.altItemDespawnRate.enabled ? nms.level.paperConfig().entities.spawning.altItemDespawnRate.items.getOrDefault(nms.getItem().getItem(), nms.level.spigotConfig.itemDespawnRate) : nms.level.spigotConfig.itemDespawnRate;
            int despawnTime = plugin.d().getConfig().getInt("deathItemDespawnSeconds") * 20;
            int age = defaultDespawnTime - despawnTime;

            if (age <= Short.MIN_VALUE) {
                plugin.getLogger().warning("Despawn time for death items is too low (" + despawnTime + "), and would be infinite. Defaulting to " + ((defaultDespawnTime - (Short.MIN_VALUE + 1))/20) + ". For infinite values, use -1.");

                age = Short.MIN_VALUE+1;
            }

            nms.age = age;
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        ItemStack item = e.getItem().getItemStack();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!container.has(new NamespacedKey(plugin, SLOT_KEY), PersistentDataType.INTEGER)) return;
        String owner = container.get(new NamespacedKey(plugin, OWNER_KEY), PersistentDataType.STRING);
        int slot = container.get(new NamespacedKey(plugin, SLOT_KEY), PersistentDataType.INTEGER);

        container.remove(new NamespacedKey(plugin, OWNER_KEY));
        container.remove(new NamespacedKey(plugin, SLOT_KEY));

        item.setItemMeta(meta);
        e.getItem().setItemStack(item);

        if (e.getEntity() instanceof Player player && player.getUniqueId().equals(UUID.fromString(owner))) {
            Inventory inv = player.getInventory();
            ItemStack invItem = inv.getItem(slot);

            if (invItem == null) {
                inv.setItem(slot, item);
                ((CraftPlayer) player).getHandle().take(((CraftItem) e.getItem()).getHandle(), item.getAmount());
                e.getItem().remove();
                e.setCancelled(true);
            }
        }
    }
}
