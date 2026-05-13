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

package ua.mcchickenstudio.opencreative.coding.blocks.executors;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.DisplayableIcon;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.fighting.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.interaction.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.movement.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.state.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Function;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Method;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.other.Structure;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.fighting.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.interaction.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.inventory.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.movement.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.player.world.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.world.blocks.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.world.inventory.*;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.world.other.*;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendDebug;

/**
 * <h1>Executors</h1>
 * This class represents registry of executors,
 * that can register new executors for coding.
 * <p>
 * To get instance use {@link #getInstance()}.
 * <p>
 * To add custom executor create a class, that
 * extends one of prepared: {@link ua.mcchickenstudio.opencreative.coding.blocks.executors.player.PlayerExecutor PlayerExecutor},
 * {@link ua.mcchickenstudio.opencreative.coding.blocks.executors.world.WorldExecutor WorldExecutor},
 * {@link ua.mcchickenstudio.opencreative.coding.blocks.executors.entity.EntityExecutor EntityExecutor}, and register it with
 * {@link #registerExecutor(Executor)} method.
 */
public final class Executors {

    private static Executors instance;
    private final List<Executor> executors = new LinkedList<>();

    /**
     * Returns instance of executors controller class.
     *
     * @return instance of executors.
     */
    public synchronized static @NotNull Executors getInstance() {
        if (instance == null) {
            instance = new Executors();
            instance.registerDefaults();
        }
        return instance;
    }

    /**
     * Registers executor, that will be replaced in coding.
     *
     * @param executor executor to register.
     */
    public void registerExecutor(@NotNull Executor executor) {
        Executor existing = getById(executor.getID());
        if (existing != null) {
            sendDebug("[EXECUTORS] Can't register executor " + executor.getName() + " (from " + executor.getExtensionId() + "), "
                    + "because there's already registered executor " + existing.getName() + " (from " + existing.getExtensionId() + ") "
                    + "with same ID: " + executor.getID());
            return;
        }
        sendDebug("[EXECUTORS] Registered executor: " + executor.getName() + " (from " + executor.getExtensionId() + ")");
        executors.add(executor);
    }

    /**
     * Registers executors, that will be replaced in coding.
     *
     * @param executors executors to register.
     */
    public void registerExecutor(@NotNull Executor... executors) {
        for (Executor executor : executors) {
            registerExecutor(executor);
        }
    }

    /**
     * Unregisters executor if list contains it.
     *
     * @param executor executor to unregister.
     */
    @SuppressWarnings("unused")
    public void unregisterExecutor(@NotNull Executor executor) {
        executors.remove(executor);
    }

    /**
     * Returns a copy of list that contains all registered executors.
     *
     * @return executors list.
     */
    public @NotNull List<Executor> getExecutors() {
        return new ArrayList<>(executors);
    }

