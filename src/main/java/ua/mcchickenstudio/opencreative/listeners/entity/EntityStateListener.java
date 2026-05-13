/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
 *
 * OpenCreative+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenCreative+ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ua.mcchickenstudio.opencreative.listeners.entity;

import com.destroystokyo.paper.event.entity.*;
import com.destroystokyo.paper.event.entity.WitchReadyPotionEvent;
import io.papermc.paper.event.entity.*;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.fighting.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.interaction.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.inventory.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.movement.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.state.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.movement.EnteredVehicleEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.movement.PlayerVehicleExitEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;

import java.util.List;

public final class EntityStateListener implements Listener {

    @EventHandler
    public void onEntityMove(EntityMoveEvent event) {
        if (!OpenCreative.getSettings().getWorldFixerSettings().shouldFixBadEntitiesInAir()) return;
        Entity entity = event.getEntity();
        if (!isBadEntityForMovement(entity)) return;
        if (!isBadEntityMovementState(entity)) {
            return;
        }
        float size = entity instanceof Projectile ? 30f : 1f;
        List<Entity> nearbyEntities = entity.getNearbyEntities(size, size, size);
        int badEntities = 0;
        for (Entity same : nearbyEntities) {
            if (isBadEntityForMovement(same) && isBadEntityMovementState(same)) {
                badEntities++;
            }
            if (badEntities > 1) {
                entity.remove();
                break;
            }
        }
    }

    private boolean isBadEntityForMovement(@NotNull Entity entity) {
        if (entity instanceof Projectile) return true;
        if (entity instanceof ArmorStand) return true;
        if (entity instanceof Minecart || entity instanceof Boat) {
            return entity.getPassengers().isEmpty();
        }
        return false;
    }

    private boolean isBadEntityMovementState(@NotNull Entity entity) {
        return !entity.isOnGround() || entity.isInWaterOrBubbleColumn();
    }

    @EventHandler
    public void onCollision(VehicleEntityCollisionEvent event) {
        if (!OpenCreative.getSettings().getWorldFixerSettings().shouldFixVehicleCollisions()) return;
        if (event.getEntity() instanceof Minecart first && event.getVehicle() instanceof Minecart second) {
            long firstLastCollision = getMetadata(first, "oc_vehicle_last_collision");
            long secondLastCollision = getMetadata(second, "oc_vehicle_last_collision");
            long firstCollisions = getMetadata(first, "oc_vehicle_collisions");
            long secondCollisions = getMetadata(second, "oc_vehicle_collisions");
            long collisionsAmount = Math.max(firstCollisions, secondCollisions);
            long lastCollision = Math.max(firstLastCollision, secondLastCollision);
            if (System.currentTimeMillis() - lastCollision < 500) {
                if (collisionsAmount > OpenCreative.getSettings().getWorldFixerSettings().getMaxMinecartCollisionsAmount()) {
                    event.setCancelled(true);
                    first.remove();
                    for (Entity entity : first.getNearbyEntities(20, 20, 20)) {
                        if (entity instanceof Player player) {
                            player.spawnParticle(Particle.EXPLOSION, first.getLocation(), 1);
                        }
                    }
                } else {
                    first.setMetadata("oc_vehicle_collisions", new FixedMetadataValue(OpenCreative.getPlugin(),
                            firstCollisions + 1));
                    second.setMetadata("oc_vehicle_collisions", new FixedMetadataValue(OpenCreative.getPlugin(),
                            secondCollisions + 1));
                }
            } else {
                first.setMetadata("oc_vehicle_last_collision", new FixedMetadataValue(OpenCreative.getPlugin(),
                        System.currentTimeMillis()));
                second.setMetadata("oc_vehicle_last_collision", new FixedMetadataValue(OpenCreative.getPlugin(),
                        System.currentTimeMillis()));
                first.removeMetadata("oc_vehicle_collisions", OpenCreative.getPlugin());
                second.removeMetadata("oc_vehicle_collisions", OpenCreative.getPlugin());
            }
        }
    }

    public long getMetadata(@NotNull Entity entity, @NotNull String key) {
        for (MetadataValue value : entity.getMetadata(key)) {
            if (OpenCreative.getPlugin().equals(value.getOwningPlugin())) {
                return value.asLong();
            }
        }
        return 0L;
    }

    @EventHandler
    public void onEntityEvent(PigZombieAngerEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new PigZombieAngeredEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(BatToggleSleepEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityBatToggledSleepModeEvent(event).callEvent();
    }

    @EventHandler
    public void onSlimeSplit(SlimeSplitEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new SlimeSplittedEvent(event).callEvent();
    }

    @EventHandler
    public void onWitchReady(WitchReadyPotionEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null)
            new ua.mcchickenstudio.opencreative.coding.blocks.events.entity.state.WitchReadyPotionEvent(event).callEvent();
    }

