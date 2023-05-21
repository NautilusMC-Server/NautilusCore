package nautiluscore;

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import nautiluscore.NautilusCore;
import nautiluscore.util.Text;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.BoundingBox;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;
import java.util.List;

public class ProtectionManager implements Listener {

    private NautilusCore plugin;

    public ProtectionManager(NautilusCore plugin) {
        this.plugin = plugin;
    }

    //util
    private void alert(Player p) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Text.c("&cYou can't do that here")));
    }

    private boolean isProtected(Location l) {
        return box().contains(l.getX(), l.getY(), l.getZ());
    }

    private BoundingBox box() {
        Location one = plugin.d().getConfig().getLocation("spawn1");
        Location two = plugin.d().getConfig().getLocation("spawn2");
        return new BoundingBox(one.getX(), one.getWorld().getMinHeight(), one.getZ(), two.getX(), two.getWorld().getMaxHeight(), two.getZ());
    }

    @EventHandler
    //entities spawn
    public void onSpawnEvent(EntitySpawnEvent e) {
        if(!(e.getEntity() instanceof Player)) {
            if(isProtected(e.getLocation())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    //break a block
    public void onBlockBreak(BlockBreakEvent e) {
        if(isProtected(e.getBlock().getLocation())) {
            if(!e.getPlayer().hasPermission("group.staff")) {
                alert(e.getPlayer());
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    //place a block
    public void onBlockPlace(BlockPlaceEvent e) {
        if(isProtected(e.getBlock().getLocation())) {
            if(!e.getPlayer().hasPermission("group.staff")) {
                alert(e.getPlayer());
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    //left or right-click a block
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null) return;
        if(isProtected(e.getClickedBlock().getLocation())) {
            if(!e.getPlayer().hasPermission("group.staff")) {
                alert(e.getPlayer());
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    //dedicated lectern suppressor
    public void onLecternTakeBook(PlayerTakeLecternBookEvent e) {
        if(isProtected(e.getLectern().getLocation())) {
            if(!e.getPlayer().hasPermission("group.staff")) {
                alert(e.getPlayer());
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    //dedicated bone meal suppressor
    public void onBoneMeal(BlockFertilizeEvent e) {
        if(e.getPlayer() == null) return;
        for(BlockState b : e.getBlocks()) {
            if(isProtected(b.getLocation())) {
                if(!e.getPlayer().hasPermission("group.staff")) {
                    alert(e.getPlayer());
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    //dedicated bucket suppressor
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        if(isProtected(e.getBlock().getLocation())) {
            if(!e.getPlayer().hasPermission("group.staff")) {
                alert(e.getPlayer());
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    //dedicated crop trample suppressor
    public void onCropTrample(PlayerInteractEvent e) {
        if(e.getAction().equals(Action.PHYSICAL) && e.getInteractionPoint() != null && e.getInteractionPoint().getBlock().getType().equals(Material.FARMLAND)) {
            if(isProtected(e.getInteractionPoint())) {
                if(!e.getPlayer().hasPermission("group.staff")) {
                    alert(e.getPlayer());
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    //dedicated frame suppressor
    public void onHangingPlace(HangingPlaceEvent e) {
        if(e.getPlayer() == null) return;
        if(isProtected(e.getBlock().getLocation())) {
            if(!e.getPlayer().hasPermission("group.staff")) {
                alert(e.getPlayer());
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    //dedicated frame suppressor 2
    public void onHangingBreak(HangingBreakByEntityEvent e) {
        if(!(e.getRemover() instanceof Player player)) return;
        if(isProtected(e.getEntity().getLocation())) {
            if(!player.hasPermission("group.staff")) {
                alert(player);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent e) {
        if(e.getAttacker() == null || !(e.getAttacker() instanceof Player player)) return;
        if(isProtected(e.getVehicle().getLocation())) {
            if(!player.hasPermission("group.staff")) {
                alert(player);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    //right-click an entity
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        //using an item while looking at the entity which will take priority
        List<Material> usable = List.of(Material.BOW, Material.CROSSBOW, Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE);
        if(e.getPlayer().getItemInUse() != null && usable.contains(e.getPlayer().getItemInUse().getType())) return;

        Player player = e.getPlayer();
        if(isProtected(e.getRightClicked().getLocation())) {
            if(!player.hasPermission("group.staff")) {
                alert(player);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    //rotate item frame
    public void onFrameRotate(PlayerInteractEntityEvent e) {
        if(e.getRightClicked() instanceof Hanging) {

            Player player = e.getPlayer();
            if(isProtected(e.getRightClicked().getLocation())) {
                if(!player.hasPermission("group.staff")) {
                    alert(player);
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    //left-click an entity
    public void onPlayerAttackEvent(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player player)) return;

        if(isProtected(e.getEntity().getLocation())) {
            if(!player.hasPermission("group.staff")) {
                alert(player);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    //explosion breaks hanging
    public void onHangingBreak(HangingBreakEvent e) {
        if(e.getCause().equals(HangingBreakEvent.RemoveCause.EXPLOSION) && isProtected(e.getEntity().getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    //explosion destroys blocks
    public void onEntityExplode(EntityExplodeEvent e) {
        if(e.getEntity() instanceof Creeper) {
            e.blockList().clear();
            return;
        }
        e.blockList().removeIf(b -> isProtected(b.getLocation()) && !b.getType().equals(Material.TNT));
    }

    @EventHandler
    //enderman picks up block or entity tramples farmland
    public void onChangeBlock(EntityChangeBlockEvent e) {
        if(e.getEntity() instanceof Enderman) {
            e.setCancelled(true);
        }
        if(!isProtected(e.getBlock().getLocation())) return;
        if(!(e.getEntity() instanceof Player) && e.getBlock().getType().equals(Material.FARMLAND)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    //door broken by monster
    public void onChangeBlock(EntityBreakDoorEvent e) {
        if(e.getEntity() instanceof Monster) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    //explosion again
    public void onExplosion(BlockExplodeEvent e) {
        e.blockList().removeIf(b -> isProtected(e.getBlock().getLocation()) && !b.getType().equals(Material.TNT));
    }

    @EventHandler
    //entity damage + particles
    public void onEntityDamage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player)) return;

        e.setCancelled(true);
        Location loc = e.getEntity().getOrigin();
        Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(0,255,0), 1);
        e.getEntity().getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(), loc.getZ(), 10, 0, 0, 0, dust);
    }

    @EventHandler
    //projectile collide
    public void onProjectileCollide(ProjectileCollideEvent e) {
        if(!(e.getEntity().getShooter() instanceof Player player)) return;

        if(isProtected(e.getCollidedWith().getLocation())) {
            alert(player);
            e.setCancelled(true);
        }

        if((e.getCollidedWith() instanceof Hanging || e.getCollidedWith() instanceof EnderCrystal)
                && e.getCollidedWith().isVisualFire()) e.getCollidedWith().setVisualFire(false);
    }

    @EventHandler
    //hunger loss
    public void onHungerEvent(FoodLevelChangeEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        if(isProtected(e.getEntity().getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    //food eat
    public void onEatFood(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        if(isProtected(e.getPlayer().getLocation())) {
            if(!player.hasPermission("group.staff")) {
                alert(player);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    //liquid flows across claim border
    public void onLiquidFlow(BlockFromToEvent e) {
        if(!isProtected(e.getToBlock().getLocation())) return;
        Block b = e.getToBlock();

        BoundingBox box = box();

        if(b.getX() == box.getMinX() || b.getX() == box.getMaxX() ||
                b.getZ() == box.getMinZ() || b.getZ() == box.getMaxZ()) {
            e.setCancelled(true);
        }

    }

    @EventHandler
    //piston extends across claim border
    public void onPistonExtend(BlockPistonExtendEvent e) {

        List<Block> blocks = e.getBlocks();

        //normal piston, not sticky
        if(e.getBlock().getType().equals(Material.PISTON)) {
            ArrayList<Block> connected = new ArrayList<>();
            Block b = e.getBlock().getRelative(e.getDirection());
            int count = 0;
            while(!b.getType().equals(Material.AIR) && count <= 12) {
                count++;
                connected.add(b);
                b = b.getRelative(e.getDirection());
            }
            blocks = connected;
        }

        for(Block b : blocks) {

            if(!isProtected(b.getRelative(e.getDirection()).getLocation())) return;

            BoundingBox box = box();

            if(Math.abs(b.getX() - box.getMinX()) <= 1 || Math.abs(b.getX() - box.getMaxX()) <= 1 ||
                    Math.abs(b.getZ() - box.getMinZ()) <= 1 || Math.abs(b.getZ() - box.getMaxZ()) <= 1) {
                e.setCancelled(true);
                break;
            }

        }

    }

    @EventHandler
    //dispenser dispenses across claim border
    public void onDispense(BlockDispenseEvent e) {

        Block b = e.getBlock();

        if(isProtected(b.getLocation())) return;

        List<Block> list = List.of(b.getRelative(BlockFace.NORTH), b.getRelative(BlockFace.EAST),
                b.getRelative(BlockFace.SOUTH), b.getRelative(BlockFace.WEST));
        for(Block adj : list) {
            //different sides of admin claim border
            if (isProtected(adj.getLocation())) {
                e.setCancelled(true);
                return;
            }
        }

    }

    //--chairs--

    @EventHandler
    public void onPlayerSit(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand().equals(EquipmentSlot.HAND) &&
                e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR) &&
                (Tag.STAIRS.isTagged(e.getClickedBlock().getType()))) {
            for(Entity entity : e.getClickedBlock().getLocation().getNearbyEntities(1.5, 1.5, 1.5)) {
                if(entity instanceof Egg) return;
            }
            if (e.getPlayer().isInsideVehicle()) return;

            Egg toSitOn = (Egg) e.getClickedBlock().getLocation().getWorld().spawn(
                    e.getClickedBlock().getLocation().add(0.5, 0.2, 0.5), Egg.class, (settings) -> {
                        settings.setGravity(false);
                        settings.setInvulnerable(true);
                    });
            toSitOn.addPassenger(e.getPlayer());

        }
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent e) {
        if(e.getDismounted() instanceof Egg) {
            e.getDismounted().remove();
        }
    }

    @EventHandler
    public void onPlayerTeleportOffChair(PlayerTeleportEvent e) {
        if(e.getPlayer().getVehicle() instanceof Egg) {
            e.getPlayer().getVehicle().remove();
        }
    }

    @EventHandler
    public void onPlayerDeathOnChair(PlayerDeathEvent e) {
        if(e.getPlayer().getVehicle() instanceof Egg) {
            e.getPlayer().getVehicle().remove();
        }
    }

    @EventHandler
    public void onChairBreak(BlockBreakEvent e) {
        for(Entity entity : e.getBlock().getLocation().getNearbyEntities(1.5, 1.5, 1.5)) {
            if(entity instanceof Egg) entity.remove();
        }
    }

}