    private void registerDefaults() {
        // Player Events
        registerExecutor(new PlayerJoinExecutor(), new PlayerQuitExecutor(), new PlayerLikedExecutor(),
                new PlayerAdvertisedExecutor(), new PlayerPlayExecutor(), new PlayerChatExecutor(),
                new PlayerPurchaseExecutor(), new PlayerChunkLoadExecutor(), new PlayerChunkUnloadExecutor());
        registerExecutor(new PlayerLeftClickExecutor(), new PlayerRightClickExecutor(), new PlayerInteractExecutor(),
                new PlayerPlaceBlockExecutor(), new PlayerDestroyBlockExecutor(), new PlayerDestroyingBlockExecutor(),
                new PlayerBlockInteractExecutor(), new PlayerMobInteractExecutor(), new PlayerBedEnterExecutor(),
                new PlayerBedLeaveExecutor(), new PlayerFishingExecutor(), new PlayerSpectatingExecutor(),
                new PlayerStopSpectatingExecutor(), new PlayerChangedSignExecutor(), new PlayerBucketFillExecutor(),
                new PlayerBucketEmptyExecutor(), new PlayerBucketEntityExecutor(), new PlayerChangedArmorExecutor());
        registerExecutor(new PlayerGetDamagedExecutor(), new MobDamagePlayerExecutor(), new PlayerDamageMobExecutor(),
                new PlayerDamagePlayerExecutor(), new PlayerHungerChangeExecutor(), new PlayerKilledPlayerExecutor(),
                new PlayerKilledMobExecutor(), new PlayerDeathExecutor(), new PlayerRespawnExecutor(),
                new PlayerTotemRespawnExecutor());
        registerExecutor(new PlayerClickInventoryExecutor(), new PlayerDropItemExecutor(), new PlayerPickupItemExecutor(),
                new PlayerSwapHandExecutor(), new PlayerOpenInventoryExecutor(), new PlayerWriteBookExecutor(),
                new PlayerChangeSlotExecutor(), new PlayerItemConsumeExecutor(), new PlayerItemCraftExecutor(),
                new PlayerItemDamageExecutor(), new PlayerItemBreakExecutor(), new PlayerDragItemExecutor(),
                new PlayerCloseInventoryExecutor());
        registerExecutor(new PlayerWalkExecutor(), new PlayerJumpExecutor(), new PlayerRunningExecutor(),
                new PlayerStopRunningExecutor(), new PlayerFlyingExecutor(), new PlayerStopFlyingExecutor(),
                new PlayerSneakingExecutor(), new PlayerStopSneakingExecutor(), new PlayerTeleportExecutor(),
                new PlayerEnteredVehicleExecutor(), new PlayerVehicleExitExecutor());
        // World Events
        registerExecutor(new WorldPlayModeExecutor(), new WorldVariableTransferExecutor(), new WorldWebResponseExecutor(),
                new WorldLightningStrikeExecutor(), new WorldReachedRedstoneLimitExecutor(), new WorldReachedBlocksLimitExecutor(),
                new WorldReachedEntitiesLimitExecutor(), new WorldReachedVariablesLimitExecutor());
        registerExecutor(new WorldBlockCookedExecutor(), new WorldBlockFurnaceBurnedExecutor(), new WorldBlockDispensedExecutor(),
                new WorldBrewingFuelExecutor(), new WorldCrafterCraftedExecutor());
        registerExecutor(new WorldBlockBurnedExecutor(), new WorldBlockExplodedExecutor(), new WorldBlockTntPrimeExecutor(),
                new WorldBlockExperienceDropExecutor(), new WorldBlockFadedExecutor(), new WorldBlockFormedExecutor(),
                new WorldBlockGrownExecutor(), new WorldBlockIgnitedExecutor(), new WorldBlockPhysicsExecutor(),
                new WorldBlockRedstoneExecutor(), new WorldPortalCreatedExecutor(), new WorldBlockPistonExtendedExecutor(),
                new WorldBlockPistonRetractedExecutor(), new WorldBlockBrewingStartExecutor(), new WorldBlockBrewingEndExecutor(),
                new WorldBlockCampfireStartExecutor(), new WorldBlockCauldronChangeExecutor(), new WorldBlockFluidChangedExecutor(),
                new WorldBlockLeavesDecayedExecutor(), new WorldBlockNotePlayedExecutor(), new WorldBlockSculkBloomedExecutor(),
                new WorldBlockBeaconActivatedExecutor(), new WorldBlockBeaconDeactivatedExecutor(), new WorldBlockAnvilDamagedExecutor(),
                new WorldBlockTargetHitExecutor(), new WorldSpongeAbsorbedExecutor(), new WorldBlockBellRungExecutor());
        // Entity Events
        registerExecutor(new EntityPigZombieAngeredExecutor(), new EntityBatToggledSleepModeExecutor(), new EntitySlimeSplitExecutor(),
                new EntityShulkerDuplicatedExecutor(), new EntityWitchReadyPotionExecutor(), new EntitySheepRegrownWoolExecutor(),
                new EntityPufferfishStateChangedExecutor(), new EntityCreeperIgnitedExecutor(), new EntityCreeperPoweredExecutor(),
                new EntityEnteredLoveModeExecutor(), new EntityTurtleGoesHomeExecutor(), new EntityResurrectedExecutor(),
                new EntityPotionEffectedExecutor(), new EntityWardenAngerChangedExecutor(), new EntityAirChangedExecutor());
        registerExecutor(new EntityProjectileHitExecutor(), new EntityEnteredBlockExecutor(), new EntityMountedExecutor(),
                new EntityDismountedExecutor(), new EntityEnteredVehicleExecutor(), new EntityVehicleExitExecutor(),
                new EntityJumpedExecutor(), new EntityHorseJumpedExecutor(), new EntityEndermanEscapedExecutor());
        registerExecutor(new EntitySpawnedExecutor(), new EntityRemovedExecutor(), new EntityBornExecutor(),
                new EntityDroppedItemExecutor(), new EntityPickedUpItemExecutor(), new EntityItemMergedExecutor(),
                new EntityItemDespawnedExecutor(), new EntityExplodedExecutor(), new EntityDamagedItemExecutor(),
                new EntityPiglinBarteredExecutor(), new EntityInteractedBlockExecutor(), new EntityTurtleLaysEggExecutor(),
                new EntityFireworkExplodedExecutor());
        registerExecutor(new EntityGetDamagedExecutor(), new EntityDiedExecutor(), new EntityShotBowExecutor(),
                new EntityWitchThrownPotionExecutor(), new EntityWitchConsumedPotionExecutor(),
                new EntityLoadedCrossbowExecutor(), new EntityCombustedByEntityExecutor(), new EntityCombustedByBlockExecutor(),
                new EntityRegainedHealthExecutor(), new EntityHangingBreakExecutor());
        // Other
        registerExecutor(new Function(), new Method(), new Structure());
    }

