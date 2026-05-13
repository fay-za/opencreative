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

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.player.fighting.*;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetFlags;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getPlayerLocaleComponent;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInLobby;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isLobbyWorld;

public final class EntityDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player victim) {
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(victim);
            if (planet != null) {
                if (planet.getMode() == Planet.Mode.BUILD) {
                    event.setCancelled(true);
                    if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                        BukkitRunnable runnable = new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (planet.isLoaded() && planet.getTerritory().getWorld().equals(victim.getWorld()) && planet.getMode() == Planet.Mode.BUILD) {
                                    victim.teleport(victim.getWorld().getSpawnLocation().add(0, 0.5, 0));
                                }
                                planet.getTerritory().removeBukkitRunnable(this);
                            }
                        };
                        planet.getTerritory().addBukkitRunnable(runnable);
                        runnable.runTaskLater(OpenCreative.getPlugin(), 1L);
                    }
                }
                if (OpenCreative.getPlanetsManager().getDevPlanet(victim) != null) {
                    event.setCancelled(true);
                }

                if (isNearSpawn(victim)) {
                    event.setCancelled(true);
                }

                byte playerDamageFlag = planet.getFlagValue(PlanetFlags.PlanetFlag.PLAYER_DAMAGE);
                if (playerDamageFlag == 2) event.setCancelled(true);
                if (playerDamageFlag == 3 && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                    event.setCancelled(true);
                if (playerDamageFlag == 4 && event.getCause() == EntityDamageEvent.DamageCause.FALL)
                    event.setCancelled(true);
                if (playerDamageFlag == 5 && (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.FALL))
                    event.setCancelled(true);

                new PlayerDamagedEvent(victim, event).callEvent();
            } else if (isEntityInLobby(victim)) {
                event.setCancelled(true);
                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    World world = victim.getWorld();
                    BukkitRunnable runnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (world.equals(victim.getWorld())) {
                                victim.teleport(world.getSpawnLocation());
                            }
                        }
                    };
                    runnable.runTaskLater(OpenCreative.getPlugin(), 1L);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {

        if (event.getEntity().getPersistentDataContainer().has(WorldUtils.getDoNotHurtAnyoneKey())) {
            event.setCancelled(true);
        }

        if (event.getEntity() instanceof Player victim) {
            // Player damages player
            if (event.getDamager() instanceof Player damager) {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(damager);
                if (planet != null) {
                    if (planet.getMode() == Planet.Mode.BUILD) {
                        damager.sendActionBar(getPlayerLocaleComponent("world.build-mode.cant-damage", damager));
                    } else if (isNearSpawn(victim)) {
                        damager.sendActionBar(getPlayerLocaleComponent("world.play-mode.cant-damage-near-spawn", damager));
                        new PlayerDamagesPlayerEvent(damager, victim, event).callEvent();
                    } else {
                        new PlayerDamagesPlayerEvent(damager, victim, event).callEvent();
                    }
                }
                // Mob damages player
            } else {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(victim);
                if (planet != null) {
                    new MobDamagesPlayerEvent(victim, event).callEvent();
                }
            }
        } else {
            // Player damages mob
            if (event.getDamager() instanceof Player damager) {
                Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer(damager);
                if (planet != null) {
                    new PlayerDamagesMobEvent(damager, event).callEvent();
                } else {
                    if (isEntityInLobby(damager) && OpenCreative.getSettings().getLobbySettings().isDamagingMobsDisallowed()
                            && !damager.hasPermission("opencreative.lobby.damaging-mobs.bypass")) {
                        event.setCancelled(true);
                        damager.sendActionBar(getPlayerLocaleComponent("not-for-lobby", damager));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onExplosionDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION &&
                event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            return;
        }
        if (isLobbyWorld(event.getEntity().getWorld())
                && OpenCreative.getSettings().getLobbySettings().areExplosionsDisabled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player player && !(event.getEntity() instanceof Player)) {
            if (OpenCreative.getPlanetsManager().getPlanetByPlayer(player) != null) {
                new PlayerKilledMobEvent(player, event.getEntity(), event).callEvent();
            }
        }
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByPlayer((Player) event.getEntity());
        if (isEntityInLobby(event.getEntity())) {
            event.setCancelled(true);
        }
        if (planet != null) {
            if (planet.getMode() == Planet.Mode.BUILD) {
                event.setCancelled(true);
            } else {
                new HungerChangeEvent((Player) event.getEntity(), event).callEvent();
            }
        }
    }

    public boolean isNearSpawn(@NotNull Player player) {
        return player.getLocation().distance(player.getWorld().getSpawnLocation()) < 5;
    }
}
