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

package ua.mcchickenstudio.opencreative.listeners.world;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import io.papermc.paper.event.block.BeaconActivatedEvent;
import io.papermc.paper.event.block.BeaconDeactivatedEvent;
import io.papermc.paper.event.block.TargetHitEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.world.PortalCreateEvent;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.events.entity.interaction.EntityExplodedEvent;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.blocks.*;
import ua.mcchickenstudio.opencreative.coding.blocks.events.world.blocks.BlockPhysicsEvent;
import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.planets.DevPlatform;
import ua.mcchickenstudio.opencreative.planets.Planet;
import ua.mcchickenstudio.opencreative.planets.PlanetFlags;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;

import java.util.Collection;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.isOutOfBorders;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isDevPlanet;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isLobbyWorld;

public final class BlockChangeListener implements Listener {

    @EventHandler
    public void onBlockChanged(BlockFadeEvent event) {
        World world = event.getBlock().getWorld();
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(world);
        if (planet != null) {
            if (planet.getFlagValue(PlanetFlags.PlanetFlag.BLOCK_CHANGING) == 2) {
                event.setCancelled(true);
            } else {
                new BlockFadedEvent(planet, event).callEvent();
            }
        }
    }

    @EventHandler
    public void onBlockChanged(BlockFormEvent event) {
        World world = event.getBlock().getWorld();
        if (isDevPlanet(world)) {
            DevPlanet devPlanet = OpenCreative.getPlanetsManager().getDevPlanet(world);
            if (devPlanet != null) {
                DevPlatform platform = devPlanet.getPlatformInLocation(event.getBlock().getLocation());
                if (platform == null) return;
                if (platform.getEventMaterial() == event.getBlock().getRelative(BlockFace.DOWN).getType() || platform.getActionMaterial() == event.getBlock().getRelative(BlockFace.DOWN).getType()) {
                    event.setCancelled(true);
                }
            }
        } else {
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(world);
            if (planet != null) {
                if (planet.getFlagValue(PlanetFlags.PlanetFlag.BLOCK_CHANGING) == 2) {
                    event.setCancelled(true);
                } else {
                    new BlockFormedEvent(planet, event).callEvent();
                }
            }
        }


    }

    @EventHandler
    public void onEntityExplosion(ExplosionPrimeEvent event) {
        if (event.getRadius() > 3) {
            event.setRadius(3);
        }
    }

    @EventHandler
    public void onEntityExplosion(EntityExplodeEvent event) {
        World world = event.getLocation().getWorld();
        if (isLobbyWorld(world)) {
            if (OpenCreative.getSettings().getLobbySettings().areExplosionsDisabled()) {
                event.blockList().clear();
                event.setCancelled(true);
            }
            return;
        }
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(world);
        if (planet != null) {
            if (planet.getFlagValue(PlanetFlags.PlanetFlag.BLOCK_EXPLOSION) == 2) {
                event.blockList().clear();
            } else if (!isDevPlanet(world)) {
                new EntityExplodedEvent(event).callEvent();
            } else {
                event.blockList().clear();
            }
        }
    }

    @EventHandler
    public void onBlockExplosion(BlockExplodeEvent event) {
        World world = event.getBlock().getLocation().getWorld();
        if (isLobbyWorld(world)) {
            if (OpenCreative.getSettings().getLobbySettings().areExplosionsDisabled()) {
                event.blockList().clear();
                event.setCancelled(true);
            }
            return;
        }
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(world);
        if (planet != null) {
            if (planet.getFlagValue(PlanetFlags.PlanetFlag.BLOCK_EXPLOSION) == 2) {
                event.blockList().clear();
            }
            new BlockExplodedEvent(planet, event).callEvent();
        }
        if (isDevPlanet(world)) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onBeacon(BeaconActivatedEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockBeaconActivatedEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onBeacon(BeaconDeactivatedEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockBeaconDeactivatedEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onPortal(PortalCreateEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getWorld());
        if (planet != null) new PortalCreatedEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onPiston(BlockPistonExtendEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockPistonExtendedEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onPiston(BlockPistonRetractEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockPistonRetractedEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onFallingBlock(EntityChangeBlockEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null && event.getEntityType() == EntityType.FALLING_BLOCK) {
            new BlockPhysicsEvent(planet, event).callEvent();
        }
    }

    @EventHandler
    public void onPhysics(org.bukkit.event.block.BlockPhysicsEvent event) {
        if (isOutOfBorders(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlock(BlockGrowEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockGrownEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onBlock(BlockIgniteEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockIgnitedEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onTntPrime(TNTPrimeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockTntPrimeEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        event.setItem(ItemUtils.fixItem(event.getItem()));
        if (event.getBlock().getBlockData() instanceof Directional directional) {
            Block block = event.getBlock().getRelative(directional.getFacing());
            String itemType = event.getItem().getType().name();
            if (itemType.contains("MINECART") || itemType.contains("SPAWN_EGG")) {
                Collection<Entity> nearEntities = block.getWorld().getNearbyEntities(block.getLocation(), 1, 1, 1);
                int minecarts = 0;
                for (Entity entity : nearEntities) {
                    if (entity instanceof Minecart) {
                        minecarts++;
                    }
                    if (minecarts >= 1) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockDispensedEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onBlockDispense(InventoryMoveItemEvent event) {
        event.setItem(ItemUtils.fixItem(event.getItem()));
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockBurnedEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onBlockCook(BlockCookEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockCookedEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onBlockExperience(BlockExpEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockExperienceDropEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onBlockBrew(BrewEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockBrewingEndEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockFurnaceBurnedEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onBrewingStandFuel(BrewingStandFuelEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockBrewingFuelEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onFluidChange(FluidLevelChangeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockFluidChangeEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onCauldronChange(CauldronLevelChangeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockCauldronChangeEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onCampfireStart(CampfireStartEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockCampfireStartEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onBrewingStart(BrewingStartEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockBrewingStartEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onBlock(AnvilDamagedEvent event) {
        Location location = event.getInventory().getLocation();
        if (location != null) {
            Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(location.getWorld());
            if (planet != null) new BlockAnvilDamagedEvent(planet, event, location.getBlock()).callEvent();
        }
    }

    @EventHandler
    public void onBlock(TargetHitEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getEntity().getWorld());
        if (planet != null) new BlockTargetHitEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onBlock(NotePlayEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockNotePlayedEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onBlock(SculkBloomEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockSculkBloomedEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onBlock(BellRingEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockBellRungEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onBlock(CrafterCraftEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockCrafterCraftedEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onBlock(LeavesDecayEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockLeavesDecayedEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onBlock(SpongeAbsorbEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getBlock().getWorld());
        if (planet != null) new BlockSpongeAbsorbedEvent(planet, event).callEvent();
    }

    @EventHandler
    public void onLightningStrike(LightningStrikeEvent event) {
        Planet planet = OpenCreative.getPlanetsManager().getPlanetByWorld(event.getWorld());
        if (planet != null)
            new ua.mcchickenstudio.opencreative.coding.blocks.events.world.other.LightningStrikeEvent(planet, event).callEvent();
    }


}