    /**
     * Returns list of executors, that have same menu category.
     *
     * @param executorCategory executor category.
     * @param menusCategory menu category.
     * @return list of executors with specified menu category.
     */
    public @NotNull List<Executor> getByCategories(@NotNull ExecutorCategory executorCategory, @NotNull MenusCategory menusCategory) {
        List<Executor> list = new LinkedList<>();
        for (Executor executor : executors) {
            if (executor.getBlockCategory() == executorCategory) {
                if (executor instanceof DisplayableIcon icon) {
                    if (icon.getCategory() == menusCategory) {
                        list.add(executor);
                    }
                }
            }
        }
        return list;
    }

    /**
     * Returns list of all menu categories of specified executor category..
     *
     * @param executorCategory executor category.
     * @return list of menu categories.
     */
    public @NotNull List<MenusCategory> getCategories(@NotNull ExecutorCategory executorCategory) {
        List<MenusCategory> list = new LinkedList<>();
        for (Executor executor : executors) {
            if (executor.getBlockCategory() == executorCategory && executor instanceof DisplayableIcon icon) {
                if (list.contains(icon.getCategory())) continue;
                list.add(icon.getCategory());
            }
        }
        return list;
    }

    /**
     * Checks if executor with specified ID exists in registry.
     *
     * @param id id of executor.
     * @return true - exists, false - not exists.
     */
    public boolean exists(@NotNull String id) {
        return getById(id) != null;
    }

    /**
     * Checks if executor with specified class exists in registry.
     *
     * @param clazz class of executor.
     * @return true - exists, false - not exists.
     */
    public boolean exists(@NotNull Class<? extends Executor> clazz) {
        return getByClass(clazz) != null;
    }

    /**
     * Returns executor from registry by specified class
     * if it exists, otherwise will return null.
     *
     * @param clazz class to get executor.
     * @return executor - if exists, or null - not exists.
     */
    public @Nullable Executor getByClass(@NotNull Class<? extends Executor> clazz) {
        for (Executor eventValue : executors) {
            if (eventValue.getClass().equals(clazz)) {
                return eventValue;
            }
        }
        return null;
    }

    /**
     * Returns executor from registry by specified id
     * if it exists, otherwise will return null.
     *
     * @param id id to get executor.
     * @return executor - if exists, or null - not exists.
     */
    public @Nullable Executor getById(@NotNull String id) {
        for (Executor eventValue : executors) {
            if (eventValue.getID().equals(id)) {
                return eventValue;
            }
        }
        return null;
    }

    /**
     * Returns executor from registry by specified block
     * if it exists, otherwise will return null.
     *
     * @param block block to get executor.
     * @return executor - if exists, or null - not exists.
     */
    public @Nullable Executor getByBlock(@NotNull Block block) {
        if (block.getType() == Material.LAPIS_BLOCK) {
            return new Function();
        } else if (block.getType() == Material.EMERALD_BLOCK) {
            return new Method();
        } else if (block.getType() == Material.OXIDIZED_COPPER) {
            return new Structure();
        }
        Block signBlock = block.getRelative(BlockFace.SOUTH);
        if (signBlock.getType().toString().contains("WALL_SIGN")) {
            Sign sign = (Sign) signBlock.getState();
            if (sign.getSide(Side.FRONT).lines().size() >= 3) {
                Component signText = sign.getSide(Side.FRONT).line(2);
                String text = ((TextComponent) signText).content().toLowerCase();
                return getById(text);
            }
        }
        return null;
    }



}