    @EventHandler
    public void onSheepRegrow(SheepRegrowWoolEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new SheepRegrownWoolEvent(event).callEvent();
    }

    @EventHandler
    public void onPufferfishState(PufferFishStateChangeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new PufferfishStateChangedEvent(event).callEvent();
    }

    @EventHandler
    public void onCreeperIgnite(CreeperIgniteEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new CreeperIgnitedEvent(event).callEvent();
    }

    @EventHandler
    public void onCreeperPower(CreeperPowerEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new CreeperPoweredEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityLoveMode(EntityEnterLoveModeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityEnteredLoveModeEvent(event).callEvent();
    }

    @EventHandler
    public void onTurtleHome(TurtleGoHomeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new TurtleGoesHomeEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityResurrect(EntityResurrectEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityResurrectedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityEvent(EntityPotionEffectEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityPotionEffectedEvent(event).callEvent();
    }

    @EventHandler
    public void onWardenAnger(WardenAngerChangeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new WardenAngerChangedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityAirChange(EntityAirChangeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityAirChangedEvent(event).callEvent();
    }

    @EventHandler
    public void onEnterBlock(EntityEnterBlockEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityEnteredBlockEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityJump(EntityJumpEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityJumpedEvent(event).callEvent();
    }

    @EventHandler
    public void onHorseJump(HorseJumpEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new HorseJumpedEvent(event).callEvent();
    }

    @EventHandler
    public void onEndermanEscape(EndermanEscapeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EndermanEscapedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityDropItem(EntityDropItemEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityDroppedItemEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityPickedUpItemEvent(event).callEvent();
    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new ItemMergedEvent(event).callEvent();
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new ItemDespawnedEvent(event).callEvent();
    }

    @EventHandler
    public void onDamageItem(EntityDamageItemEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityDamagedItemEvent(event).callEvent();
    }

    @EventHandler
    public void onPiglinBarter(PiglinBarterEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new PiglinBarteredEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityInteractedBlockEvent(event).callEvent();
    }

    @EventHandler
    public void onTurtleLayEgg(TurtleLayEggEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new TurtleLaysEggEvent(event).callEvent();
    }

    @EventHandler
    public void onFireworkExplode(FireworkExplodeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (event.getEntity().getPersistentDataContainer().has(WorldUtils.getDoNotHurtAnyoneKey())) return;
        if (planet != null) new FireworkExplodedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityDiedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityBow(EntityShootBowEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityShotBowEvent(event).callEvent();
    }

    @EventHandler
    public void onWitchPotion(WitchThrowPotionEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new WitchThrownPotionEvent(event).callEvent();
    }

    @EventHandler
    public void onWitchConsumePotion(WitchConsumePotionEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new WitchConsumedPotionEvent(event).callEvent();
    }

    @EventHandler
    public void onLoadCrossbow(EntityLoadCrossbowEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityLoadedCrossbowEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityRegainedHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) return;
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityRegainedHealthEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) return;
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityGetDamagedEvent(event).callEvent();
    }

    @EventHandler
    public void onShulkerDuplication(ShulkerDuplicateEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new ShulkerDuplicationEvent(event).callEvent();
    }

    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) {
            new HangingBreakEvent(event).callEvent();
            if (!event.isCancelled() && event.getEntity() instanceof Player player) {
                if (!planet.isOwner(player)) {
                    OpenCreative.getWander(player).getGriefStats().addDestroyedHangingsAmount(1);
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHitBlock(ProjectileHitEvent event) {
        if (event.getHitBlock() == null) return;
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new ProjectileHitBlockEvent(event, event.getHitBlock()).callEvent();
    }

    @EventHandler
    public void onEntityCombustedByEntity(EntityCombustByEntityEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityCombustedByEntityEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityCombustedByEntity(EntityCombustByBlockEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityCombustedByBlockEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityMountedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityDismountedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityRemoved(EntityRemoveFromWorldEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityRemovedEvent(event).callEvent();
    }

    @EventHandler
    public void onEntityBorn(EntityBreedEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new EntityBornEvent(event).callEvent();
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        Entity entity = event.getEntered();
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(entity.getWorld());
        if (planet == null) return;
        if (entity instanceof Player player) {
            new EnteredVehicleEvent(player, event).callEvent();
        } else {
            new EntityEnteredVehicleEvent(event).callEvent();
        }
    }

    @EventHandler
    public void onVehicleLeave(VehicleExitEvent event) {
        Entity entity = event.getExited();
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(entity.getWorld());
        if (planet == null) return;
        if (entity instanceof Player player) {
            new PlayerVehicleExitEvent(player, event).callEvent();
        } else {
            new EntityVehicleExitEvent(event).callEvent();
        }
    }


}
