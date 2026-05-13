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

package ua.mcchickenstudio.opencreative.coding.blocks.actions;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.CodingBlockType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.controlactions.events.CancelEventAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.controlactions.events.UncancelEventAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.controlactions.lines.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.controlleractions.other.CatchErrorAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.controlleractions.other.MeasureTimeAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.inventory.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.movement.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.other.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.params.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.entityactions.state.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.other.LaunchFunctionAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.other.LaunchMethodAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.appearance.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.communication.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.inventory.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.movement.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.params.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.state.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.repeatactions.other.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.selectionactions.SelectionAddTargetAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.selectionactions.SelectionRemoveTargetAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.selectionactions.SelectionSetTargetAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.structureactions.manipulations.GetStructureValueAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.structureactions.structureonly.SetFieldAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.structureactions.manipulations.SetStructureValueAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.item.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.list.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.location.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.map.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.number.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.other.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.text.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.vector.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.appearance.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.blocks.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.entity.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.world.*;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.world.phys.AddPhysObjectAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.world.phys.MotionParamPhysObjectAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.world.phys.SettingsParamPhysObjectAction;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.world.phys.VisualParamPhysObjectAction;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.entityconditions.other.*;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.entityconditions.params.*;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.appearance.*;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.blocks.*;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.inventory.*;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.params.HasPotionEffectsCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.params.MessageEqualsCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.params.PlayerNameEqualsCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.playerconditions.state.*;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.item.VarItemHasCustomData;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.item.VarItemHasCustomKey;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.item.VarItemHasEnchantments;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.list.*;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.location.LocationInAreaCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.number.NumberGreaterCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.number.NumberInRangeCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.number.NumberLessCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.other.*;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.text.TextContainsCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.text.TextEndsWithCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.text.TextEqualsCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.variableconditions.text.TextStartsWithCondition;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.worldconditions.blocks.*;
import ua.mcchickenstudio.opencreative.coding.blocks.conditions.worldconditions.world.*;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.ArgumentSlot;
import ua.mcchickenstudio.opencreative.coding.menus.layouts.ParameterSlot;
import ua.mcchickenstudio.opencreative.coding.variables.ValueType;
import ua.mcchickenstudio.opencreative.utils.hooks.HookUtils;

import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.BlockUtils.getSignLine;
import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.messageExists;


public enum ActionType implements CodingBlockType {

    /**
     * <h1>Player Actions.</h1>
     * Actions with players.
     * <p>Categories: Communication, Inventory, Movement, Params, State, Appearance</p>
     */

    // Communication
    PLAYER_SEND_MESSAGE(ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, SendMessageAction.class, Material.WRITABLE_BOOK, new ArgumentSlot("messages", ValueType.TEXT, (byte) 18), new ParameterSlot("type", Arrays.asList("new-line", "join-spaces", "join"), Material.PAPER, Material.MAP, Material.FILLED_MAP)),
    PLAYER_SEND_DIALOG(ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, SendDialogAction.class, Material.GLOBE_BANNER_PATTERN, new ArgumentSlot("messages", ValueType.TEXT, (byte) 18), new ArgumentSlot("cooldown", ValueType.NUMBER)),
    PLAYER_CLEAR_CHAT(ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ClearChatAction.class, Material.BUCKET),
    PLAYER_SHOW_TITLE(ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ShowTitleAction.class, Material.OAK_SIGN, new ArgumentSlot("title", ValueType.TEXT), new ArgumentSlot("subtitle", ValueType.TEXT), new ArgumentSlot("fade-in", ValueType.NUMBER), new ArgumentSlot("stay", ValueType.NUMBER), new ArgumentSlot("fade-out", ValueType.NUMBER)),
    PLAYER_SHOW_ACTIONBAR(ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ShowActionbarAction.class, Material.REDSTONE_TORCH, new ArgumentSlot("actionbar", ValueType.TEXT, (byte) 18), new ParameterSlot("type", Arrays.asList("join", "join-spaces"), Material.FILLED_MAP, Material.MAP)),
    PLAYER_SHOW_ADVANCEMENT(ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ShowAdvancementAction.class, Material.EMERALD, new ArgumentSlot("icon", ValueType.ITEM), new ParameterSlot("style", Arrays.asList("goal", "task", "challenge"), Material.EMERALD, Material.DIAMOND, Material.BEACON), new ArgumentSlot("title", ValueType.TEXT), new ArgumentSlot("message", ValueType.TEXT)),
    PLAYER_PLAY_SOUND(ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, PlaySoundAction.class, Material.MUSIC_DISC_OTHERSIDE, new ArgumentSlot("sound", ValueType.TEXT), new ArgumentSlot("volume", ValueType.NUMBER), new ArgumentSlot("pitch", ValueType.NUMBER), new ArgumentSlot("location", ValueType.LOCATION), new ParameterSlot("category", Arrays.asList("ambient", "blocks", "hostile", "master", "music", "neutral", "players", "records", "voice", "weather"), Material.CYAN_STAINED_GLASS, Material.GRASS_BLOCK, Material.ZOMBIE_HEAD, Material.GOLDEN_PICKAXE, Material.NOTE_BLOCK, Material.PIGLIN_HEAD, Material.PLAYER_HEAD, Material.MUSIC_DISC_CAT, Material.NAUTILUS_SHELL, Material.WATER_BUCKET), new ArgumentSlot("seed", ValueType.NUMBER)),
    PLAYER_STOP_SOUNDS(ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, StopSoundsAction.class, Material.MUSIC_DISC_11, new ArgumentSlot("sounds", ValueType.TEXT, (byte) 18)),
    PLAYER_SHOW_WIN_SCREEN(ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ShowWinScreenAction.class, Material.DRAGON_EGG),
    PLAYER_SHOW_DEMO_SCREEN(ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, ShowDemoScreenAction.class, Material.FARMLAND),
    PLAYER_REQUEST_PURCHASE(ActionCategory.PLAYER_ACTION, MenusCategory.COMMUNICATION, RequestPurchaseAction.class, Material.GOLD_INGOT, "Vault", new ArgumentSlot("id", ValueType.TEXT), new ArgumentSlot("name", ValueType.TEXT), new ParameterSlot("save"), new ArgumentSlot("price", ValueType.NUMBER)),

    // Inventory
    PLAYER_GIVE_ITEMS(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, GiveItemsAction.class, Material.CHEST_MINECART, new ArgumentSlot("items", ValueType.ITEM, (byte) 27)),
    PLAYER_SAVE_INVENTORY(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SaveInventoryAction.class, Material.HOPPER),
    PLAYER_RESTORE_INVENTORY(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, RestoreInventoryAction.class, Material.DROPPER),
    PLAYER_SET_ITEM_IN_HAND(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetItemInHandAction.class, Material.NETHERITE_SWORD, new ArgumentSlot("main", ValueType.ITEM), new ParameterSlot("replace-with-air"), new ArgumentSlot("off", ValueType.ITEM)),
    PLAYER_SET_ITEM_IN_SLOT(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetItemInSlotAction.class, Material.SLIME_BLOCK, new ArgumentSlot("slots", ValueType.NUMBER, (byte) 18), new ArgumentSlot("item", ValueType.ITEM), new ParameterSlot("replace-with-air")),
    PLAYER_SET_CURSOR_ITEM(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetCursorItemAction.class, Material.TRIPWIRE_HOOK, new ArgumentSlot("item", ValueType.ITEM)),
    PLAYER_SET_ITEMS(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetItemsAction.class, Material.DARK_OAK_CHEST_BOAT, new ArgumentSlot("items", ValueType.ITEM, (byte) 27, true)),
    PLAYER_SET_ARMOR(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetArmorAction.class, Material.NETHERITE_CHESTPLATE, new ArgumentSlot("helmet", ValueType.ITEM), new ArgumentSlot("chestplate", ValueType.ITEM), new ArgumentSlot("leggings", ValueType.ITEM), new ArgumentSlot("boots", ValueType.ITEM), new ParameterSlot("replace-with-air")),
    PLAYER_GIVE_RANDOM_ITEM(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, GiveRandomItemAction.class, Material.LIME_SHULKER_BOX, new ArgumentSlot("items", ValueType.ITEM, (byte) 27)),
    PLAYER_CLEAR_INVENTORY(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, ClearInventoryAction.class, Material.BUCKET),
    PLAYER_CLOSE_INVENTORY(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, CloseInventoryAction.class, Material.STRUCTURE_VOID),
    PLAYER_REMOVE_ITEMS(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, RemoveItemsAction.class, Material.COBWEB, new ArgumentSlot("items", ValueType.ITEM, (byte) 27)),
    PLAYER_SET_SLOT(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetSlotAction.class, Material.SLIME_BALL, new ArgumentSlot("slot", ValueType.NUMBER)),
    PLAYER_SET_HOTBAR(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetHotBarAction.class, Material.BIRCH_CHEST_BOAT, new ArgumentSlot("items", ValueType.ITEM, (byte) 9, true), new ParameterSlot("replace-with-air")),
    PLAYER_SET_ITEM_COOLDOWN(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetItemCooldownAction.class, Material.CLOCK, new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("cooldown", ValueType.NUMBER)),
    PLAYER_DISCOVER_RECIPE(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, DiscoverRecipeAction.class, Material.KNOWLEDGE_BOOK, new ArgumentSlot("recipes", ValueType.TEXT, (byte) 18)),
    PLAYER_UNDISCOVER_RECIPE(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, UndiscoverRecipeAction.class, Material.KNOWLEDGE_BOOK, new ArgumentSlot("recipes", ValueType.TEXT, (byte) 18)),

    PLAYER_APPLY_MENDING(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, ApplyMendingAction.class, Material.ENCHANTED_BOOK, new ArgumentSlot("amount", ValueType.NUMBER)),
    //PLAYER_DAMAGE_ITEM(                 ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, null, Material.NETHERITE_SCRAP,  new ArgumentSlot("item",VariableType.ITEM),new ArgumentSlot("damage",VariableType.NUMBER)),
    PLAYER_OPEN_SIGN(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, OpenSignAction.class, Material.OAK_SIGN, new ArgumentSlot("location", ValueType.LOCATION), new ParameterSlot("side", Arrays.asList("front", "back"), Material.OAK_SIGN, Material.WARPED_SIGN)),
    PLAYER_OPEN_CONTAINER(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, OpenContainerAction.class, Material.BARREL, new ArgumentSlot("location", ValueType.LOCATION), new ParameterSlot("save")),
    PLAYER_OPEN_BOOK(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, OpenBookAction.class, Material.BOOK, new ArgumentSlot("book", ValueType.ITEM)),
    PLAYER_GET_ITEM_BY_SLOT(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, GetItemAction.class, Material.MAGMA_CREAM, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("slot", ValueType.NUMBER)),
    PLAYER_OPEN_INVENTORY_VIEW(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, OpenInventoryAction.class, Material.CHEST, new ParameterSlot("type", Arrays.asList("chest", "dispenser", "dropper", "furnace", "workbench", "enchanting", "brewing", "player", "ender_chest", "anvil", "smithing", "beacon", "hopper", "shulker_box", "barrel", "blast_furnace", "lectern", "smoker", "loom", "cartography", "grindstone", "stonecutter", "crafter"), Material.CHEST, Material.DISPENSER, Material.DROPPER, Material.FURNACE, Material.CRAFTING_TABLE, Material.ENCHANTING_TABLE, Material.BREWING_STAND, Material.PLAYER_HEAD, Material.ENDER_CHEST, Material.ANVIL, Material.SMITHING_TABLE, Material.BEACON, Material.HOPPER, Material.SHULKER_BOX, Material.BARREL, Material.BLAST_FURNACE, Material.LECTERN, Material.SMOKER, Material.LOOM, Material.CARTOGRAPHY_TABLE, Material.GRINDSTONE, Material.STONECUTTER, Material.CRAFTER), new ArgumentSlot("title", ValueType.TEXT)),
    PLAYER_SET_INVENTORY_VIEW_ITEM(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetMenuItemAction.class, Material.PAINTING, new ArgumentSlot("slots", ValueType.NUMBER, (byte) 18), new ArgumentSlot("item", ValueType.ITEM)),
    PLAYER_SET_INVENTORY_VIEW_ROW_ITEMS(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetMenuItemsRowAction.class, Material.DARK_OAK_CHEST_BOAT, new ArgumentSlot("items", ValueType.ITEM, (byte) 9, true), new ArgumentSlot("row", ValueType.NUMBER), new ParameterSlot("replace-with-air")),
    PLAYER_SET_INVENTORY_VIEW_ROWS(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetMenuSizeAction.class, Material.ITEM_FRAME, new ArgumentSlot("rows", ValueType.NUMBER)),
    PLAYER_SET_INVENTORY_VIEW_TITLE(ActionCategory.PLAYER_ACTION, MenusCategory.INVENTORY, SetMenuTitleAction.class, Material.GLOW_ITEM_FRAME, new ArgumentSlot("title", ValueType.TEXT)),

    // Movement
    PLAYER_TELEPORT(ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT, TeleportPlayerAction.class, Material.ENDER_PEARL, new ArgumentSlot("location", ValueType.LOCATION), new ParameterSlot("consider", Arrays.asList("all", "only-coordinates", "only-rotation"), Material.ENDER_EYE, Material.PAPER, Material.PLAYER_HEAD)),
    //PLAYER_RANDOM_TELEPORT(             ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT,      null, Material.CHORUS_FRUIT,         new ArgumentSlot("locations",VariableType.LOCATION,(byte)18)),
    //PLAYER_TELEPORT_QUEUE(              ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT,      null, Material.ENDER_EYE,          new ArgumentSlot("locations",VariableType.LOCATION,(byte)18),new ArgumentSlot("cooldown",VariableType.NUMBER)),
    PLAYER_KICK(ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT, KickPlayerAction.class, Material.BARRIER),
    PLAYER_LAUNCH_VERTICAL(ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT, LaunchVerticalAction.class, Material.PRISMARINE_SHARD, new ArgumentSlot("power", ValueType.NUMBER)),
    PLAYER_LAUNCH_HORIZONTAL(ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT, LaunchHorizontalAction.class, Material.FEATHER, new ArgumentSlot("power", ValueType.NUMBER)),
    PLAYER_LAUNCH_TO_LOCATION(ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT, LaunchToLocationAction.class, Material.MAP, new ArgumentSlot("location", ValueType.LOCATION), new ArgumentSlot("power", ValueType.NUMBER)),
    PLAYER_SADDLE_ENTITY(ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT, SaddleEntityAction.class, Material.SADDLE, new ArgumentSlot("entity", ValueType.TEXT)),
    PLAYER_FIREWORK_BOOST(ActionCategory.PLAYER_ACTION, MenusCategory.MOVEMENT, FireworkBoostAction.class, Material.FIREWORK_ROCKET, new ArgumentSlot("firework", ValueType.ITEM)),
    PLAYER_SET_SPECTATOR_TARGET(
            ActionCategory.PLAYER_ACTION,
            MenusCategory.MOVEMENT,
            SetSpectatorTargetAction.class,
            Material.SKELETON_SKULL,
            new ArgumentSlot("entity", ValueType.TEXT)
    ),

    // Params
    PLAYER_ADD_DAMAGE(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, DamagePlayerAction.class, Material.NETHERITE_SWORD, new ArgumentSlot("damage", ValueType.NUMBER)),
    PLAYER_SET_HEALTH(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetHealthAction.class, Material.APPLE, new ArgumentSlot("health", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_HUNGER(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetHungerAction.class, Material.COOKED_CHICKEN, new ArgumentSlot("hunger", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_WALK_SPEED(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetWalkSpeedAction.class, Material.CHAINMAIL_BOOTS, new ArgumentSlot("speed", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_FLY_SPEED(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetFlySpeedAction.class, Material.FEATHER, new ArgumentSlot("speed", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_MAX_HEALTH(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetMaxHealthAction.class, Material.GOLDEN_APPLE, new ArgumentSlot("health", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_FIRE_TICKS(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetFireTicksAction.class, Material.CAMPFIRE, new ArgumentSlot("ticks", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_FREEZE_TICKS(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetFreezeTicksAction.class, Material.ICE, new ArgumentSlot("ticks", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_NO_DAMAGE_TICKS(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetNoDamageTicksAction.class, Material.TOTEM_OF_UNDYING, new ArgumentSlot("ticks", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_EXP(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetExpAction.class, Material.SLIME_SPAWN_EGG, new ArgumentSlot("exp", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_EXP_LEVEL(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetExpLevelAction.class, Material.SUGAR_CANE, new ArgumentSlot("level", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_TOTAL_EXPERIENCE(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetTotalExpAction.class, Material.EXPERIENCE_BOTTLE, new ArgumentSlot("exp", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_GIVE_POTION_EFFECTS(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, GivePotionEffectsAction.class, Material.POTION, new ArgumentSlot("potions", ValueType.POTION, (byte) 18), new ParameterSlot("replace")),
    PLAYER_CLEAR_POTION_EFFECTS(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, ClearPotionEffectsAction.class, Material.MILK_BUCKET),
    PLAYER_REMOVE_POTION_EFFECTS(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, RemovePotionEffectsAction.class, Material.GLASS_BOTTLE, new ArgumentSlot("potions", ValueType.POTION, (byte) 27)),
    PLAYER_SET_REMAINING_AIR(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, PlayerSetRemainingAirAction.class, Material.SPONGE, new ArgumentSlot("ticks", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_FLYING_FALL_DAMAGE(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetFlyingFallDamageAction.class, Material.RABBIT_HIDE, new ParameterSlot("boolean")),
    PLAYER_SET_FALL_DISTANCE(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetFallDistanceAction.class, Material.RABBIT_FOOT, new ArgumentSlot("distance", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_LAST_DAMAGE(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetLastDamageAction.class, Material.REDSTONE, new ArgumentSlot("damage", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_CAN_PICKUP_ITEM(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetCanPickupItem.class, Material.GLOWSTONE_DUST, new ParameterSlot("boolean")),
    PLAYER_SET_SHIELD_BLOCKING_DELAY(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetShieldBlockingDelay.class, Material.SHIELD, new ArgumentSlot("delay", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_BEE_STINGER_COOLDOWN(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetBeeStingerCooldownAction.class, Material.BEE_NEST, new ArgumentSlot("cooldown", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_MAXIMUM_NO_DAMAGE_TICKS(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, SetMaxNoDamageTicksAction.class, Material.ENCHANTED_GOLDEN_APPLE, new ArgumentSlot("ticks", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_MAXIMUM_AIR(ActionCategory.PLAYER_ACTION, MenusCategory.PARAMS, PlayerSetMaximumAirAction.class, Material.POTION, new ArgumentSlot("ticks", ValueType.NUMBER), new ParameterSlot("add")),

    // State
    PLAYER_SET_GAMEMODE(ActionCategory.PLAYER_ACTION, MenusCategory.STATE, SetGameModeAction.class, Material.CRAFTING_TABLE, new ParameterSlot("game-mode", Arrays.asList("adventure", "survival", "creative", "spectator"), Material.PUFFERFISH_BUCKET, Material.CRAFTING_TABLE, Material.TNT, Material.FEATHER)),
    PLAYER_SET_FLYING(ActionCategory.PLAYER_ACTION, MenusCategory.STATE, SetFlyingAction.class, Material.FEATHER, new ParameterSlot("flying", Material.CHAINMAIL_BOOTS, Material.FEATHER), new ParameterSlot("allow-flight", Material.GRASS_BLOCK, Material.GLASS)),
    PLAYER_SET_GLOWING(ActionCategory.PLAYER_ACTION, MenusCategory.STATE, SetGlowingAction.class, Material.DRAGON_BREATH, new ParameterSlot("glowing", Arrays.asList(false, true), Material.WHITE_STAINED_GLASS, Material.GLASS)),
    PLAYER_SET_GLIDING(ActionCategory.PLAYER_ACTION, MenusCategory.STATE, SetGlidingAction.class, Material.ELYTRA, new ParameterSlot("boolean", Material.CHAINMAIL_BOOTS, Material.ELYTRA)),
    PLAYER_SET_SPRINTING(ActionCategory.PLAYER_ACTION, MenusCategory.STATE, SetSprintingAction.class, Material.GOLDEN_BOOTS, new ParameterSlot("boolean", Material.LEATHER_BOOTS, Material.NETHERITE_BOOTS)),
    PLAYER_SET_SNEAKING(ActionCategory.PLAYER_ACTION, MenusCategory.STATE, SetSneakingAction.class, Material.RABBIT_FOOT, new ParameterSlot("boolean", Material.RABBIT_FOOT, Material.RABBIT_STEW)),
    PLAYER_RELEASE_SHOULDERS(ActionCategory.PLAYER_ACTION, MenusCategory.STATE, PlayerReleaseShouldersAction.class, Material.PARROT_SPAWN_EGG, new ParameterSlot("type", List.of("all", "left", "right"), Material.PARROT_SPAWN_EGG, Material.SHIELD, Material.NETHERITE_SWORD)),

    // Appearance
    PLAYER_SET_RESOURCE_PACK(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, SetResourcePackAction.class, Material.SHROOMLIGHT, new ArgumentSlot("url", ValueType.TEXT), new ArgumentSlot("prompt", ValueType.TEXT)),
    PLAYER_SET_WORLD_BORDER(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, PlayerSetWorldBorderAction.class, Material.END_CRYSTAL, new ArgumentSlot("center", ValueType.LOCATION), new ArgumentSlot("radius", ValueType.NUMBER), new ArgumentSlot("time", ValueType.NUMBER), new ArgumentSlot("warning-distance", ValueType.NUMBER), new ArgumentSlot("warning-time", ValueType.NUMBER), new ArgumentSlot("safe-distance", ValueType.NUMBER)),
    PLAYER_SET_TIME(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, PlayerSetTimeAction.class, Material.CLOCK, new ArgumentSlot("time", ValueType.NUMBER)),
    PLAYER_SET_WEATHER(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, PlayerSetWeatherAction.class, Material.WATER_BUCKET, new ParameterSlot("weather", Arrays.asList("clean", "storm"), Material.SUNFLOWER, Material.WATER_BUCKET)),
    PLAYER_SET_VISUAL_FIRE(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, SetVisualFireAction.class, Material.CAMPFIRE, new ParameterSlot("boolean")),
    PLAYER_SHOW_SCOREBOARD(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, ShowScoreboardAction.class, Material.PAINTING, new ArgumentSlot("scoreboard", ValueType.TEXT)),
    PLAYER_HIDE_SCOREBOARD(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, HideScoreboardAction.class, Material.ITEM_FRAME),
    PLAYER_SHOW_BOSS_BAR(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, ShowBossBarAction.class, Material.DRAGON_BREATH, new ArgumentSlot("bossbar", ValueType.TEXT)),
    PLAYER_HIDE_BOSS_BAR(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, HideBossBarAction.class, Material.DRAGON_HEAD, new ArgumentSlot("bossbar", ValueType.TEXT)),
    PLAYER_SHOW_PARTICLE(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, ShowParticleAction.class, Material.NETHER_STAR, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("particle", ValueType.PARTICLE), new ArgumentSlot("count", ValueType.NUMBER), new ArgumentSlot("offset-x", ValueType.NUMBER), new ArgumentSlot("offset-y", ValueType.NUMBER), new ArgumentSlot("offset-z", ValueType.NUMBER)),

    PLAYER_SET_ARROWS_IN_BODY(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, SetArrowsInBodyAction.class, Material.ARROW, new ArgumentSlot("count", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_BEE_STINGERS_IN_BODY(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, SetBeeStingersInBodyAction.class, Material.BEE_SPAWN_EGG, new ArgumentSlot("count", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_VIEW_DISTANCE(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, SetViewDistanceAction.class, Material.SPYGLASS, new ArgumentSlot("distance", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SET_SIMULATION_DISTANCE(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, SetSimulationDistanceAction.class, Material.WIND_CHARGE, new ArgumentSlot("distance", ValueType.NUMBER), new ParameterSlot("add")),
    PLAYER_SWING_HAND(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, SwingHandAction.class, Material.LEVER, new ParameterSlot("hand", List.of("main", "off"), Material.NETHERITE_SWORD, Material.SHIELD)),
    PLAYER_SET_COMPASS_TARGET(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, SetCompassTarget.class, Material.COMPASS, new ArgumentSlot("location", ValueType.LOCATION)),
    PLAYER_SHOW_ELDER_GUARDIAN(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, ShowElderGuardianAction.class, Material.ELDER_GUARDIAN_SPAWN_EGG, new ParameterSlot("silent", Material.STRUCTURE_VOID, Material.NAUTILUS_SHELL)),
    //PLAYER_SPAWN_PARTICLE(              ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, null, Material.NETHER_STAR, new ArgumentSlot("particle",VariableType.PARTICLE,(byte) 18),new ArgumentSlot("location",VariableType.LOCATION),new ArgumentSlot("count",VariableType.NUMBER)),
    PLAYER_SEND_SIGN_CHANGE(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, SendSignChangeAction.class, Material.OAK_SIGN, new ArgumentSlot("location", ValueType.LOCATION), new ArgumentSlot("text", ValueType.TEXT), new ArgumentSlot("number", ValueType.NUMBER)),
    PLAYER_SHOW_PHANTOM_BLOCK(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, ShowPhantomBlockAction.class, Material.GLASS, new ArgumentSlot("location", ValueType.LOCATION), new ArgumentSlot("block", ValueType.ITEM)),
    PLAYER_SHOW_ENTITY(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, ShowEntityAction.class, Material.PIGLIN_HEAD, new ArgumentSlot("entity", ValueType.TEXT)),
    PLAYER_HIDE_ENTITY(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, HideEntityAction.class, Material.SKELETON_SKULL, new ArgumentSlot("entity", ValueType.TEXT)),
    PLAYER_SHOW_PLAYER(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, ShowPlayerAction.class, Material.PLAYER_HEAD, new ArgumentSlot("player", ValueType.TEXT)),
    PLAYER_HIDE_PLAYER(ActionCategory.PLAYER_ACTION, MenusCategory.APPEARANCE, HidePlayerAction.class, Material.WITHER_SKELETON_SKULL, new ArgumentSlot("player", ValueType.TEXT)),

    /**
     * <h1>Player Conditions.</h1>
     */

    IF_PLAYER_NAME_EQUALS(ActionCategory.PLAYER_CONDITION, MenusCategory.PARAMS, PlayerNameEqualsCondition.class, Material.NAME_TAG, new ArgumentSlot("names", ValueType.TEXT, (byte) 18), new ParameterSlot("require-caps")),
    IF_PLAYER_MESSAGE_EQUALS(ActionCategory.PLAYER_CONDITION, MenusCategory.PARAMS, MessageEqualsCondition.class, Material.BOOK, new ArgumentSlot("messages", ValueType.TEXT, (byte) 18), new ParameterSlot("require-caps")),
    IF_PLAYER_HAS_POTION_EFFECTS(ActionCategory.PLAYER_CONDITION, MenusCategory.PARAMS, HasPotionEffectsCondition.class, Material.POTION, new ArgumentSlot("potions", ValueType.POTION, (byte) 18), new ParameterSlot("all")),

    IF_PLAYER_STANDS_ON_BLOCK(ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, IsStandingOnBlockCondition.class, Material.GRASS_BLOCK, new ArgumentSlot("blocks", ValueType.ITEM, (byte) 9)),
    IF_PLAYER_LOOKS_AT_BLOCK(ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, IsLookingAtBlockCondition.class, Material.CHEST, new ArgumentSlot("blocks", ValueType.ITEM, (byte) 9), new ArgumentSlot("locations", ValueType.LOCATION, (byte) 9), new ArgumentSlot("radius", ValueType.NUMBER)),
    IF_PLAYER_BLOCK_EQUALS(ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, IsBlockEqualsCondition.class, Material.OAK_LOG, new ArgumentSlot("blocks", ValueType.ITEM, (byte) 9)),
    IF_PLAYER_IN_AREA(ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, PlayerInAreaCondition.class, Material.PAPER, new ArgumentSlot("first", ValueType.LOCATION), new ArgumentSlot("second", ValueType.LOCATION)),
    IF_PLAYER_IS_UNDERWATER(ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, IsUnderWater.class, Material.BLUE_STAINED_GLASS),
    IF_PLAYER_IS_IN_RAIN(ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, IsInRain.class, Material.WATER_BUCKET),
    IF_PLAYER_IS_IN_LAVA(ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, IsInLava.class, Material.LAVA_BUCKET),
    IF_PLAYER_IS_IN_POWDERED_SNOW(ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, IsInPowderedSnow.class, Material.POWDER_SNOW_BUCKET),
    IF_PLAYER_IS_INSIDE_VEHICLE(ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, IsInsideVehicle.class, Material.MINECART),

    IF_PLAYER_ITEM_EQUALS(ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, IsItemEqualsCondition.class, Material.GLOW_ITEM_FRAME, new ArgumentSlot("items", ValueType.ITEM, (byte) 18), new ParameterSlot("ignore-amount", Material.BEETROOT_SEEDS, Material.OAK_BUTTON), new ParameterSlot("ignore-name", Material.NAME_TAG, Material.STRING), new ParameterSlot("ignore-lore", Material.WRITABLE_BOOK, Material.COBWEB), new ParameterSlot("ignore-enchantments", Material.ENCHANTED_BOOK, Material.BOOK), new ParameterSlot("ignore-flags", Material.BLUE_BANNER, Material.WHITE_BANNER), new ParameterSlot("ignore-material", Material.CRAFTING_TABLE, Material.WHITE_STAINED_GLASS), new ParameterSlot("ignore-damage", Material.DEAD_BUSH, Material.GOLDEN_HOE)),
    IF_PLAYER_HAS_ITEM_COOLDOWN(ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, HasItemCooldownCondition.class, Material.CLOCK, new ArgumentSlot("items", ValueType.ITEM, (byte) 18)),
    IF_PLAYER_IS_NEAR_LOCATION(ActionCategory.PLAYER_CONDITION, MenusCategory.MOVEMENT, IsNearLocationCondition.class, Material.COMPASS, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("distance", ValueType.NUMBER)),
    IF_PLAYER_HAS_ITEM(ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, HasItemCondition.class, Material.CHEST_MINECART, new ArgumentSlot("items", ValueType.ITEM, (byte) 18), new ParameterSlot("all"), new ParameterSlot("compare-mode", Arrays.asList("min-items", "full", "material"), Material.TORCH, Material.SOUL_TORCH, Material.REDSTONE_TORCH)),
    IF_PLAYER_HAS_ITEM_IN_AMOUNT(ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, HasItemInAmountCondition.class, Material.CHEST_MINECART, new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("amount", ValueType.NUMBER), new ParameterSlot("compare-mode", Arrays.asList("full", "material"), Material.SOUL_TORCH, Material.REDSTONE_TORCH)),
    IF_PLAYER_IS_INVENTORY_OWN(ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, IsClickedInOwnInventoryCondition.class, Material.PLAYER_HEAD),
    IF_PLAYER_INVENTORY_NAME_EQUALS(ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, IsInventoryNameEqualsCondition.class, Material.BOOK, new ArgumentSlot("names", ValueType.TEXT, (byte) 18), new ParameterSlot("require-color", Material.WHITE_WOOL, Material.BLUE_WOOL), new ParameterSlot("require-caps")),
    IF_PLAYER_HAS_ITEM_IN_HAND(ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, HasItemInHandCondition.class, Material.WOODEN_SWORD, new ArgumentSlot("items", ValueType.ITEM, (byte) 18), new ParameterSlot("hand", Arrays.asList("main-hand", "off-hand", "main-or-off-hands", "main-and-off-hands"), Material.DIAMOND_SWORD, Material.SHIELD, Material.BOW, Material.CHEST), new ParameterSlot("ignore-amount", Material.BEETROOT_SEEDS, Material.OAK_BUTTON), new ParameterSlot("ignore-name", Material.NAME_TAG, Material.STRING), new ParameterSlot("ignore-lore", Material.WRITABLE_BOOK, Material.COBWEB), new ParameterSlot("ignore-enchantments", Material.ENCHANTED_BOOK, Material.BOOK), new ParameterSlot("ignore-flags", Material.BLUE_BANNER, Material.WHITE_BANNER), new ParameterSlot("ignore-material", Material.CRAFTING_TABLE, Material.WHITE_STAINED_GLASS), new ParameterSlot("ignore-damage", Material.DEAD_BUSH, Material.GOLDEN_HOE)),
    IF_PLAYER_IS_INVENTORY_FULL(ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, IsInventoryFullCondition.class, Material.GLOWSTONE_DUST),
    IF_PLAYER_CLICKED_SLOT_EQUALS(ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, EqualsClickedSlotCondition.class, Material.SLIME_BALL, new ArgumentSlot("slots", ValueType.NUMBER, (byte) 27)),
    IF_PLAYER_CURRENT_SLOT_EQUALS(ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, IsCurrentSlotEqualsCondition.class, Material.GRAY_STAINED_GLASS_PANE, new ArgumentSlot("slots", ValueType.NUMBER, (byte) 9)),

    IF_PLAYER_LIKED_WORLD(ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, IsLikedWorldCondition.class, Material.GOLDEN_APPLE),
    IF_PLAYER_IS_SNEAKING(ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, IsSneakingCondition.class, Material.RABBIT),
    IF_PLAYER_IS_ON_GROUND(ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, IsOnGroundCondition.class, Material.STONE_SLAB),
    IF_PLAYER_IS_FLYING(ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, IsFlyingCondition.class, Material.FEATHER),
    IF_PLAYER_HAS_SAVED_PURCHASE(ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, HasSavedPurchaseCondition.class, Material.GOLD_BLOCK, new ArgumentSlot("names", ValueType.TEXT, (byte) 18)),
    IF_PLAYER_IS_IN_TEAM(ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, IsPlayerInTeam.class, Material.LIME_BANNER, new ArgumentSlot("scoreboard", ValueType.TEXT), new ArgumentSlot("team", ValueType.TEXT)),
    IF_PLAYER_IS_DEAD(ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, IsPlayerDead.class, Material.REDSTONE),
    IF_PLAYER_GAME_MODE_EQUALS(ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, EqualsGameModeCondition.class, Material.CRAFTING_TABLE, new ParameterSlot("game-mode", Arrays.asList("adventure", "survival", "creative", "spectator"), Material.PUFFERFISH_BUCKET, Material.CRAFTING_TABLE, Material.TNT, Material.FEATHER)),
    IF_PLAYER_IS_SPRINTING(ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, IsSprintingCondition.class, Material.IRON_BOOTS),
    IF_PLAYER_IS_GLOWING(ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, IsGlowingCondition.class, Material.WHITE_STAINED_GLASS),
    IF_PLAYER_IS_GLIDING(ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, IsGlidingCondition.class, Material.ELYTRA),
    IF_PLAYER_IS_BLOCKING(ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, IsBlockingCondition.class, Material.SHIELD),
    IF_PLAYER_HAS_WORLD_PERMISSION(ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, HasWorldPermissionCondition.class, Material.COMPARATOR, new ParameterSlot("permission", List.of("owner", "build", "develop"), Material.PUFFERFISH, Material.BRICK, Material.REPEATING_COMMAND_BLOCK)),
    IF_PLAYER_IS_FLIGHT_ALLOWED(ActionCategory.PLAYER_CONDITION, MenusCategory.STATE, IsAllowedFlightCondition.class, Material.FEATHER),

    IF_PLAYER_HAS_RESOURCE_PACK(ActionCategory.PLAYER_CONDITION, MenusCategory.APPEARANCE, HasResourcePackCondition.class, Material.SHROOMLIGHT),
    IF_PLAYER_CAN_SEE_PLAYER(ActionCategory.PLAYER_CONDITION, MenusCategory.APPEARANCE, CanSeePlayersCondition.class, Material.PLAYER_HEAD, new ArgumentSlot("players", ValueType.TEXT, (byte) 18), new ParameterSlot("all")),
    IF_PLAYER_CAN_SEE_ENTITY(ActionCategory.PLAYER_CONDITION, MenusCategory.APPEARANCE, CanSeeEntitiesCondition.class, Material.PIGLIN_HEAD, new ArgumentSlot("entities", ValueType.TEXT, (byte) 18), new ParameterSlot("all")),
    IF_PLAYER_SCOREBOARD_EQUALS(ActionCategory.PLAYER_CONDITION, MenusCategory.APPEARANCE, IsCurrentScoreboardCondition.class, Material.PAINTING, new ArgumentSlot("scoreboards", ValueType.TEXT, (byte) 27)),
    IF_PLAYER_BOSS_BAR_EQUALS(ActionCategory.PLAYER_CONDITION, MenusCategory.APPEARANCE, IsCurrentBossBarCondition.class, Material.DRAGON_BREATH, new ArgumentSlot("bossbars", ValueType.TEXT, (byte) 18), new ParameterSlot("all")),
    IF_PLAYER_IS_TIME_RELATIVE(ActionCategory.PLAYER_CONDITION, MenusCategory.APPEARANCE, IsTimeRelativeCondition.class, Material.CLOCK),

    //IF_PLAYER_CURSOR_ITEM_EQUALS(                   ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, null, Material.TRIPWIRE_HOOK, new ArgumentSlot("items", ValueType.ITEM,(byte) 18), new ParameterSlot("ignore-amount", Material.BEETROOT_SEEDS, Material.OAK_BUTTON), new ParameterSlot("ignore-name", Material.NAME_TAG, Material.STRING), new ParameterSlot("ignore-lore", Material.WRITABLE_BOOK, Material.COBWEB), new ParameterSlot("ignore-enchantments", Material.ENCHANTED_BOOK, Material.BOOK), new ParameterSlot("ignore-flags",Material.BLUE_BANNER,Material.WHITE_BANNER), new ParameterSlot("ignore-material",Material.CRAFTING_TABLE,Material.WHITE_STAINED_GLASS)),
    //IF_PLAYER_INTERACT_TYPE_EQUALS(                   ActionCategory.PLAYER_CONDITION, MenusCategory.INTERACTION, null, Material.BIRCH_PRESSURE_PLATE, new ParameterSlot("id",List.of("physical","left-click-block","right-click-block","left-click-air","right-click-air"),Material.STONE_PRESSURE_PLATE,Material.MAGMA_BLOCK,Material.GRASS_BLOCK,Material.GRAY_STAINED_GLASS,Material.WHITE_STAINED_GLASS)),
    //IF_PLAYER_IS_INVENTORY_OPENED(                   ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, null, Material.CHEST),
    //IF_PLAYER_INVENTORY_CLICK_EQUALS(                   ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, null, Material.BIRCH_PRESSURE_PLATE, new ParameterSlot("id",List.of("left","right","shift-left","shift-right","middle","drop","control-drop","number-key","swap-offhand","creative","window-border-left","window-border-right","unknown"),Material.LEVER,Material.LEVER,Material.TRIPWIRE_HOOK,Material.TRIPWIRE_HOOK,Material.STONE_BUTTON,Material.HOPPER,Material.HOPPER_MINECART,Material.STONECUTTER,Material.SHIELD,Material.PUFFERFISH_BUCKET,Material.BIRCH_CHEST_BOAT,Material.SPRUCE_CHEST_BOAT,Material.STRUCTURE_VOID)),
    //IF_PLAYER_EQUIPMENT_SLOT_EQUALS(                   ActionCategory.PLAYER_CONDITION, MenusCategory.INTERACTION, null, Material.LEVER, new ParameterSlot("id",List.of("hand","off-hand","head","body","chest","legs","foot"),Material.NETHERITE_SWORD,Material.SHIELD,Material.NETHERITE_HELMET,Material.NETHERITE_CHESTPLATE,Material.DIAMOND_CHESTPLATE,Material.NETHERITE_LEGGINGS,Material.NETHERITE_BOOTS)),
    //IF_PLAYER_WEARS_ITEM(                 ActionCategory.PLAYER_CONDITION, MenusCategory.INVENTORY, null, Material.NETHERITE_HELMET, new ArgumentSlot("items", ValueType.ITEM,(byte) 18), new ParameterSlot("slot",Arrays.asList("any","helmet","chestplate","leggings","boots"),Material.GRAY_STAINED_GLASS,Material.NETHERITE_HELMET,Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS), new ParameterSlot("ignore-amount", Material.BEETROOT_SEEDS, Material.OAK_BUTTON), new ParameterSlot("ignore-name", Material.NAME_TAG, Material.STRING), new ParameterSlot("ignore-lore", Material.WRITABLE_BOOK, Material.COBWEB), new ParameterSlot("ignore-enchantments", Material.ENCHANTED_BOOK, Material.BOOK), new ParameterSlot("ignore-flags",Material.BLUE_BANNER,Material.WHITE_BANNER), new ParameterSlot("ignore-material",Material.CRAFTING_TABLE,Material.WHITE_STAINED_GLASS)),

    /**
     * <h1>Control Actions.</h1>
     */

    //ELSE(ActionCategory.ELSE_CONDITION,null,null,null),

    CONTROL_WAIT(ActionCategory.CONTROL_ACTION, MenusCategory.LINES, WaitAction.class, Material.CLOCK, new ArgumentSlot("time", ValueType.NUMBER)),
    CONTROL_BREAK_REPEAT(ActionCategory.CONTROL_ACTION, MenusCategory.LINES, BreakRepeatAction.class, Material.PRISMARINE_WALL),
    CONTROL_SKIP_REPEAT(ActionCategory.CONTROL_ACTION, MenusCategory.LINES, SkipRepeatAction.class, Material.PRISMARINE_STAIRS),
    CONTROL_CLEAR_LOCAL_VARIABLES(ActionCategory.CONTROL_ACTION, MenusCategory.LINES, ClearLocalVariablesAction.class, Material.SLIME_BALL),
    CONTROL_CLEAR_GLOBAL_VARIABLES(ActionCategory.CONTROL_ACTION, MenusCategory.LINES, ClearGlobalVariablesAction.class, Material.MAGMA_CREAM),
    CONTROL_STOP_CODE_LINE(ActionCategory.CONTROL_ACTION, MenusCategory.LINES, StopCodeLineAction.class, Material.STRUCTURE_VOID),
    CONTROL_THROW_ERROR(ActionCategory.CONTROL_ACTION, MenusCategory.LINES, ThrowErrorAction.class, Material.TNT_MINECART, new ArgumentSlot("message", ValueType.TEXT)),

    CONTROL_CANCEL_EVENT(ActionCategory.CONTROL_ACTION, MenusCategory.EVENTS, CancelEventAction.class, Material.BARRIER),
    CONTROL_UNCANCEL_EVENT(ActionCategory.CONTROL_ACTION, MenusCategory.EVENTS, UncancelEventAction.class, Material.STRUCTURE_VOID),


    /**
     * <h1>World Actions.</h1>
     */

    WORLD_SWITCH_TO_BUILD_MODE(ActionCategory.WORLD_ACTION, MenusCategory.WORLD, SwitchToBuildModeAction.class, Material.BRICKS),
    WORLD_TRANSFER_VARIABLE(ActionCategory.WORLD_ACTION, MenusCategory.WORLD, TransferVariableAction.class, Material.CALIBRATED_SCULK_SENSOR, new ArgumentSlot("world", ValueType.TEXT), new ArgumentSlot("key", ValueType.TEXT), new ArgumentSlot("value", ValueType.ANY)),
    WORLD_SEND_WEB_REQUEST(ActionCategory.WORLD_ACTION, MenusCategory.WORLD, SendWebRequestAction.class, Material.BEACON, new ArgumentSlot("url", ValueType.TEXT), new ArgumentSlot("body", ValueType.TEXT), new ParameterSlot("request", List.of("get", "post"), Material.BOOK, Material.FEATHER), new ParameterSlot("media", List.of("text", "json"), Material.CHISELED_BOOKSHELF, Material.BOOKSHELF)),
    WORLD_ADD_CRAFTING_RECIPE(ActionCategory.WORLD_ACTION, MenusCategory.WORLD, AddCraftingRecipeAction.class, Material.CRAFTING_TABLE, new ArgumentSlot("items", ValueType.ITEM, (byte) 9, true), new ArgumentSlot("result", ValueType.ITEM), new ArgumentSlot("name", ValueType.TEXT), new ParameterSlot("ignore-shape"), new ParameterSlot("ignore-data"), new ParameterSlot("category", List.of("misc", "building", "equipment", "redstone"), Material.APPLE, Material.BRICKS, Material.IRON_AXE, Material.REDSTONE)),
    WORLD_ADD_FURNACE_RECIPE(ActionCategory.WORLD_ACTION, MenusCategory.WORLD, AddFurnaceRecipeAction.class, Material.FURNACE, new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("result", ValueType.ITEM), new ArgumentSlot("name", ValueType.TEXT), new ArgumentSlot("time", ValueType.NUMBER), new ArgumentSlot("experience", ValueType.NUMBER), new ParameterSlot("category", List.of("food", "building", "misc"), Material.PORKCHOP, Material.STONE, Material.LAVA_BUCKET)),
    WORLD_ADD_BLAST_FURNACE_RECIPE(ActionCategory.WORLD_ACTION, MenusCategory.WORLD, AddBlastingFurnaceRecipeAction.class, Material.BLAST_FURNACE, new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("result", ValueType.ITEM), new ArgumentSlot("name", ValueType.TEXT), new ArgumentSlot("time", ValueType.NUMBER), new ArgumentSlot("experience", ValueType.NUMBER), new ParameterSlot("category", List.of("misc", "building"), Material.REDSTONE_BLOCK, Material.STONE)),
    WORLD_ADD_SMOKER_RECIPE(ActionCategory.WORLD_ACTION, MenusCategory.WORLD, AddSmokerRecipeAction.class, Material.SMOKER, new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("result", ValueType.ITEM), new ArgumentSlot("name", ValueType.TEXT), new ArgumentSlot("time", ValueType.NUMBER), new ArgumentSlot("experience", ValueType.NUMBER)),
    WORLD_ADD_CAMPFIRE_RECIPE(ActionCategory.WORLD_ACTION, MenusCategory.WORLD, AddCampfireRecipeAction.class, Material.CAMPFIRE, new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("result", ValueType.ITEM), new ArgumentSlot("name", ValueType.TEXT), new ArgumentSlot("time", ValueType.NUMBER), new ArgumentSlot("experience", ValueType.NUMBER)),

    WORLD_REMOVE_RECIPE(ActionCategory.WORLD_ACTION, MenusCategory.WORLD, RemoveRecipeAction.class, Material.KNOWLEDGE_BOOK, new ArgumentSlot("recipes", ValueType.TEXT, (byte) 18)),

    WORLD_SET_TIME(ActionCategory.WORLD_ACTION, MenusCategory.WORLD, SetTimeAction.class, Material.CLOCK, new ArgumentSlot("time", ValueType.NUMBER)),
    WORLD_SET_WEATHER(ActionCategory.WORLD_ACTION, MenusCategory.WORLD, SetWeatherAction.class, Material.WATER_BUCKET, new ParameterSlot("weather", Arrays.asList("clean", "storm", "thunder"), Material.SUNFLOWER, Material.WATER_BUCKET, Material.TRIDENT), new ArgumentSlot("duration", ValueType.NUMBER)),
    WORLD_SET_WORLD_BORDER(ActionCategory.WORLD_ACTION, MenusCategory.WORLD, SetWorldBorderAction.class, Material.END_CRYSTAL, new ArgumentSlot("radius", ValueType.NUMBER), new ArgumentSlot("time", ValueType.NUMBER), new ArgumentSlot("damage", ValueType.NUMBER), new ArgumentSlot("warning-distance", ValueType.NUMBER), new ArgumentSlot("warning-time", ValueType.NUMBER), new ArgumentSlot("safe-distance", ValueType.NUMBER)),
    WORLD_SET_DIFFICULTY(ActionCategory.WORLD_ACTION, MenusCategory.WORLD, SetDifficultyAction.class, Material.WITHER_SKELETON_SKULL, new ParameterSlot("difficulty", List.of("peaceful", "easy", "normal", "hard"), Material.GOLDEN_APPLE, Material.WOODEN_SWORD, Material.IRON_SWORD, Material.NETHERITE_SWORD)),
    WORLD_VISUAL_PARAM_PHYS_OBJECT(
            ActionCategory.WORLD_ACTION, MenusCategory.WORLD, VisualParamPhysObjectAction.class,
            Material.FIRE_CHARGE,
            new ArgumentSlot("variable", ValueType.VARIABLE),
            new ArgumentSlot("particle", ValueType.PARTICLE),
            new ArgumentSlot("particle2", ValueType.PARTICLE),
            new ArgumentSlot("param1", ValueType.NUMBER),
            new ArgumentSlot("param2", ValueType.NUMBER),
            new ArgumentSlot("param3", ValueType.NUMBER),
            new ArgumentSlot("param4", ValueType.NUMBER),
            new ArgumentSlot("hit-particle", ValueType.PARTICLE),
            new ArgumentSlot("hit-param1", ValueType.NUMBER)
    ),

    WORLD_MOTION_PARAM_PHYS_OBJECT(
            ActionCategory.WORLD_ACTION, MenusCategory.WORLD, MotionParamPhysObjectAction.class,
            Material.FEATHER,
            new ArgumentSlot("variable", ValueType.VARIABLE),
            new ArgumentSlot("location", ValueType.LOCATION),
            new ArgumentSlot("speed", ValueType.NUMBER),
            new ArgumentSlot("weight", ValueType.NUMBER),
            new ArgumentSlot("acceleration-speed", ValueType.NUMBER),
            new ArgumentSlot("speed-limit", ValueType.NUMBER),
            new ArgumentSlot("acceleration-weight", ValueType.NUMBER),
            new ArgumentSlot("weight-limit", ValueType.NUMBER)
    ),
    WORLD_SETTINGS_PARAM_PHYS_OBJECT(
            ActionCategory.WORLD_ACTION, MenusCategory.WORLD, SettingsParamPhysObjectAction.class,
            Material.PAPER,
            new ArgumentSlot("variable", ValueType.VARIABLE),
            new ArgumentSlot("damage", ValueType.NUMBER),
            new ArgumentSlot("explosion", ValueType.NUMBER),
            new ArgumentSlot("potion", ValueType.POTION),
            new ArgumentSlot("shockwave-range", ValueType.NUMBER),
            new ArgumentSlot("shockwave-power", ValueType.NUMBER)
    ),

    WORLD_ADD_PHYS_OBJECT(
            ActionCategory.WORLD_ACTION, MenusCategory.WORLD, AddPhysObjectAction.class,
            Material.STRUCTURE_BLOCK,
            new ArgumentSlot("visual", ValueType.VARIABLE),
            new ArgumentSlot("motion", ValueType.VARIABLE),
            new ArgumentSlot("settings", ValueType.VARIABLE)
    ),

    WORLD_CREATE_SCOREBOARD(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, CreateScoreboardAction.class, Material.PAINTING, new ArgumentSlot("name", ValueType.TEXT), new ArgumentSlot("display-name", ValueType.TEXT)),
    WORLD_SCOREBOARD_SET_SCORE(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, ScoreboardSetScoreAction.class, Material.ITEM_FRAME, new ArgumentSlot("name", ValueType.TEXT), new ArgumentSlot("object", ValueType.TEXT), new ArgumentSlot("score", ValueType.NUMBER)),
    WORLD_SCOREBOARD_RESET_SCORE(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, ScoreboardResetScoreAction.class, Material.GLOW_ITEM_FRAME, new ArgumentSlot("name", ValueType.TEXT), new ArgumentSlot("object", ValueType.TEXT)),
    WORLD_SCOREBOARD_SET_DISPLAY_NAME(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, ScoreboardSetDisplayNameAction.class, Material.NAME_TAG, new ArgumentSlot("name", ValueType.TEXT), new ArgumentSlot("display-name", ValueType.TEXT)),
    WORLD_SCOREBOARD_CLEAR_SCORES(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, ClearScoreboardScoresAction.class, Material.PAPER, new ArgumentSlot("scoreboards", ValueType.TEXT, (byte) 27)),
    WORLD_SCOREBOARD_SET_NUMBER_STYLE(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, SetScoreNumberStyleAction.class, Material.SLIME_BALL, new ArgumentSlot("scoreboards", ValueType.TEXT, (byte) 18), new ArgumentSlot("style", ValueType.TEXT)),
    WORLD_DELETE_SCORE_BOARD(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, DeleteScoreboardAction.class, Material.STRUCTURE_VOID, new ArgumentSlot("scoreboards", ValueType.TEXT, (byte) 27)),
    WORLD_CREATE_BOSS_BAR(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, CreateBossBarAction.class, Material.DRAGON_BREATH, new ArgumentSlot("name", ValueType.TEXT), new ArgumentSlot("display-name", ValueType.TEXT), new ArgumentSlot("progress", ValueType.NUMBER), new ParameterSlot("color", List.of("purple", "red", "yellow", "green", "blue", "pink", "white"), Material.PURPLE_STAINED_GLASS, Material.RED_STAINED_GLASS, Material.YELLOW_STAINED_GLASS, Material.LIME_STAINED_GLASS, Material.BLUE_STAINED_GLASS, Material.PINK_STAINED_GLASS, Material.WHITE_STAINED_GLASS), new ParameterSlot("overlay", List.of("progress", "notched_6", "notched_10", "notched_12", "notched_20"), Material.BREEZE_ROD, Material.BLAZE_ROD, Material.STICK, Material.BONE, Material.AMETHYST_SHARD)),
    WORLD_BOSS_BAR_COLOR(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, BossBarColorAction.class, Material.PURPLE_DYE, new ArgumentSlot("name", ValueType.TEXT), new ParameterSlot("color", List.of("purple", "red", "yellow", "green", "blue", "pink", "white"), Material.PURPLE_STAINED_GLASS, Material.RED_STAINED_GLASS, Material.YELLOW_STAINED_GLASS, Material.LIME_STAINED_GLASS, Material.BLUE_STAINED_GLASS, Material.PINK_STAINED_GLASS, Material.WHITE_STAINED_GLASS)),
    WORLD_BOSS_BAR_PROGRESS(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, BossBarProgressAction.class, Material.EXPERIENCE_BOTTLE, new ArgumentSlot("name", ValueType.TEXT), new ArgumentSlot("progress", ValueType.NUMBER)),
    WORLD_BOSS_BAR_DISPLAY_NAME(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, BossBarDisplayNameAction.class, Material.BOOK, new ArgumentSlot("name", ValueType.TEXT), new ArgumentSlot("display-name", ValueType.TEXT)),
    WORLD_BOSS_BAR_OVERLAY(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, BossBarOverlayAction.class, Material.BREEZE_ROD, new ArgumentSlot("name", ValueType.TEXT), new ParameterSlot("overlay", List.of("progress", "notched_6", "notched_10", "notched_12", "notched_20"), Material.BREEZE_ROD, Material.BLAZE_ROD, Material.STICK, Material.BONE, Material.AMETHYST_SHARD)),
    WORLD_DELETE_BOSS_BAR(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, DeleteBossBarAction.class, Material.POTION, new ArgumentSlot("name", ValueType.TEXT, (byte) 27)),
    WORLD_CREATE_TEAM(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, CreateTeamAction.class, Material.LIME_BANNER, new ArgumentSlot("scoreboard", ValueType.TEXT), new ArgumentSlot("team", ValueType.TEXT)),
    WORLD_TEAM_SET_COLOR(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, TeamSetColorAction.class, Material.BLUE_DYE, new ArgumentSlot("scoreboard", ValueType.TEXT), new ArgumentSlot("team", ValueType.TEXT), new ArgumentSlot("color", ValueType.COLOR)),
    WORLD_TEAM_SET_COLLISION_RULE(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, TeamSetCollisionRuleAction.class, Material.PLAYER_HEAD, new ArgumentSlot("scoreboard", ValueType.TEXT), new ArgumentSlot("team", ValueType.TEXT), new ParameterSlot("option", List.of("always", "for-own-team", "for-other-teams", "never"), Material.LIME_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.RED_SHULKER_BOX)),
    WORLD_TEAM_SET_NAME_TAG_VISIBLE(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, TeamSetVisibleTagAction.class, Material.NAME_TAG, new ArgumentSlot("scoreboard", ValueType.TEXT), new ArgumentSlot("team", ValueType.TEXT), new ParameterSlot("option", List.of("always", "for-own-team", "for-other-teams", "never"), Material.LIME_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.RED_SHULKER_BOX)),
    WORLD_TEAM_SET_CAN_SEE_INVISIBLE(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, TeamSetCanSeeInvisibleAction.class, Material.GRAY_BANNER, new ArgumentSlot("scoreboard", ValueType.TEXT), new ArgumentSlot("team", ValueType.TEXT), new ParameterSlot("visible")),
    WORLD_DELETE_TEAM(ActionCategory.WORLD_ACTION, MenusCategory.APPEARANCE, DeleteTeamAction.class, Material.RED_BANNER, new ArgumentSlot("scoreboard", ValueType.TEXT), new ArgumentSlot("team", ValueType.TEXT)),

    WORLD_SPAWN_ENTITY(ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, SpawnEntityAction.class, Material.SPAWNER, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 9), new ArgumentSlot("type", ValueType.ITEM), new ArgumentSlot("name", ValueType.TEXT), new ParameterSlot("show-name", true, Material.NAME_TAG, Material.STRING), new ParameterSlot("gravity", true, Material.SAND, Material.COBWEB), new ParameterSlot("ai", true, Material.PLAYER_HEAD, Material.SKELETON_SKULL), new ParameterSlot("glowing", Material.GLASS, Material.WHITE_STAINED_GLASS), new ParameterSlot("invisible", Material.POTION, Material.GLASS_BOTTLE), new ParameterSlot("invulnerable", Material.REDSTONE, Material.TOTEM_OF_UNDYING), new ParameterSlot("visible-for-all", true, Material.GRASS_BLOCK, Material.GRAY_STAINED_GLASS)),
    WORLD_SPAWN_ITEM_DISPLAY(ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, SpawnItemDisplayAction.class, Material.ITEM_FRAME, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("name", ValueType.TEXT)),
    WORLD_SPAWN_TEXT_DISPLAY(ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, SpawnTextDisplayAction.class, Material.DARK_OAK_SIGN, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("text", ValueType.TEXT), new ArgumentSlot("name", ValueType.TEXT)),
    WORLD_SPAWN_BLOCK_DISPLAY(ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, SpawnBlockDisplayAction.class, Material.GRASS_BLOCK, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("block", ValueType.ITEM), new ArgumentSlot("name", ValueType.TEXT)),
    WORLD_SPAWN_EYE_OF_ENDER(ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, SpawnEnderEyeAction.class, Material.ENDER_EYE, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("name", ValueType.TEXT), new ArgumentSlot("despawn-time", ValueType.NUMBER), new ArgumentSlot("target", ValueType.LOCATION), new ParameterSlot("drop", List.of("random", "drop", "shatter"), Material.PRISMARINE_SHARD, Material.ENDER_EYE, Material.BLUE_STAINED_GLASS)),
    WORLD_SPAWN_FALLING_BLOCK(ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, SpawnFallingBlockAction.class, Material.SAND, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("block", ValueType.ITEM), new ArgumentSlot("name", ValueType.TEXT), new ParameterSlot("drop"), new ArgumentSlot("damage", ValueType.NUMBER)),
    WORLD_SPAWN_END_CRYSTAL(ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, SpawnEndCrystalAction.class, Material.END_CRYSTAL, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("name", ValueType.TEXT), new ParameterSlot("bottom"), new ArgumentSlot("beam", ValueType.LOCATION)),
    WORLD_CREATE_EXPLOSION(ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, CreateExplosionAction.class, Material.TNT, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("power", ValueType.NUMBER), new ParameterSlot("fire", Material.GUNPOWDER, Material.CAMPFIRE), new ParameterSlot("damage", Material.GRASS_BLOCK, Material.MAGMA_BLOCK)),
    WORLD_STRIKE_LIGHTNING(ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, StrikeLightningAction.class, Material.TRIDENT, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ParameterSlot("damage", true, Material.REDSTONE, Material.GLOWSTONE_DUST)),
    WORLD_DROP_ITEM(ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, DropItemAction.class, Material.BREAD, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ParameterSlot("naturally", true, Material.PUMPKIN_SEEDS, Material.PUMPKIN_SEEDS), new ArgumentSlot("item", ValueType.ITEM)),
    WORLD_SPAWN_PARTICLE(ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, SpawnParticleAction.class, Material.NETHER_STAR, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("particle", ValueType.PARTICLE), new ArgumentSlot("count", ValueType.NUMBER), new ArgumentSlot("offset-x", ValueType.NUMBER), new ArgumentSlot("offset-y", ValueType.NUMBER), new ArgumentSlot("offset-z", ValueType.NUMBER)),
    WORLD_SPAWN_ARROW(ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, SpawnArrowAction.class, Material.ARROW, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("arrow", ValueType.ITEM)),
    WORLD_SPAWN_FIREWORK(ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, SpawnFireworkAction.class, Material.FIREWORK_ROCKET, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("firework", ValueType.ITEM)),
    WORLD_CREATE_FIREWORK_EXPLOSION(ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, CreateFireworkExplosionAction.class, Material.FIREWORK_STAR, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("firework", ValueType.ITEM)),
    WORLD_CREATE_EXPERIENCE_ORB(ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, SpawnExperienceOrbAction.class, Material.EXPERIENCE_BOTTLE, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("amount", ValueType.NUMBER)),
    WORLD_SPAWN_PARTICLES_LINE(ActionCategory.WORLD_ACTION, MenusCategory.ENTITY, SpawnParticlesLineAction.class, Material.BREEZE_ROD, new ArgumentSlot("first", ValueType.LOCATION), new ArgumentSlot("second", ValueType.LOCATION), new ArgumentSlot("particle", ValueType.PARTICLE), new ArgumentSlot("count", ValueType.NUMBER), new ArgumentSlot("offset-x", ValueType.NUMBER), new ArgumentSlot("offset-y", ValueType.NUMBER), new ArgumentSlot("offset-z", ValueType.NUMBER)),

    WORLD_COPY_BLOCKS(ActionCategory.WORLD_ACTION, MenusCategory.BLOCKS, CopyBlocksAction.class, Material.NETHERITE_SCRAP, new ArgumentSlot("first", ValueType.LOCATION), new ArgumentSlot("second", ValueType.LOCATION), new ArgumentSlot("from", ValueType.LOCATION), new ArgumentSlot("where", ValueType.LOCATION)),
    WORLD_SET_BLOCKS_AREA_TYPE(ActionCategory.WORLD_ACTION, MenusCategory.BLOCKS, SetBlocksAreaTypeAction.class, Material.COBBLESTONE, new ArgumentSlot("first", ValueType.LOCATION), new ArgumentSlot("second", ValueType.LOCATION), new ArgumentSlot("type", ValueType.ITEM)),
    WORLD_SET_BLOCK_TYPE(ActionCategory.WORLD_ACTION, MenusCategory.BLOCKS, SetBlockTypeAction.class, Material.STONE, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("type", ValueType.ITEM)),
    WORLD_DESTROY_BLOCK(ActionCategory.WORLD_ACTION, MenusCategory.BLOCKS, DestroyBlockAction.class, Material.TNT, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ParameterSlot("show-particle", true, Material.GUNPOWDER, Material.LIGHT_GRAY_STAINED_GLASS), new ParameterSlot("drop-experience", true, Material.EXPERIENCE_BOTTLE, Material.STRING)),
    WORLD_APPLY_BONE_MEAL(ActionCategory.WORLD_ACTION, MenusCategory.BLOCKS, ApplyBoneMealAction.class, Material.BONE_MEAL, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 27)),
    WORLD_SET_BLOCK_POWERED(ActionCategory.WORLD_ACTION, MenusCategory.BLOCKS, SetBlockPoweredAction.class, Material.REDSTONE_BLOCK, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ParameterSlot("powered", true, Material.REDSTONE_BLOCK, Material.COAL_BLOCK)),
    WORLD_SET_BLOCK_WATERLOGGED(ActionCategory.WORLD_ACTION, MenusCategory.BLOCKS, SetWaterLoggedAction.class, Material.WATER_BUCKET, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ParameterSlot("water", true, Material.WATER_BUCKET, Material.BUCKET)),
    WORLD_GIVE_CONTAINER_ITEMS(ActionCategory.WORLD_ACTION, MenusCategory.BLOCKS, GiveItemsToContainerAction.class, Material.CHEST, new ArgumentSlot("items", ValueType.ITEM, (byte) 18), new ArgumentSlot("location", ValueType.LOCATION)),
    WORLD_PUT_ITEM_IN_CONTAINER(ActionCategory.WORLD_ACTION, MenusCategory.BLOCKS, SetItemBySlotInContainerAction.class, Material.HOPPER, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("slot", ValueType.NUMBER), new ArgumentSlot("item", ValueType.ITEM)),
    WORLD_CLEAR_CONTAINER(ActionCategory.WORLD_ACTION, MenusCategory.BLOCKS, ClearContainerAction.class, Material.BARRIER, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 27)),
    WORLD_GET_CONTAINER_ITEMS(ActionCategory.WORLD_ACTION, MenusCategory.BLOCKS, GetContainerItemsAction.class, Material.HOPPER, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION)),
    WORLD_GET_SIGN_LINE(ActionCategory.WORLD_ACTION, MenusCategory.BLOCKS, GetSignLineAction.class, Material.OAK_SIGN, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION), new ParameterSlot("side", Arrays.asList("front", "back"), Material.OAK_SIGN, Material.WARPED_SIGN), new ArgumentSlot("number", ValueType.NUMBER)),
    WORLD_SET_SIGN_LINE(ActionCategory.WORLD_ACTION, MenusCategory.BLOCKS, SetSignLineAction.class, Material.BIRCH_SIGN, new ArgumentSlot("location", ValueType.LOCATION), new ParameterSlot("side", Arrays.asList("front", "back"), Material.OAK_SIGN, Material.WARPED_SIGN), new ArgumentSlot("number", ValueType.NUMBER), new ArgumentSlot("text", ValueType.TEXT)),
    WORLD_SET_SIGN_WAXED(ActionCategory.WORLD_ACTION, MenusCategory.BLOCKS, SetSignWaxedAction.class, Material.HONEYCOMB, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ParameterSlot("waxed", true, Material.HONEYCOMB, Material.GLASS_BOTTLE)),
    WORLD_SET_SIGN_GLOWING_TEXT(ActionCategory.WORLD_ACTION, MenusCategory.BLOCKS, SetSignGlowingTextAction.class, Material.GLOW_INK_SAC, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ParameterSlot("side", Arrays.asList("front", "back"), Material.OAK_SIGN, Material.WARPED_SIGN), new ParameterSlot("glowing", true, Material.GLOW_INK_SAC, Material.INK_SAC)),
    WORLD_SET_BLOCK_BIOME(ActionCategory.WORLD_ACTION, MenusCategory.BLOCKS, SetBlockBiomeAction.class, Material.MYCELIUM, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ArgumentSlot("biome", ValueType.TEXT)),

    /**
     * <h1>Variable Actions.</h1>
     */

    VAR_SET_VALUE(ActionCategory.VARIABLE_ACTION, MenusCategory.OTHER, SetVariableValueAction.class, Material.IRON_INGOT, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("value", ValueType.ANY)),
    VAR_SET_RANDOM_VALUE(ActionCategory.VARIABLE_ACTION, MenusCategory.OTHER, SetVariableRandomValueAction.class, Material.PUMPKIN_SEEDS, new ArgumentSlot("values", ValueType.ANY, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_GET_TYPE(ActionCategory.VARIABLE_ACTION, MenusCategory.OTHER, GetVariableTypeAction.class, Material.STRUCTURE_VOID,
            new ArgumentSlot("result", ValueType.VARIABLE),
            new ArgumentSlot("variable", ValueType.VARIABLE)
    ),
    VAR_DELETE_VARIABLE(ActionCategory.VARIABLE_ACTION, MenusCategory.OTHER, DeleteVariableAction.class, Material.BARRIER, new ArgumentSlot("variables", ValueType.VARIABLE, (byte) 18)),

    VAR_SUM_NUMBERS(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, SumNumbersAction.class, Material.BRICK, new ArgumentSlot("numbers", ValueType.NUMBER, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_SUM_ASSIGN_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, SumAssignNumberAction.class, Material.BRICKS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_SUBTRACT_NUMBERS(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, SubtractNumbersAction.class, Material.NETHER_BRICK, new ArgumentSlot("numbers", ValueType.NUMBER, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_SUBTRACT_ASSIGN_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, SubtractAssignNumberAction.class, Material.NETHER_BRICKS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_MULTIPLY_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, MultiplyNumberAction.class, Material.COPPER_INGOT, new ArgumentSlot("numbers", ValueType.NUMBER, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_MULTIPLY_ASSIGN_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, MultiplyAssignNumberAction.class, Material.COPPER_BLOCK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_DIVIDE_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, DivideNumberAction.class, Material.NETHERITE_INGOT, new ArgumentSlot("numbers", ValueType.NUMBER, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_DIVIDE_ASSIGN_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, DivideAssignNumberAction.class, Material.NETHERITE_BLOCK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_MODULAR_DIVIDE_NUMBERS(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, ModularDivideNumbersAction.class, Material.NETHER_WART, new ArgumentSlot("numbers", ValueType.NUMBER, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_MODULAR_DIVIDE_ASSIGN_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, ModularDivideAssignNumberAction.class, Material.NETHER_WART_BLOCK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_SET_RANDOM_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, RandomNumberAction.class, Material.ENDER_EYE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("min", ValueType.NUMBER), new ArgumentSlot("max", ValueType.NUMBER)),
    VAR_MODULE_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, ModuleNumberAction.class, Material.TURTLE_HELMET, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_ROUND_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, RoundNumberAction.class, Material.SNOWBALL, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER), new ParameterSlot("type", Arrays.asList("ceil", "floor"), Material.SNOWBALL, Material.SUNFLOWER), new ArgumentSlot("digits", ValueType.NUMBER)),
    VAR_MIN_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, MinNumberAction.class, Material.LIME_CARPET, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("first", ValueType.NUMBER), new ArgumentSlot("second", ValueType.NUMBER)),
    VAR_MAX_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, MaxNumberAction.class, Material.LIME_WOOL, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("first", ValueType.NUMBER), new ArgumentSlot("second", ValueType.NUMBER)),
    VAR_NEGATE_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, NegateNumberAction.class, Material.WEEPING_VINES, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_POWER_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, PowerNumberAction.class, Material.BREEZE_ROD, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER), new ArgumentSlot("power", ValueType.NUMBER)),
    VAR_LOGARITHM_OF_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, LogarithmOfNumberAction.class, Material.BLAZE_ROD, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_SQUARE_ROOT_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, SquareRootNumberAction.class, Material.MANGROVE_ROOTS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_TO_RADIANS(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, NumberToRadiansAction.class, Material.ARMADILLO_SCUTE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_TO_DEGREES(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, NumberToDegreesAction.class, Material.SUNFLOWER, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_SINE_OF_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, SineOfNumberAction.class, Material.TUBE_CORAL_FAN, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_COSINE_OF_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, CosineOfNumberAction.class, Material.BUBBLE_CORAL_FAN, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_ARCSINE_OF_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, ArcSineOfNumberAction.class, Material.TUBE_CORAL, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_ARCCOSINE_OF_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, ArcCosineOfNumberAction.class, Material.BUBBLE_CORAL, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_ARCTANGENT_OF_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, ArcTangentOfNumberAction.class, Material.HORN_CORAL, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_TANGENT_OF_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, TangentOfNumberAction.class, Material.HORN_CORAL_FAN, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("number", ValueType.NUMBER)),
    VAR_NOISE_PERLIN(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, PerlinNoiseAction.class, Material.BEETROOT_SEEDS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION), new ArgumentSlot("seed", ValueType.NUMBER), new ArgumentSlot("lacunarity", ValueType.NUMBER), new ArgumentSlot("octaves", ValueType.NUMBER), new ArgumentSlot("frequency", ValueType.NUMBER), new ArgumentSlot("amplitude", ValueType.NUMBER), new ParameterSlot("normalize")),
    VAR_NOISE_SIMPLEX(ActionCategory.VARIABLE_ACTION, MenusCategory.NUMBER_OPERATIONS, SimplexNoiseAction.class, Material.PUMPKIN_SEEDS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION), new ArgumentSlot("seed", ValueType.NUMBER), new ArgumentSlot("lacunarity", ValueType.NUMBER), new ArgumentSlot("octaves", ValueType.NUMBER), new ArgumentSlot("frequency", ValueType.NUMBER), new ArgumentSlot("amplitude", ValueType.NUMBER), new ParameterSlot("normalize")),

    VAR_PARSE_NUMBER(ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, ParseNumberAction.class, Material.SLIME_BLOCK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT)),
    VAR_CHAR_AT(ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, CharAtTextAction.class, Material.IRON_NUGGET, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT), new ArgumentSlot("index", ValueType.NUMBER)),
    VAR_CONCAT_TEXT(ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, ConcatTextAction.class, Material.BARREL, new ArgumentSlot("text", ValueType.TEXT, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE), new ParameterSlot("type", Arrays.asList("new-line", "join-spaces", "join"), Material.PAPER, Material.MAP, Material.FILLED_MAP)),
    VAR_SUBSTRING_TEXT(ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, SubstringTextAction.class, Material.SHEARS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT), new ArgumentSlot("from", ValueType.NUMBER), new ArgumentSlot("to", ValueType.NUMBER)),
    VAR_TRANSLATE_COLORS(ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, TranslateColorsAction.class, Material.PURPLE_WOOL, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT), new ArgumentSlot("character", ValueType.TEXT)),
    VAR_STRIP_COLORS(ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, StripColorAction.class, Material.WHITE_STAINED_GLASS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT)),
    VAR_REVERSE_TEXT(ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, ReverseTextAction.class, Material.ENCHANTING_TABLE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT)),
    VAR_FORMAT_UNIX_TIME(ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, FormatUnixTimeAction.class, Material.CLOCK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("time", ValueType.NUMBER), new ArgumentSlot("format", ValueType.TEXT)),
    VAR_UPPER_CASE_TEXT(ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, UpperCaseTextAction.class, Material.OBSERVER, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT)),
    VAR_LOWER_CASE_TEXT(ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, LowerCaseTextAction.class, Material.DROPPER, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT)),
    VAR_REPLACE_TEXT(ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, ReplaceTextAction.class, Material.MAGENTA_GLAZED_TERRACOTTA, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT), new ArgumentSlot("target", ValueType.TEXT), new ArgumentSlot("replacement", ValueType.TEXT)),
    VAR_SPLIT_TEXT(ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, SplitTextAction.class, Material.CHISELED_BOOKSHELF, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT), new ArgumentSlot("splitter", ValueType.TEXT)),
    VAR_PARSE_JSON(ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, ParseJSONAction.class, Material.KNOWLEDGE_BOOK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT)),
    VAR_CONVERT_TO_JSON(ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, ConvertToJSONAction.class, Material.ENCHANTED_BOOK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT), new ParameterSlot("pretty")),
    VAR_TEXT_LENGTH(ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, TextLengthAction.class, Material.SLIME_BALL, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("text", ValueType.TEXT)),
    VAR_CODE_POINT_OF_CHAR(ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, CodePointOfCharAction.class, Material.GOLD_NUGGET, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("char", ValueType.TEXT)),
    VAR_CHAR_FROM_CODE_POINT(ActionCategory.VARIABLE_ACTION, MenusCategory.TEXT_OPERATIONS, CharFromCodePointAction.class, Material.IRON_NUGGET, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("code-point", ValueType.NUMBER)),

    VAR_MODIFY_LOCATION(ActionCategory.VARIABLE_ACTION, MenusCategory.LOCATION_OPERATIONS, ModifyLocationAction.class, Material.WHITE_STAINED_GLASS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION), new ArgumentSlot("yaw", ValueType.NUMBER), new ArgumentSlot("pitch", ValueType.NUMBER), new ArgumentSlot("x", ValueType.NUMBER), new ArgumentSlot("y", ValueType.NUMBER), new ArgumentSlot("z", ValueType.NUMBER), new ParameterSlot("add")),
    VAR_LOCATION_TO_VECTOR(ActionCategory.VARIABLE_ACTION, MenusCategory.LOCATION_OPERATIONS, LocationToVectorAction.class, Material.PRISMARINE_SHARD, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION)),
    VAR_GET_BLOCK_TYPE(ActionCategory.VARIABLE_ACTION, MenusCategory.LOCATION_OPERATIONS, GetBlockTypeAction.class, Material.GRASS_BLOCK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION)),
    VAR_GET_BLOCK_FACING(ActionCategory.VARIABLE_ACTION, MenusCategory.LOCATION_OPERATIONS, GetBlockFacingAction.class, Material.ENDER_EYE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION)),
    VAR_GET_DISTANCE(ActionCategory.VARIABLE_ACTION, MenusCategory.LOCATION_OPERATIONS, GetDistanceAction.class, Material.SPYGLASS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("first", ValueType.LOCATION), new ArgumentSlot("second", ValueType.LOCATION)),
    VAR_GET_LOCATION_ALL(
            ActionCategory.VARIABLE_ACTION,
            MenusCategory.LOCATION_OPERATIONS,
            GetLocationAllCoordinateAction.class,
            Material.QUARTZ,
            new ArgumentSlot("location", ValueType.LOCATION),
            new ArgumentSlot("yaw", ValueType.VARIABLE),
            new ArgumentSlot("pitch", ValueType.VARIABLE),
            new ArgumentSlot("x", ValueType.VARIABLE),
            new ArgumentSlot("y", ValueType.VARIABLE),
            new ArgumentSlot("z", ValueType.VARIABLE)
    ),

    VAR_GET_LOCATION_X(
            ActionCategory.VARIABLE_ACTION,
            MenusCategory.LOCATION_OPERATIONS,
            GetLocationXAction.class,
            Material.RED_STAINED_GLASS,
            new ArgumentSlot("variable", ValueType.VARIABLE),
            new ArgumentSlot("location", ValueType.LOCATION)
    ),
    VAR_GET_LOCATION_Y(ActionCategory.VARIABLE_ACTION, MenusCategory.LOCATION_OPERATIONS, GetLocationYAction.class, Material.GREEN_STAINED_GLASS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION)),
    VAR_GET_LOCATION_Z(ActionCategory.VARIABLE_ACTION, MenusCategory.LOCATION_OPERATIONS, GetLocationZAction.class, Material.BLUE_STAINED_GLASS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION)),
    VAR_GET_LOCATION_YAW(ActionCategory.VARIABLE_ACTION, MenusCategory.LOCATION_OPERATIONS, ua.mcchickenstudio.opencreative.coding.blocks.actions.variableactions.location.GetLocationYawAction.class, Material.YELLOW_STAINED_GLASS_PANE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION)),
    VAR_GET_LOCATION_PITCH(ActionCategory.VARIABLE_ACTION, MenusCategory.LOCATION_OPERATIONS, GetLocationPitchAction.class, Material.ORANGE_STAINED_GLASS_PANE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("location", ValueType.LOCATION)),
    VAR_MOVE_LOCATION_BY_VECTOR(
            ActionCategory.VARIABLE_ACTION,
            MenusCategory.LOCATION_OPERATIONS,
            MoveLocationByVector.class,
            Material.PISTON,
            new ArgumentSlot("variable", ValueType.VARIABLE),
            new ArgumentSlot("target", ValueType.LOCATION),
            new ArgumentSlot("vector", ValueType.VECTOR),
            new ArgumentSlot("distance", ValueType.NUMBER)
    ),
    VAR_CREATE_LIST(ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, CreateListAction.class, Material.BOOKSHELF, new ArgumentSlot("elements", ValueType.ANY, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_ADD_TO_LIST(ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, AddToListAction.class, Material.KNOWLEDGE_BOOK, new ArgumentSlot("elements", ValueType.ANY, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_SET_IN_LIST(ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, SetInListAction.class, Material.CAULDRON, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("index", ValueType.NUMBER), new ArgumentSlot("value", ValueType.ANY)),
    VAR_GET_FIRST_FROM_LIST(ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, GetFirstValueFromListAction.class, Material.HOPPER_MINECART, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("list", ValueType.VARIABLE)),
    VAR_GET_BY_ID_FROM_LIST(ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, GetByIdFromListAction.class, Material.WATER_BUCKET, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("list", ValueType.VARIABLE), new ArgumentSlot("index", ValueType.NUMBER)),
    VAR_GET_LAST_FROM_LIST(ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, GetLastValueFromListAction.class, Material.HOPPER, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("list", ValueType.VARIABLE)),

    VAR_GET_RANDOM_FROM_LIST(ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, GetRandomFromListAction.class, Material.ENDER_EYE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("list", ValueType.VARIABLE)),
    VAR_SUBLIST_LIST(ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, SublistAction.class, Material.SHEARS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("list", ValueType.VARIABLE), new ArgumentSlot("from", ValueType.NUMBER), new ArgumentSlot("to", ValueType.NUMBER)),
    VAR_CLONE_LIST(ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, CloneListAction.class, Material.COMPOSTER, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("list", ValueType.VARIABLE)),
    VAR_MERGE_LISTS(ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, MergeListsAction.class, Material.LEAD, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("list", ValueType.VARIABLE)),
    VAR_CALCULATE_FROM_LIST(ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS,
            CalculateFromListAction.class,
            Material.COBWEB, new ArgumentSlot("variable", ValueType.VARIABLE),
            new ArgumentSlot("list", ValueType.VARIABLE),
            new ParameterSlot("calculation",
                    Arrays.asList(
                            "get-min", "get-max",
                            "average", "variance", "deviation",
                            "median", "distinct", "kurtosis",
                            "skewness", "mode", "quantile",
                            "iqr", "shannon-entropy", "gini-index",
                            "linear-trend", "jolt-delta-list", "outliers-list"
                    ),
                    Material.COAL, Material.DIAMOND, Material.ANVIL,
                    Material.IRON_INGOT, Material.NETHERITE_INGOT, Material.GOLD_INGOT,
                    Material.MELON_SEEDS, Material.STRING, Material.COBWEB,
                    Material.GRAY_CARPET, Material.FIREWORK_STAR, Material.NETHER_STAR,
                    Material.EMERALD, Material.IRON_NUGGET, Material.STICK,
                    Material.PUMPKIN_SEEDS, Material.WHEAT_SEEDS
            )),
    VAR_GET_LIST_SIZE(ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, GetListSizeAction.class, Material.SLIME_BALL, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("list", ValueType.VARIABLE)),
    VAR_REMOVE_BY_ID_FROM_LIST(ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, RemoveByIdFromListAction.class, Material.LAVA_BUCKET, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("index", ValueType.NUMBER)),
    VAR_REMOVE_BY_VALUE_FROM_LIST(ActionCategory.VARIABLE_ACTION, MenusCategory.LIST_OPERATIONS, RemoveByValueFromListAction.class, Material.POWDER_SNOW_BUCKET, new ArgumentSlot("elements", ValueType.ANY, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE), new ParameterSlot("deletion", List.of("all", "first", "last"), Material.RED_DYE, Material.ENDER_EYE, Material.ENDER_PEARL)),

    VAR_CREATE_MAP(ActionCategory.VARIABLE_ACTION, MenusCategory.MAP_OPERATIONS, CreateMapAction.class, Material.CHEST_MINECART, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("keys", ValueType.VARIABLE), new ArgumentSlot("values", ValueType.VARIABLE)),
    VAR_CREATE_MAP_BY_KEYS(ActionCategory.VARIABLE_ACTION, MenusCategory.MAP_OPERATIONS, CreateMapByKeysAction.class, Material.BARREL, new ArgumentSlot("keys", ValueType.ANY, (byte) 9), new ArgumentSlot("values", ValueType.ANY, (byte) 9), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_PUT_INTO_MAP(ActionCategory.VARIABLE_ACTION, MenusCategory.MAP_OPERATIONS, PutIntoMapAction.class, Material.CHEST, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("key", ValueType.ANY), new ArgumentSlot("value", ValueType.ANY)),
    VAR_GET_FROM_MAP_BY_KEY(ActionCategory.VARIABLE_ACTION, MenusCategory.MAP_OPERATIONS, GetFromMapByKeyAction.class, Material.MINECART, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("map", ValueType.VARIABLE), new ArgumentSlot("key", ValueType.ANY)),
    VAR_GET_KEYS_SET(ActionCategory.VARIABLE_ACTION, MenusCategory.MAP_OPERATIONS, GetKeysSetAction.class, Material.NAME_TAG, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("map", ValueType.VARIABLE)),
    VAR_GET_VALUES_SET(ActionCategory.VARIABLE_ACTION, MenusCategory.MAP_OPERATIONS, GetValuesSetAction.class, Material.APPLE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("map", ValueType.VARIABLE)),
    VAR_REMOVE_FROM_MAP_BY_KEY(ActionCategory.VARIABLE_ACTION, MenusCategory.MAP_OPERATIONS, RemoveFromMapByKeyAction.class, Material.BARRIER, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("key", ValueType.ANY)),

    VAR_CLONE_ITEM(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, CloneItemAction.class, Material.ITEM_FRAME, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM)),
    VAR_SET_ITEM_DISPLAY_NAME(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, SetItemDisplayNameAction.class, Material.NAME_TAG, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("name", ValueType.TEXT)),
    VAR_SET_ITEM_AMOUNT(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, SetItemAmountAction.class, Material.PUMPKIN_SEEDS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("amount", ValueType.NUMBER)),
    VAR_SET_ITEM_LORE(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, SetItemLoreAction.class, Material.WRITABLE_BOOK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("list", ValueType.VARIABLE)),
    VAR_ADD_ITEM_LORE_LINE(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, AddItemLoreLineAction.class, Material.FEATHER, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("line", ValueType.TEXT)),
    VAR_SET_ITEM_LORE_LINE(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, SetItemLoreLineAction.class, Material.CHICKEN_SPAWN_EGG, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("index", ValueType.NUMBER), new ArgumentSlot("line", ValueType.TEXT)),
    VAR_REMOVE_ITEM_LORE_LINE(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, RemoveItemLoreLineAction.class, Material.RED_MUSHROOM, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("index", ValueType.NUMBER)),
    VAR_SET_ITEM_PAGES(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, SetItemPagesAction.class, Material.CRAFTING_TABLE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("list", ValueType.VARIABLE)),
    VAR_SET_ITEM_SKULL_TEXTURES(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, SetSkullTexturesAction.class, Material.PLAYER_HEAD, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("textures", ValueType.TEXT)),
    VAR_SET_ITEM_SKULL_OWNER(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, SetSkullOwnerAction.class, Material.ZOMBIE_HEAD, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("owner", ValueType.TEXT)),
    VAR_SET_CUSTOM_DATA_TO_ITEM(
            ActionCategory.VARIABLE_ACTION,
            MenusCategory.ITEM_OPERATIONS,
            SetCustomDataToItemActon.class,
            Material.EGG,
            new ArgumentSlot("result", ValueType.VARIABLE),
            new ArgumentSlot("item", ValueType.ITEM),
            new ArgumentSlot("key", ValueType.TEXT),
            new ArgumentSlot("value", ValueType.TEXT)
    ),
    VAR_REMOVE_CUSTOM_DATA_FROM_ITEM(
            ActionCategory.VARIABLE_ACTION,
            MenusCategory.ITEM_OPERATIONS,
            RemoveCustomDataFromItemAction.class,
            Material.DISC_FRAGMENT_5,
            new ArgumentSlot("keys", ValueType.TEXT, (byte) 18),
            new ArgumentSlot("result", ValueType.VARIABLE),
            new ArgumentSlot("item", ValueType.ITEM)
    ),
    VAR_GET_CUSTOM_DATA_FROM_ITEM(
            ActionCategory.VARIABLE_ACTION,
            MenusCategory.ITEM_OPERATIONS,
            GetCustomDataFromItemAction.class,
            Material.HOPPER,
            new ArgumentSlot("variable", ValueType.VARIABLE),
            new ArgumentSlot("item", ValueType.ITEM),
            new ArgumentSlot("key", ValueType.TEXT)
    ),
    VAR_CREATE_ITEM_BY_ID(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, GetItemById.class, Material.APPLE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("id", ValueType.TEXT)),
    //VAR_SET_ITEM_PAGE( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, null, Material.CRAFTING_TABLE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("index", ValueType.NUMBER), new ArgumentSlot("page", ValueType.TEXT)),
    VAR_ADD_ITEM_PAGE(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, AddItemPageAction.class, Material.CRAFTING_TABLE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("page", ValueType.TEXT)),
    VAR_SET_ITEM_DAMAGE(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, SetItemDamageAction.class, Material.STICK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("damage", ValueType.NUMBER), new ParameterSlot("add")),
    VAR_SET_ITEM_MAX_DAMAGE(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, SetItemMaxDamageAction.class, Material.WOODEN_PICKAXE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("damage", ValueType.NUMBER), new ParameterSlot("add")),

    VAR_GET_ITEM_DAMAGE(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, GetItemDamageAction.class, Material.REDSTONE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM)),
    VAR_GET_ITEM_MAX_DAMAGE(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, GetItemMaxDamageAction.class, Material.REDSTONE_BLOCK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM)),
    VAR_GET_ITEM_AMOUNT(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, GetItemAmountAction.class, Material.MELON_SEEDS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM)),
    VAR_GET_ITEM_DISPLAY_NAME(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, GetItemDisplayNameAction.class, Material.NAME_TAG, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM)),
    VAR_GET_ITEM_LORE(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, GetItemLoreAction.class, Material.BOOK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM)),
    VAR_GET_ITEM_LORE_LINE(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, GetItemLoreLineAction.class, Material.WRITABLE_BOOK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("index", ValueType.NUMBER)),
    VAR_ADD_ITEM_ENCHANTMENT(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, AddItemEnchantmentAction.class, Material.ENCHANTING_TABLE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("enchantment", ValueType.ITEM), new ArgumentSlot("level", ValueType.NUMBER)),
    VAR_REMOVE_ITEM_ENCHANTMENT(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, RemoveItemEnchantmentAction.class, Material.ENCHANTED_BOOK, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("enchantment", ValueType.ITEM)),
    VAR_CLEAR_ITEM_ENCHANTMENTS(ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, ClearItemEnchantmentsAction.class, Material.GRINDSTONE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM)),

    //VAR_GET_ITEM_PAGES( ActionCategory.VARIABLE_ACTION, MenusCategory.ITEM_OPERATIONS, null, Material.CRAFTING_TABLE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("item", ValueType.ITEM)),

    VAR_MODIFY_VECTOR(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, ModifyVectorAction.class, Material.CYAN_STAINED_GLASS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("vector", ValueType.VECTOR), new ArgumentSlot("x", ValueType.NUMBER), new ArgumentSlot("y", ValueType.NUMBER), new ArgumentSlot("z", ValueType.NUMBER), new ParameterSlot("add")),
    VAR_NORMALIZE_VECTOR(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, NormalizeVectorAction.class, Material.PRISMARINE_BRICKS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("vector", ValueType.VECTOR)),
    VAR_VECTOR_TO_LOCATION(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, VectorToLocationAction.class, Material.PAPER, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("vector", ValueType.VECTOR)),

    VAR_ADD_VECTORS(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, AddVectorsAction.class, Material.BRICK, new ArgumentSlot("vectors", ValueType.VECTOR, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_SUBTRACT_VECTORS(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, SubtractVectorsAction.class, Material.NETHER_BRICK, new ArgumentSlot("vectors", ValueType.VECTOR, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_MULTIPLY_VECTORS(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, MultiplyVectorsAction.class, Material.COPPER_INGOT, new ArgumentSlot("vectors", ValueType.VECTOR, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_MULTIPLY_VECTOR(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, MultiplyVectorAction.class, Material.IRON_INGOT, new ArgumentSlot("vector", ValueType.VECTOR, (byte) 1), new ArgumentSlot("number", ValueType.NUMBER), new ArgumentSlot("variable", ValueType.VARIABLE)),
    VAR_DIVIDE_VECTORS(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, DivideVectorsAction.class, Material.NETHERITE_INGOT, new ArgumentSlot("vectors", ValueType.VECTOR, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),

    VAR_CROSS_PRODUCT_VECTOR(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, CrossProductVectorAction.class, Material.BEETROOT, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("first", ValueType.VECTOR), new ArgumentSlot("second", ValueType.VECTOR)),
    VAR_ANGLE_VECTOR(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, AngleVectorAction.class, Material.HEART_OF_THE_SEA, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("first", ValueType.VECTOR), new ArgumentSlot("second", ValueType.VECTOR)),
    VAR_DOT_VECTOR(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, DotVectorAction.class, Material.ARMADILLO_SCUTE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("first", ValueType.VECTOR), new ArgumentSlot("second", ValueType.VECTOR)),
    VAR_MIDPOINT_VECTOR(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, MidpointVectorAction.class, Material.NETHER_STAR, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("first", ValueType.VECTOR), new ArgumentSlot("second", ValueType.VECTOR)),
    VAR_DISTANCE_VECTOR(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, DistanceVectorAction.class, Material.SPYGLASS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("first", ValueType.VECTOR), new ArgumentSlot("second", ValueType.VECTOR)),

    VAR_GET_VECTOR_X(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, GetVectorXAction.class, Material.RED_STAINED_GLASS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("vector", ValueType.VECTOR)),
    VAR_GET_VECTOR_Y(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, GetVectorYAction.class, Material.GREEN_STAINED_GLASS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("vector", ValueType.VECTOR)),
    VAR_GET_VECTOR_Z(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, GetVectorZAction.class, Material.BLUE_STAINED_GLASS, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("vector", ValueType.VECTOR)),
    VAR_GET_VECTOR_ALL(
            ActionCategory.VARIABLE_ACTION,
            MenusCategory.VECTOR_OPERATIONS,
            GetVectorAllAction.class,
            Material.PRISMARINE_SHARD,
            new ArgumentSlot("target", ValueType.VECTOR),
            new ArgumentSlot("x", ValueType.VARIABLE),
            new ArgumentSlot("y", ValueType.VARIABLE),
            new ArgumentSlot("z", ValueType.VARIABLE)
    ),
    VAR_ROTATION_TO_VECTOR(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, RotationToVectorAction.class, Material.ENDER_EYE,
            new ArgumentSlot("variable", ValueType.VARIABLE),
            new ArgumentSlot("yaw", ValueType.NUMBER),
            new ArgumentSlot("pitch", ValueType.NUMBER)
    ),
    VAR_DO_RAY_TRACE(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, RayTraceVectorAction.class, Material.ARROW,
            new ArgumentSlot("hitVec", ValueType.VARIABLE),
            new ArgumentSlot("vector", ValueType.VECTOR),
            new ArgumentSlot("from", ValueType.LOCATION),
            new ArgumentSlot("to", ValueType.LOCATION),
            new ArgumentSlot("range", ValueType.NUMBER),
            new ArgumentSlot("xSize", ValueType.NUMBER),
            new ArgumentSlot("ySize", ValueType.NUMBER),
            new ArgumentSlot("zSize", ValueType.NUMBER),
            new ParameterSlot("calculation", Arrays.asList("vanilla-java", "optifine-fastmath"), Material.PAPER, Material.MAP)
    ),
    VAR_DO_RAY_TRACE_MULTI(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, RayTraceVectorMultiAction.class, Material.SPECTRAL_ARROW,
            new ArgumentSlot("hitVec", ValueType.VARIABLE),
            new ArgumentSlot("vector", ValueType.VECTOR),
            new ArgumentSlot("from", ValueType.LOCATION),
            new ArgumentSlot("to", ValueType.VARIABLE),
            new ArgumentSlot("range", ValueType.NUMBER),
            new ArgumentSlot("xSize", ValueType.NUMBER),
            new ArgumentSlot("ySize", ValueType.NUMBER),
            new ArgumentSlot("zSize", ValueType.NUMBER),
            new ParameterSlot("calculation", Arrays.asList("vanilla-java", "optifine-fastmath"), Material.PAPER, Material.MAP)
    ),
    VAR_DO_RAY_TRACE_MULTI_ENTITIES(ActionCategory.VARIABLE_ACTION, MenusCategory.VECTOR_OPERATIONS, RayTraceVectorMultiEntitiesAction.class, Material.TIPPED_ARROW,
            new ArgumentSlot("hitVec", ValueType.VARIABLE),
            new ArgumentSlot("vector", ValueType.VECTOR),
            new ArgumentSlot("from", ValueType.LOCATION),
            new ArgumentSlot("range", ValueType.NUMBER),
            new ParameterSlot("filter", Arrays.asList("no-filter", "only-players", "only-entities"), Material.BARRIER, Material.PLAYER_HEAD, Material.ZOMBIE_HEAD),
            new ParameterSlot("calculation", Arrays.asList("vanilla-java", "optifine-fastmath"), Material.PAPER, Material.MAP)
    ),

    /**
     * <h1>Structure Actions.</h1>
     */

    STRUCT_SET_FIELD_VALUE(ActionCategory.STRUCTURE_ACTION, MenusCategory.STRUCTURE_ONLY, SetFieldAction.class, Material.DIAMOND, new ArgumentSlot("field", ValueType.TEXT), new ArgumentSlot("value", ValueType.ANY)),
    STRUCT_SET_STRUCT_VALUE(ActionCategory.STRUCTURE_ACTION, MenusCategory.MANIPULATIONS, SetStructureValueAction.class, Material.DIAMOND, new ArgumentSlot("structure", ValueType.TEXT), new ArgumentSlot("field", ValueType.TEXT), new ArgumentSlot("value", ValueType.ANY)),
    STRUCT_GET_STRUCT_VALUE(ActionCategory.STRUCTURE_ACTION, MenusCategory.MANIPULATIONS, GetStructureValueAction.class, Material.CYAN_DYE, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("structure", ValueType.TEXT), new ArgumentSlot("field", ValueType.TEXT)),


    /**
     * <h1>Selection Actions.</h1>
     */

    SELECTION_SET(ActionCategory.SELECTION_ACTION, MenusCategory.WORLD, SelectionSetTargetAction.class, Material.BARRIER),
    SELECTION_ADD(ActionCategory.SELECTION_ACTION, MenusCategory.WORLD, SelectionAddTargetAction.class, Material.BARRIER),
    SELECTION_REMOVE(ActionCategory.SELECTION_ACTION, MenusCategory.WORLD, SelectionRemoveTargetAction.class, Material.BARRIER),

    /**
     * <h1>Entity Actions.</h1>
     */

    ENTITY_SET_SCALE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, SetScaleAction.class, Material.SHULKER_SHELL, new ArgumentSlot("scale", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_VISUAL_FIRE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetVisualFireAction.class, Material.CAMPFIRE, new ParameterSlot("boolean")),
    ENTITY_SET_GLOWING(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetGlowingAction.class, Material.DRAGON_BREATH, new ParameterSlot("glowing", Arrays.asList(false, true), Material.WHITE_STAINED_GLASS, Material.GLASS)),
    ENTITY_SET_DISGUISE_NAME(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, SetDisguiseNameAction.class, Material.BOOK, new ArgumentSlot("name", ValueType.TEXT)),
    ENTITY_DISGUISE_AS_PLAYER(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, DisguiseAsPlayerAction.class, Material.PLAYER_HEAD, new ArgumentSlot("name", ValueType.TEXT), new ArgumentSlot("skin", ValueType.TEXT)),
    ENTITY_DISGUISE_AS_BLOCK(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, DisguiseAsBlockAction.class, Material.GRASS_BLOCK, new ArgumentSlot("block", ValueType.ITEM)),
    ENTITY_DISGUISE_AS_ENTITY(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, DisguiseAsEntityAction.class, Material.PIGLIN_HEAD, new ArgumentSlot("type", ValueType.ITEM)),
    ENTITY_CLEAR_DISGUISE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, ClearDisguiseAction.class, Material.MILK_BUCKET),

    ENTITY_SET_ARMOR_STAND_POSE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, SetArmorStandPoseAction.class, Material.ARMOR_STAND, new ArgumentSlot("x", ValueType.NUMBER), new ArgumentSlot("y", ValueType.NUMBER), new ArgumentSlot("z", ValueType.NUMBER)),
    ENTITY_SET_CAT_LYING_DOWN(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetCatLyingDownAction.class, Material.CAT_SPAWN_EGG, new ParameterSlot("boolean")),
    ENTITY_SET_FOX_SLEEPING(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetFoxSleepingAction.class, Material.FOX_SPAWN_EGG, new ParameterSlot("boolean")),
    ENTITY_SET_FOX_LEAPING(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetFoxLeapingAction.class, Material.FOX_SPAWN_EGG, new ParameterSlot("boolean")),
    ENTITY_SET_SNOWMAN_PUMPKIN(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetSnowGolemPumpkinAction.class, Material.CARVED_PUMPKIN, new ParameterSlot("boolean")),
    ENTITY_SET_BEAR_STANDING(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetBearStandingAction.class, Material.POLAR_BEAR_SPAWN_EGG, new ParameterSlot("boolean")),
    ENTITY_SET_ALLAY_DANCING(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetAllayDancingAction.class, Material.ALLAY_SPAWN_EGG, new ParameterSlot("boolean"), new ArgumentSlot("jukebox", ValueType.LOCATION)),
    ENTITY_SET_SCREAMING(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetScreamingAction.class, Material.ENDERMAN_SPAWN_EGG, new ParameterSlot("boolean")),
    ENTITY_SET_GOAT_HORNS(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetGoatHornsAction.class, Material.GOAT_HORN, new ParameterSlot("left"), new ParameterSlot("right")),
    ENTITY_SET_FOX_FACEPLANTED(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetFoxFaceplantedAction.class, Material.FOX_SPAWN_EGG, new ParameterSlot("boolean")),
    ENTITY_SET_FOX_DEFENDING(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetFoxDefendingAction.class, Material.FOX_SPAWN_EGG, new ParameterSlot("boolean")),
    ENTITY_SET_FOX_CROUCHING(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetFoxCrouchingAction.class, Material.FOX_SPAWN_EGG, new ParameterSlot("boolean")),
    ENTITY_VILLAGER_SHAKE_HEAD(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntityVillagerShakeHeadAction.class, Material.VILLAGER_SPAWN_EGG),
    ENTITY_SET_PANDA_ON_BACK(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetPandaOnBackAction.class, Material.PANDA_SPAWN_EGG, new ParameterSlot("boolean")),
    ENTITY_SET_SHEEP_COLOR(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetSheepColorAction.class, Material.SHEEP_SPAWN_EGG, new ParameterSlot("color", List.of("white", "light-gray", "gray", "black", "red", "orange", "yellow", "lime", "green", "light-blue", "cyan", "blue", "purple", "magenta", "pink", "brown"), Material.WHITE_DYE, Material.LIGHT_GRAY_DYE, Material.GRAY_DYE, Material.BLACK_DYE, Material.RED_DYE, Material.ORANGE_DYE, Material.YELLOW_DYE, Material.LIME_DYE, Material.GREEN_DYE, Material.LIGHT_BLUE_DYE, Material.CYAN_DYE, Material.BLUE_DYE, Material.PURPLE_DYE, Material.MAGENTA_DYE, Material.PINK_DYE, Material.BROWN_DYE)),
    ENTITY_SET_PARROT_COLOR(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetParrotColorAction.class, Material.PARROT_SPAWN_EGG, new ParameterSlot("color", List.of("red", "blue", "green", "cyan", "gray"), Material.RED_DYE, Material.BLUE_DYE, Material.GREEN_DYE, Material.CYAN_DYE, Material.GRAY_DYE)),
    ENTITY_SET_DOG_COLLAR_COLOR(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetDogCollarColorAction.class, Material.WOLF_SPAWN_EGG, new ParameterSlot("color", List.of("white", "light-gray", "gray", "black", "red", "orange", "yellow", "lime", "green", "light-blue", "cyan", "blue", "purple", "magenta", "pink", "brown"), Material.WHITE_DYE, Material.LIGHT_GRAY_DYE, Material.GRAY_DYE, Material.BLACK_DYE, Material.RED_DYE, Material.ORANGE_DYE, Material.YELLOW_DYE, Material.LIME_DYE, Material.GREEN_DYE, Material.LIGHT_BLUE_DYE, Material.CYAN_DYE, Material.BLUE_DYE, Material.PURPLE_DYE, Material.MAGENTA_DYE, Material.PINK_DYE, Material.BROWN_DYE)),
    ENTITY_SET_PIGLIN_DANCING(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetPiglinDancingAction.class, Material.PIGLIN_HEAD, new ParameterSlot("boolean"), new ArgumentSlot("time", ValueType.NUMBER)),
    ENTITY_SET_CELEBRATING(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_APPEARANCE, EntitySetPillagerCelebratingAction.class, Material.PILLAGER_SPAWN_EGG, new ParameterSlot("boolean")),

    ENTITY_SET_CUSTOM_NAME(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntitySetNameAction.class, Material.NAME_TAG, new ArgumentSlot("name", ValueType.TEXT, (byte) 18)),
    ENTITY_ADD_DAMAGE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, DamageEntityAction.class, Material.NETHERITE_SWORD, new ArgumentSlot("damage", ValueType.NUMBER)),
    ENTITY_SET_HEALTH(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntitySetHealthAction.class, Material.APPLE, new ArgumentSlot("health", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_WALK_SPEED(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntitySetWalkSpeedAction.class, Material.CHAINMAIL_BOOTS, new ArgumentSlot("speed", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_MAX_HEALTH(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntitySetMaxHealthAction.class, Material.GOLDEN_APPLE, new ArgumentSlot("health", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_FIRE_TICKS(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntitySetFireTicksAction.class, Material.CAMPFIRE, new ArgumentSlot("ticks", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_FREEZE_TICKS(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntitySetFreezeTicksAction.class, Material.ICE, new ArgumentSlot("ticks", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_NO_DAMAGE_TICKS(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntitySetNoDamageTicksAction.class, Material.TOTEM_OF_UNDYING, new ArgumentSlot("ticks", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_GIVE_POTION_EFFECTS(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntityGivePotionEffectsAction.class, Material.POTION, new ArgumentSlot("potions", ValueType.POTION, (byte) 18), new ParameterSlot("replace")),
    ENTITY_CLEAR_POTION_EFFECTS(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntityClearPotionEffectsAction.class, Material.MILK_BUCKET),
    ENTITY_REMOVE_POTION_EFFECTS(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntityRemovePotionEffectsAction.class, Material.GLASS_BOTTLE, new ArgumentSlot("potions", ValueType.POTION, (byte) 27)),
    ENTITY_SET_STEP_HEIGHT(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, SetStepHeightAction.class, Material.RABBIT_FOOT, new ArgumentSlot("height", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_TARGET(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, SetEntityTargetAction.class, Material.NETHER_STAR, new ArgumentSlot("entity", ValueType.TEXT)),
    ENTITY_SET_REMAINING_AIR(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntitySetRemainingAirAction.class, Material.SPONGE, new ArgumentSlot("ticks", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_FALL_DISTANCE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntitySetFallDistanceAction.class, Material.RABBIT_FOOT, new ArgumentSlot("distance", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_LAST_DAMAGE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntitySetLastDamageAction.class, Material.REDSTONE, new ArgumentSlot("damage", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_CAN_PICKUP_ITEM(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntitySetCanPickupItemAction.class, Material.GLOWSTONE_DUST, new ParameterSlot("boolean")),
    ENTITY_SET_ARROWS_IN_BODY(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntitySetArrowsInBodyAction.class, Material.ARROW, new ArgumentSlot("count", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_BEE_STINGERS_IN_BODY(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntitySetBeeStingersInBodyAction.class, Material.BEE_SPAWN_EGG, new ArgumentSlot("count", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_SHIELD_BLOCKING_DELAY(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntitySetShieldBlockingDelay.class, Material.SHIELD, new ArgumentSlot("delay", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_BEE_STINGER_COOLDOWN(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntitySetBeeStingerCooldownAction.class, Material.BEE_NEST, new ArgumentSlot("cooldown", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_MAXIMUM_NO_DAMAGE_TICKS(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntitySetMaxNoDamageTicksAction.class, Material.ENCHANTED_GOLDEN_APPLE, new ArgumentSlot("ticks", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_MAXIMUM_AIR(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, EntitySetMaximumAirAction.class, Material.POTION, new ArgumentSlot("ticks", ValueType.NUMBER), new ParameterSlot("add")),

    ENTITY_GIVE_ITEMS(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_INVENTORY, EntityGiveItemsAction.class, Material.CHEST_MINECART, new ArgumentSlot("items", ValueType.ITEM, (byte) 27)),
    ENTITY_SET_ITEMS(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_INVENTORY, EntitySetItemsAction.class, Material.DARK_OAK_CHEST_BOAT, new ArgumentSlot("items", ValueType.ITEM, (byte) 27, true)),
    ENTITY_SET_ARMOR(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_INVENTORY, EntitySetArmorAction.class, Material.NETHERITE_CHESTPLATE, new ArgumentSlot("helmet", ValueType.ITEM), new ArgumentSlot("chestplate", ValueType.ITEM), new ArgumentSlot("leggings", ValueType.ITEM), new ArgumentSlot("boots", ValueType.ITEM), new ParameterSlot("replace-with-air")),
    ENTITY_CLEAR_INVENTORY(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_INVENTORY, EntityClearInventoryAction.class, Material.BUCKET),
    ENTITY_SET_ITEM_IN_HAND(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_INVENTORY, EntitySetItemInHandAction.class, Material.NETHERITE_SWORD, new ArgumentSlot("main", ValueType.ITEM), new ParameterSlot("replace-with-air"), new ArgumentSlot("off", ValueType.ITEM)),
    ENTITY_GET_ITEM_BY_SLOT(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_INVENTORY, EntityGetItemAction.class, Material.MAGMA_CREAM, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("slot", ValueType.NUMBER)),
    ENTITY_REMOVE_ITEMS(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_INVENTORY, EntityRemoveItemsAction.class, Material.COBWEB, new ArgumentSlot("items", ValueType.ITEM, (byte) 27)),

    ENTITY_REMOVE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, RemoveEntityAction.class, Material.BARRIER),
    ENTITY_SET_TEAM(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, SetTeamAction.class, Material.LIME_BANNER, new ArgumentSlot("scoreboard", ValueType.TEXT), new ArgumentSlot("team", ValueType.TEXT)),
    ENTITY_UNSET_TEAM(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, UnsetTeamAction.class, Material.RED_BANNER, new ArgumentSlot("scoreboard", ValueType.TEXT), new ArgumentSlot("team", ValueType.TEXT)),
    ENTITY_SET_FRICTION(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetFrictionAction.class, Material.ICE, new ParameterSlot("friction", List.of("not-set", "true", "false"), Material.LIGHT_BLUE_DYE, Material.GREEN_DYE, Material.RED_DYE)),
    ENTITY_SET_LEASH_HOLDER(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetLeashHolderAction.class, Material.LEAD, new ArgumentSlot("entity", ValueType.TEXT)),
    ENTITY_REMOVE_PASSENGER(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntityRemovePassengerAction.class, Material.MINECART, new ArgumentSlot("entity", ValueType.TEXT)),
    ENTITY_SET_DESPAWNING(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetDespawningAction.class, Material.BARRIER, new ParameterSlot("boolean")),
    ENTITY_SET_AGGRESSIVE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetAggressiveAction.class, Material.NETHERITE_SWORD, new ParameterSlot("boolean")),

    ENTITY_SET_DISPLAY_BILLBOARD(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetDisplayBillboardAction.class, Material.ENDER_EYE, new ParameterSlot("billboard", Arrays.asList("center", "fixed", "horizontal", "vertical"), Material.ENDER_EYE, Material.ENDER_PEARL, Material.SLIME_BALL, Material.SNOWBALL)),
    ENTITY_SET_DISPLAY_SCALE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetDisplayScaleAction.class, Material.PAPER, new ArgumentSlot("x", ValueType.NUMBER), new ArgumentSlot("y", ValueType.NUMBER), new ArgumentSlot("z", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_DISPLAY_TRANSLATION(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetDisplayTranslationAction.class, Material.ARROW, new ArgumentSlot("x", ValueType.NUMBER), new ArgumentSlot("y", ValueType.NUMBER), new ArgumentSlot("z", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_DISPLAY_ITEM(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetDisplayItemAction.class, Material.CRAFTING_TABLE, new ArgumentSlot("item", ValueType.ITEM)),
    ENTITY_RELEASE_SHOULDERS(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntityReleaseShouldersAction.class, Material.PARROT_SPAWN_EGG, new ParameterSlot("type", List.of("all", "left", "right"), Material.PARROT_SPAWN_EGG, Material.SHIELD, Material.NETHERITE_SWORD)),

    //ENTITY_SET_DISPLAY_ITEM_MODE(       ActionCategory.ENTITY_ACTION, MenusCategory.STATE, null, Material.LECTERN, new ParameterSlot("mode", Arrays.asList("fixed","none","ground","gui","head","firstperson-lefthand","firstperson-righthand","thirdperson-lefthand","thirdperson-righthand"), Material.ENDER_EYE, Material.STRUCTURE_VOID, Material.GRASS_BLOCK, Material.CHEST, Material.PLAYER_HEAD, Material.TRIPWIRE_HOOK, Material.TRIPWIRE_HOOK, Material.LEVER, Material.LEVER)),
    //ENTITY_SET_TEXT_DISPLAY_BACKGROUND( ActionCategory.ENTITY_ACTION, MenusCategory.STATE, null, Material.GRAY_STAINED_GLASS, new ArgumentSlot("color",ValueType.COLOR), new ArgumentSlot("opacity",ValueType.NUMBER)),
    //ENTITY_RESET_TEXT_DISPLAY_BACKGROUND(ActionCategory.ENTITY_ACTION, MenusCategory.STATE, null, Material.LIGHT_GRAY_STAINED_GLASS),
    ENTITY_SET_BEE_NECTAR(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetBeeNectarAction.class, Material.HONEYCOMB, new ParameterSlot("boolean")),
    ENTITY_SET_BEE_STUNG(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetBeeHasStungAction.class, Material.BEE_SPAWN_EGG, new ParameterSlot("boolean")),

    ENTITY_SET_CARRYING_CHEST(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetHasChestAction.class, Material.CHEST, new ParameterSlot("boolean")),
    ENTITY_SET_AWARE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetAwareAction.class, Material.LIGHT, new ParameterSlot("boolean")),
    ENTITY_SET_CAMEL_DASHING(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetCamelDashingAction.class, Material.CAMEL_SPAWN_EGG, new ParameterSlot("boolean")),
    //ENTITY_SET_RIPTIDING(         ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                               Material.TRIDENT,               new ParameterSlot("boolean")),
    ENTITY_SET_CHARGED(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetChargedAction.class, Material.CREEPER_SPAWN_EGG, new ParameterSlot("boolean")),
    ENTITY_SET_BREED(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetBreedAction.class, Material.CARROT, new ParameterSlot("boolean")),
    ENTITY_SET_AGE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetAgeAction.class, Material.EGG, new ArgumentSlot("age", ValueType.NUMBER), new ParameterSlot("add"), new ParameterSlot("lock")),
    ENTITY_SET_HAND(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetHandAction.class, Material.TRIPWIRE_HOOK, new ParameterSlot("hand", List.of("right", "left"), Material.NETHERITE_SWORD, Material.SHIELD)),
    ENTITY_SET_GUARDIAN_LASER(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetGuardianLaserAction.class, Material.GUARDIAN_SPAWN_EGG, new ParameterSlot("boolean")),
    ENTITY_ENDERMAN_TELEPORT(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntityEndermanTeleportAction.class, Material.ENDERMAN_SPAWN_EGG),
    //ENTITY_SET_LLAMA_COLOR(         ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                     Material.LLAMA_SPAWN_EGG,       new ParameterSlot("color", List.of("brown","creamy","gray","white"), Material.BROWN_DYE, Material.YELLOW_DYE, Material.GRAY_DYE, Material.WHITE_DYE)),
    //ENTITY_SET_LLAMA_STRENGTH(      ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_PARAMS, null,                    Material.LLAMA_SPAWN_EGG,       new ArgumentSlot("strength", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_SET_EATING(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetEatingAction.class, Material.PANDA_SPAWN_EGG, new ParameterSlot("boolean")),
    ENTITY_SET_INTERESTED(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetInterestedAction.class, Material.BONE, new ParameterSlot("boolean")),
    ENTITY_SET_HORSE_REARING(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetHorseRearingAction.class, Material.PANDA_SPAWN_EGG, new ParameterSlot("boolean")),
    ENTITY_EXPLOSIVE_IGNITE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntityExplosiveIgniteAction.class, Material.FLINT_AND_STEEL, new ParameterSlot("boolean")),
    ENTITY_EXPLODE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntityExplodeAction.class, Material.TNT, new ParameterSlot("boolean")),
    ENTITY_SET_BAT_AWAKE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetBatAwakeAction.class, Material.BAT_SPAWN_EGG, new ParameterSlot("boolean")),
    ENTITY_SET_HOOK_APPLY_LURE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetHookApplyLureAction.class, Material.FISHING_ROD, new ParameterSlot("boolean")),
    ENTITY_SET_AXOLOTL_PLAYING_DEAD(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetAxolotlPlayingDeadAction.class, Material.AXOLOTL_SPAWN_EGG, new ParameterSlot("boolean")),
    //ENTITY_HEAL_ZOMBIE_VILLAGER(    ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                               Material.ZOMBIE_VILLAGER_SPAWN_EGG, new ArgumentSlot("time", ValueType.NUMBER)),
    //ENTITY_SLEEP(                   ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                               Material.RED_BED,               new ArgumentSlot("bed", ValueType.LOCATION)),
    //ENTITY_WAKE_UP(                 ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                               Material.GREEN_BED),
    //ENTITY_SET_VILLAGER_LEVEL(      ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                               Material.EMERALD_BLOCK,         new ArgumentSlot("level", ValueType.NUMBER), new ParameterSlot("add")),
    //ENTITY_SET_VILLAGER_EXPERIENCE( ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                               Material.EMERALD,               new ArgumentSlot("experience", ValueType.NUMBER), new ParameterSlot("add")),
    //ENTITY_SET_VILLAGER_RESTOCKS(   ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                               Material.VILLAGER_SPAWN_EGG,    new ArgumentSlot("restocks", ValueType.NUMBER), new ParameterSlot("add")),
    ENTITY_VILLAGER_ZOMBIFY(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntityVillagerZombifyAction.class, Material.ZOMBIE_HEAD),
    //ENTITY_SET_ITEM_PICKUP(         ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                               Material.GLOWSTONE_DUST,        new ParameterSlot("mob"), new ParameterSlot("player")),
    //ENTITY_SET_ITEM_PICKUP_DELAY(   ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                               Material.GUNPOWDER,             new ArgumentSlot("delay", ValueType.NUMBER), new ParameterSlot("add")),
    //ENTITY_SET_SHULKER_PEEK(        ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                               Material.SHULKER_BOX,           new ArgumentSlot("peek", ValueType.NUMBER), new ParameterSlot("add")),

    ENTITY_SET_AI(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetAIAction.class, Material.FERMENTED_SPIDER_EYE, new ParameterSlot("boolean")),
    ENTITY_SET_BABY(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetBabyAction.class, Material.EGG),
    ENTITY_SET_ADULT(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetAdultAction.class, Material.CHICKEN),
    ENTITY_SET_PHYSICS(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetPhysicsAction.class, Material.CHIPPED_ANVIL, new ParameterSlot("boolean")),
    ENTITY_SET_COLLIDABLE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetCollidableAction.class, Material.SHIELD, new ParameterSlot("boolean")),
    ENTITY_SET_SILENT(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetSilentAction.class, Material.NAUTILUS_SHELL, new ParameterSlot("boolean")),

    ENTITY_SET_SITTING(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetSittingAction.class, Material.WOLF_SPAWN_EGG, new ParameterSlot("boolean")),
    ENTITY_SET_SADDLE(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetSaddleAction.class, Material.SADDLE, new ParameterSlot("boolean")),
    ENTITY_SET_MINECART_BLOCK(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetMinecartBlockAction.class, Material.HOPPER_MINECART, new ArgumentSlot("block", ValueType.ITEM)),
    ENTITY_SET_PIGLIN_CROSSBOW(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetPiglinChargingAction.class, Material.CROSSBOW, new ParameterSlot("boolean")),
    //ENTITY_SET_TURTLE_HOME(         ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                               Material.TURTLE_SPAWN_EGG,      new ArgumentSlot("location", ValueType.LOCATION)),
    //ENTITY_SET_BEE_HIVE(            ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                               Material.BEEHIVE,               new ArgumentSlot("location", ValueType.LOCATION)),
    //ENTITY_SET_BEE_FLOWER(          ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                               Material.ROSE_BUSH,             new ArgumentSlot("location", ValueType.LOCATION)),
    //ENTITY_SET_DOLPHIN_TREASURE(    ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                               Material.DOLPHIN_SPAWN_EGG,     new ArgumentSlot("location", ValueType.LOCATION)),
    //ENTITY_SET_ENDER_CRYSTAL_BEAM(  ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                               Material.TURTLE_SPAWN_EGG,      new ArgumentSlot("location", ValueType.LOCATION)),
    //ENTITY_SET_WANDERING_TOWARDS(   ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                               Material.WANDERING_TRADER_SPAWN_EGG, new ArgumentSlot("location", ValueType.LOCATION)),
    ENTITY_SET_WANDERING_DRINK_MILK(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetWanderingDrinkMilkAction.class, Material.MILK_BUCKET, new ParameterSlot("boolean")),
    ENTITY_SET_WANDERING_DRINK_POTION(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetWanderingDrinkPotionAction.class, Material.POTION, new ParameterSlot("boolean")),
    //ENTITY_SET_SLIME_SIZE(          ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, null,                               Material.SLIME_BLOCK,           new ArgumentSlot("size", ValueType.NUMBER), new ParameterSlot("add")),


    ENTITY_SET_TEXT_DISPLAY_SEE_THROUGH(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetDisplaySeeThroughAction.class, Material.SPECTRAL_ARROW, new ParameterSlot("see")),
    ENTITY_SET_TEXT_DISPLAY_SHADOW(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetDisplayShadowedAction.class, Material.NAME_TAG, new ParameterSlot("shadow")),
    //ENTITY_SET_TEXT_DISPLAY_ALIGN(      ActionCategory.ENTITY_ACTION, MenusCategory.STATE, null, Material.MAGENTA_TERRACOTTA, new ParameterSlot("align", Arrays.asList("center","left","right"), Material.ENDER_EYE, Material.SHIELD, Material.DIAMOND_SWORD)),
    ENTITY_SET_TEXT_DISPLAY_OPACITY(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetDisplayTextOpacityAction.class, Material.BIRCH_SIGN, new ArgumentSlot("opacity", ValueType.NUMBER)),
    ENTITY_SET_TEXT_DISPLAY_WIDTH(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetDisplayTextWidthAction.class, Material.OAK_SIGN, new ArgumentSlot("width", ValueType.NUMBER)),
    ENTITY_SET_DISPLAY_TEXT(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetDisplayTextAction.class, Material.BOOK, new ArgumentSlot("text", ValueType.TEXT)),
    ENTITY_SET_DISPLAY_BRIGHTNESS(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_STATE, EntitySetDisplayLightningAction.class, Material.LIGHT, new ArgumentSlot("light", ValueType.NUMBER)),
    //ENTITY_RESET_DISPLAY_BRIGHTNESS(ActionCategory.ENTITY_ACTION, MenusCategory.STATE, null, Material.GLASS_BOTTLE),

    ENTITY_TELEPORT(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_MOVEMENT, EntityTeleportAction.class, Material.ENDER_PEARL, new ArgumentSlot("location", ValueType.LOCATION), new ParameterSlot("consider", Arrays.asList("all", "only-coordinates", "only-rotation"), Material.ENDER_EYE, Material.PAPER, Material.PLAYER_HEAD)),
    ENTITY_PATH_MOVE_TO_LOCATION(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_MOVEMENT, SetEntityPathMoveToLocationAction.class, Material.PAPER, new ArgumentSlot("location", ValueType.LOCATION)),
    ENTITY_SADDLE_ENTITY(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_MOVEMENT, EntitySaddleEntityAction.class, Material.SADDLE, new ArgumentSlot("entity", ValueType.TEXT)),
    ENTITY_LAUNCH_VERTICAL(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_MOVEMENT, EntityLaunchVerticalAction.class, Material.PRISMARINE_SHARD, new ArgumentSlot("power", ValueType.NUMBER)),
    ENTITY_LAUNCH_HORIZONTAL(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_MOVEMENT, EntityLaunchHorizontalAction.class, Material.FEATHER, new ArgumentSlot("power", ValueType.NUMBER)),
    ENTITY_LAUNCH_TO_LOCATION(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_MOVEMENT, EntityLaunchToLocationAction.class, Material.MAP, new ArgumentSlot("location", ValueType.LOCATION), new ArgumentSlot("power", ValueType.NUMBER)),
    ENTITY_SET_VELOCITY(ActionCategory.ENTITY_ACTION, MenusCategory.ENTITY_MOVEMENT, SetVelocityAction.class, Material.PRISMARINE_SHARD, new ArgumentSlot("vector", ValueType.VECTOR)),

    /**
     * <h1>Other Actions.</h1>
     */

    CONTROLLER_CATCH_ERROR(ActionCategory.CONTROLLER_ACTION, MenusCategory.CONTROLLER, CatchErrorAction.class, Material.RED_DYE, new ArgumentSlot("variable", ValueType.VARIABLE)),
    CONTROLLER_MEASURE_TIME(ActionCategory.CONTROLLER_ACTION, MenusCategory.CONTROLLER, MeasureTimeAction.class, Material.CLOCK, new ArgumentSlot("variable", ValueType.VARIABLE)),

    REPEAT_ALWAYS(ActionCategory.REPEAT_ACTION, MenusCategory.REPEATS, RepeatAlwaysAction.class, Material.BEACON),
    REPEAT_WHILE(ActionCategory.REPEAT_ACTION, MenusCategory.REPEATS, RepeatWhileConditionAction.class, Material.IRON_INGOT),
    REPEAT_WHILE_NOT(ActionCategory.REPEAT_ACTION, MenusCategory.REPEATS, RepeatWhileConditionNotAction.class, Material.GOLD_INGOT),
    REPEAT_FOR_NUMBERS(ActionCategory.REPEAT_ACTION, MenusCategory.REPEATS, RepeatForLoopAction.class, Material.SLIME_BALL, new ArgumentSlot("variable", ValueType.VARIABLE), new ParameterSlot("type", Arrays.asList("less", "less-equals", "greater", "greater-equals"), Material.BRICK, Material.BRICKS, Material.NETHER_BRICK, Material.NETHER_BRICKS), new ArgumentSlot("range", ValueType.NUMBER), new ArgumentSlot("add", ValueType.NUMBER)),
    REPEAT_FOR_LIST(ActionCategory.REPEAT_ACTION, MenusCategory.REPEATS, RepeatForEachAction.class, Material.BOOKSHELF, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("list", ValueType.VARIABLE)),
    REPEAT_FOR_ENTRY(ActionCategory.REPEAT_ACTION, MenusCategory.REPEATS, RepeatForEntryAction.class, Material.CHEST_MINECART, new ArgumentSlot("key", ValueType.VARIABLE), new ArgumentSlot("value", ValueType.VARIABLE), new ArgumentSlot("map", ValueType.VARIABLE)),
    REPEAT_FOR_BLOCKS(ActionCategory.REPEAT_ACTION, MenusCategory.REPEATS, RepeatForBlocksAction.class, Material.PAPER, new ArgumentSlot("variable", ValueType.VARIABLE), new ArgumentSlot("first", ValueType.LOCATION), new ArgumentSlot("second", ValueType.LOCATION)),

    LAUNCH_FUNCTION(ActionCategory.LAUNCH_FUNCTION_ACTION, MenusCategory.OTHER, LaunchFunctionAction.class, Material.LAPIS_ORE),
    LAUNCH_METHOD(ActionCategory.LAUNCH_METHOD_ACTION, MenusCategory.OTHER, LaunchMethodAction.class, Material.EMERALD),

    /**
     * <h1>Variable Conditions.</h1>
     */

    IF_VAR_EQUALS(ActionCategory.VARIABLE_CONDITION, MenusCategory.OTHER, VariableEqualsCondition.class, Material.NETHERITE_INGOT, new ArgumentSlot("values", ValueType.ANY, (byte) 18), new ArgumentSlot("variable", ValueType.VARIABLE)),
    IF_VAR_IS_NULL(ActionCategory.VARIABLE_CONDITION, MenusCategory.OTHER, VariableIsNullCondition.class, Material.STRUCTURE_VOID, new ArgumentSlot("values", ValueType.ANY, (byte) 18), new ParameterSlot("all")),
    IF_VAR_EXISTS(ActionCategory.VARIABLE_CONDITION, MenusCategory.OTHER, VariableExistsCondition.class, Material.MAGMA_CREAM, new ArgumentSlot("variables", ValueType.VARIABLE, (byte) 18), new ParameterSlot("all")),
    IF_VAR_ITEM_EQUALS(ActionCategory.VARIABLE_CONDITION, MenusCategory.OTHER, VarItemEqualsCondition.class, Material.CRAFTING_TABLE, new ArgumentSlot("items", ValueType.ITEM, (byte) 18), new ParameterSlot("ignore-amount", Material.BEETROOT_SEEDS, Material.OAK_BUTTON), new ParameterSlot("ignore-name", Material.NAME_TAG, Material.STRING), new ParameterSlot("ignore-lore", Material.WRITABLE_BOOK, Material.COBWEB), new ArgumentSlot("item", ValueType.ITEM), new ParameterSlot("ignore-enchantments", Material.ENCHANTED_BOOK, Material.BOOK), new ParameterSlot("ignore-flags", Material.BLUE_BANNER, Material.WHITE_BANNER), new ParameterSlot("ignore-material", Material.CRAFTING_TABLE, Material.WHITE_STAINED_GLASS), new ParameterSlot("ignore-damage", Material.DEAD_BUSH, Material.GOLDEN_HOE)),
    IF_VAR_LOCATION_IN_AREA(ActionCategory.VARIABLE_CONDITION, MenusCategory.OTHER, LocationInAreaCondition.class, Material.HONEY_BLOCK, new ArgumentSlot("first", ValueType.LOCATION), new ArgumentSlot("location", ValueType.LOCATION), new ArgumentSlot("second", ValueType.LOCATION)),
    IF_VAR_ITEM_HAS_ENCHANTMENTS(ActionCategory.VARIABLE_CONDITION, MenusCategory.OTHER, VarItemHasEnchantments.class, Material.ENCHANTED_BOOK, new ArgumentSlot("item", ValueType.ITEM), new ArgumentSlot("enchantment", ValueType.ITEM), new ParameterSlot("all"), new ParameterSlot("level-check", Arrays.asList("exact", "min-level", "max-level", "ignore"), Material.LECTERN, Material.GLOW_ITEM_FRAME, Material.ITEM_FRAME, Material.PAINTING)),
    IF_VAR_ITEM_HAS_CUSTOM_KEY(
            ActionCategory.VARIABLE_CONDITION,
            MenusCategory.OTHER,
            VarItemHasCustomKey.class,
            Material.NAME_TAG,
            new ArgumentSlot("keys", ValueType.TEXT, (byte) 18),
            new ArgumentSlot("item", ValueType.ITEM)
    ),
    IF_VAR_ITEM_HAS_CUSTOM_DATA(
            ActionCategory.VARIABLE_CONDITION,
            MenusCategory.OTHER,
            VarItemHasCustomData.class,
            Material.FLOWER_POT,
            new ArgumentSlot("values", ValueType.TEXT, (byte) 18),
            new ArgumentSlot("item", ValueType.ITEM),
            new ArgumentSlot("key", ValueType.TEXT)
    ),
    IF_VAR_BOOLEAN_IS_TRUE(ActionCategory.VARIABLE_CONDITION, MenusCategory.OTHER, BooleanIsTrueCondition.class, Material.CLOCK, new ArgumentSlot("booleans", ValueType.BOOLEAN, (byte) 18), new ParameterSlot("all")),

    IF_VAR_TEXT_EQUALS(ActionCategory.VARIABLE_CONDITION, MenusCategory.TEXT_OPERATIONS, TextEqualsCondition.class, Material.BOOK, new ArgumentSlot("content", ValueType.TEXT, (byte) 18), new ArgumentSlot("text", ValueType.TEXT), new ParameterSlot("ignore-caps"), new ParameterSlot("ignore-colors")),
    IF_VAR_TEXT_CONTAINS(ActionCategory.VARIABLE_CONDITION, MenusCategory.TEXT_OPERATIONS, TextContainsCondition.class, Material.LECTERN, new ArgumentSlot("contains", ValueType.TEXT, (byte) 18), new ArgumentSlot("text", ValueType.TEXT), new ParameterSlot("ignore-caps"), new ParameterSlot("ignore-colors")),
    IF_VAR_TEXT_STARTS_WITH(ActionCategory.VARIABLE_CONDITION, MenusCategory.TEXT_OPERATIONS, TextStartsWithCondition.class, Material.OAK_DOOR, new ArgumentSlot("start", ValueType.TEXT, (byte) 18), new ArgumentSlot("text", ValueType.TEXT), new ParameterSlot("ignore-caps"), new ParameterSlot("ignore-colors")),
    IF_VAR_TEXT_ENDS_WITH(ActionCategory.VARIABLE_CONDITION, MenusCategory.TEXT_OPERATIONS, TextEndsWithCondition.class, Material.DARK_OAK_DOOR, new ArgumentSlot("ending", ValueType.TEXT, (byte) 18), new ArgumentSlot("text", ValueType.TEXT), new ParameterSlot("ignore-caps"), new ParameterSlot("ignore-colors")),

    IF_VAR_NUMBER_GREATER(ActionCategory.VARIABLE_CONDITION, MenusCategory.NUMBER_OPERATIONS, NumberGreaterCondition.class, Material.NETHERITE_INGOT, new ArgumentSlot("first", ValueType.NUMBER), new ParameterSlot("equals", Material.BRICK, Material.BRICKS), new ArgumentSlot("second", ValueType.NUMBER)),
    IF_VAR_NUMBER_LESS(ActionCategory.VARIABLE_CONDITION, MenusCategory.NUMBER_OPERATIONS, NumberLessCondition.class, Material.COPPER_INGOT, new ArgumentSlot("first", ValueType.NUMBER), new ParameterSlot("equals", Material.NETHER_BRICK, Material.NETHER_BRICKS), new ArgumentSlot("second", ValueType.NUMBER)),
    IF_VAR_NUMBER_IN_RANGE(ActionCategory.VARIABLE_CONDITION, MenusCategory.NUMBER_OPERATIONS, NumberInRangeCondition.class, Material.IRON_INGOT, new ArgumentSlot("min", ValueType.NUMBER), new ParameterSlot("min-equals", Material.NETHER_BRICK, Material.NETHER_BRICKS), new ArgumentSlot("number", ValueType.NUMBER), new ParameterSlot("max-equals", Material.BRICK, Material.BRICKS), new ArgumentSlot("max", ValueType.NUMBER)),

    IF_VAR_LIST_IS_EMPTY(ActionCategory.VARIABLE_CONDITION, MenusCategory.LIST_OPERATIONS, ListIsEmptyCondition.class, Material.STRUCTURE_VOID, new ArgumentSlot("list", ValueType.VARIABLE)),
    IF_VAR_LIST_CONTAINS(ActionCategory.VARIABLE_CONDITION, MenusCategory.LIST_OPERATIONS, ListContainsCondition.class, Material.CHEST_MINECART, new ArgumentSlot("elements", ValueType.ANY, (byte) 18), new ArgumentSlot("list", ValueType.VARIABLE), new ParameterSlot("all")),

    IF_VAR_LIST_VALUE_AT_FIRST_EQUALS(
            ActionCategory.VARIABLE_CONDITION,
            MenusCategory.LIST_OPERATIONS,
            ListValueAtFirstEqualsCondition.class,
            Material.COMPARATOR,
            new ArgumentSlot("values", ValueType.ANY, (byte) 18),
            new ArgumentSlot("list", ValueType.VARIABLE)
    ),
    IF_VAR_LIST_VALUE_AT_INDEX_EQUALS(
            ActionCategory.VARIABLE_CONDITION,
            MenusCategory.LIST_OPERATIONS,
            ListValueAtIndexEqualsCondition.class,
            Material.RAIL,
            new ArgumentSlot("values", ValueType.ANY, (byte) 18),
            new ArgumentSlot("list", ValueType.VARIABLE),
            new ArgumentSlot("index", ValueType.NUMBER)
    ),
    IF_VAR_LIST_VALUE_AT_LAST_EQUALS(
            ActionCategory.VARIABLE_CONDITION,
            MenusCategory.LIST_OPERATIONS,
            ListValueAtLastEqualsCondition.class,
            Material.REPEATER,
            new ArgumentSlot("values", ValueType.ANY, (byte) 18),
            new ArgumentSlot("list", ValueType.VARIABLE)
    ),
    /**
     * <h1>World Conditions.</h1>
     */

    IF_WORLD_IS_PLAYER_IN(ActionCategory.WORLD_CONDITION, MenusCategory.WORLD, IsWorldPlayerInWorldCondition.class, Material.PLAYER_HEAD, new ArgumentSlot("players", ValueType.ANY, (byte) 18), new ParameterSlot("all"), new ParameterSlot("consider", List.of("all", "build", "dev"), Material.BEACON, Material.GRASS_BLOCK, Material.REPEATING_COMMAND_BLOCK)),
    IF_WORLD_ENTITY_EXISTS(ActionCategory.WORLD_CONDITION, MenusCategory.WORLD, IsWorldEntityInWorldCondition.class, Material.CHICKEN_SPAWN_EGG, new ArgumentSlot("entities", ValueType.ANY, (byte) 18), new ParameterSlot("all")),
    IF_WORLD_IS_IN_DEBUG_MODE(ActionCategory.WORLD_CONDITION, MenusCategory.WORLD, IsWorldDebugModeCondition.class, Material.PUFFERFISH_BUCKET),
    IF_WORLD_IS_PUBLIC(ActionCategory.WORLD_CONDITION, MenusCategory.WORLD, IsWorldPublicCondition.class, Material.SPRUCE_DOOR),
    IF_WORLD_IS_THUNDERING(ActionCategory.WORLD_CONDITION, MenusCategory.WORLD, IsWorldThunderingCondition.class, Material.TRIDENT),
    IF_WORLD_IS_DAY_TIME(ActionCategory.WORLD_CONDITION, MenusCategory.WORLD, IsWorldDayTimeCondition.class, Material.SUNFLOWER),
    IF_WORLD_IS_BED_WORKS(ActionCategory.WORLD_CONDITION, MenusCategory.WORLD, IsWorldBedWorksCondition.class, Material.GREEN_BED),
    IF_WORLD_IS_CLEAR_WEATHER(ActionCategory.WORLD_CONDITION, MenusCategory.WORLD, IsWorldClearWeatherCondition.class, Material.BLUE_WOOL),
    IF_WORLD_BLOCK_TYPE_EQUALS(ActionCategory.WORLD_CONDITION, MenusCategory.BLOCKS, WorldBlockTypeEqualsCondition.class, Material.GRASS_BLOCK, new ArgumentSlot("blocks", ValueType.LOCATION, (byte) 18), new ArgumentSlot("type", ValueType.ITEM), new ParameterSlot("all")),
    IF_WORLD_BLOCK_IS_EMPTY(ActionCategory.WORLD_CONDITION, MenusCategory.BLOCKS, IsWorldBlockEmptyCondition.class, Material.GLASS, new ArgumentSlot("blocks", ValueType.LOCATION, (byte) 18), new ParameterSlot("all")),
    IF_WORLD_BLOCK_IS_POWERED(ActionCategory.WORLD_CONDITION, MenusCategory.BLOCKS, IsWorldBlockPoweredCondition.class, Material.REDSTONE_BLOCK, new ArgumentSlot("blocks", ValueType.LOCATION, (byte) 18), new ParameterSlot("all")),
    IF_WORLD_BLOCK_IS_SOLID(ActionCategory.WORLD_CONDITION, MenusCategory.BLOCKS, IsWorldBlockSolidCondition.class, Material.BRICKS, new ArgumentSlot("blocks", ValueType.LOCATION, (byte) 18), new ParameterSlot("all")),
    IF_WORLD_BLOCK_IS_BURNABLE(ActionCategory.WORLD_CONDITION, MenusCategory.BLOCKS, IsWorldBlockBurnableCondition.class, Material.CAMPFIRE, new ArgumentSlot("blocks", ValueType.LOCATION, (byte) 18), new ParameterSlot("all")),
    IF_WORLD_BLOCK_IS_LIQUID(ActionCategory.WORLD_CONDITION, MenusCategory.BLOCKS, IsWorldBlockLiquidCondition.class, Material.WATER_BUCKET, new ArgumentSlot("blocks", ValueType.LOCATION, (byte) 18), new ParameterSlot("all")),
    IF_WORLD_BLOCK_IS_PASSABLE(ActionCategory.WORLD_CONDITION, MenusCategory.BLOCKS, IsWorldBlockPassableCondition.class, Material.SHORT_GRASS, new ArgumentSlot("blocks", ValueType.LOCATION, (byte) 18), new ParameterSlot("all")),
    IF_WORLD_BLOCK_IS_BUILDABLE(ActionCategory.WORLD_CONDITION, MenusCategory.BLOCKS, IsWorldBlockBuildableCondition.class, Material.RED_TULIP, new ArgumentSlot("blocks", ValueType.LOCATION, (byte) 18), new ParameterSlot("all")),
    IF_WORLD_BLOCK_IS_REPLACEABLE(ActionCategory.WORLD_CONDITION, MenusCategory.BLOCKS, IsWorldBlockReplaceableCondition.class, Material.TALL_GRASS, new ArgumentSlot("blocks", ValueType.LOCATION, (byte) 18), new ParameterSlot("all")),
    IF_WORLD_BLOCK_IS_COLLIDABLE(ActionCategory.WORLD_CONDITION, MenusCategory.BLOCKS, IsWorldBlockCollidableCondition.class, Material.RED_NETHER_BRICKS, new ArgumentSlot("blocks", ValueType.LOCATION, (byte) 18), new ParameterSlot("all")),
    IF_WORLD_BLOCK_IS_PREFERRED_TOOL(ActionCategory.WORLD_CONDITION, MenusCategory.BLOCKS, WorldBlockIsPreferredToolCondition.class, Material.DIAMOND_PICKAXE, new ArgumentSlot("blocks", ValueType.LOCATION, (byte) 18), new ArgumentSlot("tool", ValueType.ITEM), new ParameterSlot("all")),
    IF_WORLD_BLOCK_IS_VALID_TOOL(ActionCategory.WORLD_CONDITION, MenusCategory.BLOCKS, WorldBlockIsValidToolCondition.class, Material.STONE_PICKAXE, new ArgumentSlot("blocks", ValueType.LOCATION, (byte) 18), new ArgumentSlot("tool", ValueType.ITEM), new ParameterSlot("all")),

    /**
     * <h1>Entity Conditions.</h1>
     */

    IF_ENTITY_NAME_EQUALS(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_STATE, EntityNameEqualsCondition.class, Material.NAME_TAG, new ArgumentSlot("names", ValueType.TEXT, (byte) 18), new ParameterSlot("require-caps"), new ParameterSlot("require-color")),
    IF_ENTITY_UUID_EQUALS(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_STATE, EntityUUIDEqualsCondition.class, Material.KNOWLEDGE_BOOK, new ArgumentSlot("uuids", ValueType.TEXT, (byte) 27)),
    IF_ENTITY_IN_AREA(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_STATE, IsEntityInAreaCondition.class, Material.PAPER, new ArgumentSlot("first", ValueType.LOCATION), new ArgumentSlot("second", ValueType.LOCATION)),
    IF_ENTITY_IS_PLAYER(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_STATE, IsEntityPlayer.class, Material.PLAYER_HEAD),
    IF_ENTITY_IS_DEAD(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_STATE, IsEntityDead.class, Material.REDSTONE),
    IF_ENTITY_IS_MOB(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_STATE, IsEntityMob.class, Material.PIG_SPAWN_EGG),
    IF_ENTITY_IS_LIVING_ENTITY(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_STATE, IsEntityLivingEntity.class, Material.APPLE),
    IF_ENTITY_IS_HUMAN_ENTITY(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_STATE, IsEntityHuman.class, Material.ZOMBIE_HEAD),
    IF_ENTITY_IS_AGEABLE(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_STATE, IsEntityAgeable.class, Material.CAT_SPAWN_EGG),
    IF_ENTITY_IS_THROWABLE_PROJECTILE(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_STATE, IsEntityThrowableProjectile.class, Material.TRIDENT),
    IF_ENTITY_IS_PROJECTILE(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_STATE, IsEntityProjectile.class, Material.ARROW),
    IF_ENTITY_IS_NPC(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_STATE, IsEntityNPC.class, Material.PLAYER_HEAD),
    IF_ENTITY_IS_CREATURE(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_STATE, IsEntityCreature.class, Material.PIGLIN_HEAD),
    IF_ENTITY_IS_MONSTER(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_STATE, IsEntityMonster.class, Material.ZOMBIE_SPAWN_EGG),
    IF_ENTITY_IS_ENEMY(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_STATE, IsEntityEnemy.class, Material.ENDER_DRAGON_SPAWN_EGG),

    IF_ENTITY_IS_IN_TEAM(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_INTERACTION, IsEntityInTeam.class, Material.LIME_BANNER, new ArgumentSlot("scoreboard", ValueType.TEXT), new ArgumentSlot("team", ValueType.TEXT)),
    IF_ENTITY_IS_UNDERWATER(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_INTERACTION, IsEntityUnderWater.class, Material.BLUE_STAINED_GLASS),
    IF_ENTITY_HAS_GRAVITY(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_INTERACTION, HasEntityGravity.class, Material.SAND),
    IF_ENTITY_IS_IN_RAIN(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_INTERACTION, IsEntityInRain.class, Material.WATER_BUCKET),
    IF_ENTITY_IS_IN_LAVA(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_INTERACTION, IsEntityInLava.class, Material.LAVA_BUCKET),
    IF_ENTITY_IS_IN_POWDERED_SNOW(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_INTERACTION, IsEntityInPowderedSnow.class, Material.POWDER_SNOW_BUCKET),
    IF_ENTITY_IS_ON_GROUND(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_INTERACTION, IsEntityOnGround.class, Material.GRASS_BLOCK),
    IF_ENTITY_IS_INSIDE_VEHICLE(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_INTERACTION, IsEntityInsideVehicle.class, Material.MINECART),
    IF_ENTITY_HAS_NO_PHYSICS(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_INTERACTION, HasEntityNoPhysics.class, Material.DAMAGED_ANVIL),
    IF_ENTITY_CONTAINS_LOCATION(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_INTERACTION, IsEntityContainsLocation.class, Material.OAK_BUTTON, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ParameterSlot("all")),
    IF_ENTITY_OVERLAPS_BLOCK(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_INTERACTION, IsEntityCollidesBlock.class, Material.OAK_SLAB, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ParameterSlot("ignore-size", false, Material.SMOOTH_STONE_SLAB, Material.STONE), new ParameterSlot("all")),
    IF_ENTITY_CONTAINS_BLOCK(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_INTERACTION, IsEntityContainsBlock.class, Material.OAK_WOOD, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ParameterSlot("ignore-size", false, Material.SMOOTH_STONE_SLAB, Material.STONE), new ParameterSlot("all")),
    IF_ENTITY_IS_INSIDE_BLOCK(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_INTERACTION, IsEntityBlockContains.class, Material.GRAVEL, new ArgumentSlot("locations", ValueType.LOCATION, (byte) 18), new ParameterSlot("ignore-size", false, Material.SMOOTH_STONE_SLAB, Material.STONE), new ParameterSlot("all")),

    IF_ENTITY_IS_INVULNERABLE(ActionCategory.ENTITY_CONDITION, MenusCategory.ENTITY_INTERACTION, IsEntityInvulnerable.class, Material.TOTEM_OF_UNDYING);

    private final Class<? extends Action> actionClass;
    private final ActionCategory category;
    private final MenusCategory menusCategory;
    private final Material material;
    private final String requiredPlugin;
    private ArgumentSlot[] layout;

    ActionType(ActionCategory category, MenusCategory menusCategory, Class<? extends Action> actionClass, Material material) {
        this.actionClass = actionClass;
        this.category = category;
        this.menusCategory = menusCategory;
        this.material = material;
        this.requiredPlugin = null;
    }

    ActionType(ActionCategory category, MenusCategory menusCategory, Class<? extends Action> actionClass, Material material, String requiredPlugin, ArgumentSlot... argumentSlots) {
        this.actionClass = actionClass;
        this.layout = argumentSlots;
        this.category = category;
        this.menusCategory = menusCategory;
        this.material = material;
        this.requiredPlugin = requiredPlugin;
    }

    ActionType(ActionCategory category, MenusCategory menusCategory, Class<? extends Action> actionClass, Material material, ArgumentSlot... argumentSlots) {
        this.actionClass = actionClass;
        this.layout = argumentSlots;
        this.category = category;
        this.menusCategory = menusCategory;
        this.material = material;
        this.requiredPlugin = null;
    }

    public static ActionType getType(Block block) {
        if (block.getType() == Material.LAPIS_ORE) {
            return LAUNCH_FUNCTION;
        }
        if (block.getType() == Material.EMERALD_ORE) {
            return LAUNCH_METHOD;
        }
        Block signBlock = block.getRelative(BlockFace.SOUTH);
        String signLine = getSignLine(signBlock.getLocation(), (byte) 3);
        if (block.getType() == ActionCategory.SELECTION_ACTION.getBlock()) {
            signLine = getSignLine(signBlock.getLocation(), (byte) 4);
        }
        if (block.getType() == ActionCategory.REPEAT_ACTION.getBlock()) {
            // Repeat while <---
            // If player          Repeat
            // Is sitting         For numbers
            // Target
            String text = getSignLine(signBlock.getLocation(), (byte) 1);
            if (text != null && !text.isEmpty()) {
                signLine = getSignLine(signBlock.getLocation(), (byte) 1);
            }
        }
        if (signLine != null) {
            for (ActionType actionType : values()) {
                if (actionType.name().equals(signLine.toUpperCase())) {
                    return actionType;
                }
            }
        }
        return null;
    }

    public static @Nullable ActionType getType(@NotNull String text) {
        for (ActionType actionType : values()) {
            if (actionType.name().equalsIgnoreCase(text)) {
                return actionType;
            }
        }
        return null;
    }

    public static ActionType getTypeFromSelectionAction(Block block) {
        Block signBlock = block.getRelative(BlockFace.SOUTH);
        String signLine = getSignLine(signBlock.getLocation(), (byte) 3);
        if (signLine != null) {
            for (ActionType actionType : values()) {
                if (actionType.name().equals(signLine.toUpperCase())) {
                    return actionType;
                }
            }
        }
        return null;
    }

    public static Set<MenusCategory> getMenusCategories(ActionCategory category) {
        Set<MenusCategory> set = new LinkedHashSet<>();
        for (ActionType type : values()) {
            if (type.category == category) {
                set.add(type.menusCategory);
            }
        }
        return set;
    }

    public static List<ActionType> getActionsByCategories(ActionCategory actionCategory, MenusCategory menusCategory) {
        List<ActionType> list = new ArrayList<>();
        for (ActionType type : values()) {
            if (type.category == actionCategory && type.menusCategory == menusCategory) {
                if (!type.isDisabled() || OpenCreative.getSettings().isDebug()) list.add(type);
            }
        }
        return list;
    }

    public Class<? extends Action> getActionClass() {
        return actionClass;
    }

    public boolean isChestRequired() {
        return layout != null && layout.length > 0;
    }

    public @NotNull String getLocaleName() {
        String path = "items.developer." + (isCondition() ? "conditions" : "actions") + "." + this.name().toLowerCase().replace("_", "-") + ".name";
        if (messageExists(path)) {
            return getLocaleMessage(path);
        } else {
            return this.name().toLowerCase().replace("_", "-");
        }
    }

    public ArgumentSlot[] getArgumentsSlots() {
        return layout;
    }

    public byte getArgumentSlotID(ArgumentSlot slot) {
        for (byte i = 0; i < this.getArgumentsSlots().length; i++) {
            if (this.getArgumentsSlots()[i] == slot) return (byte) (i + 1);
        }
        return -1;
    }

    public boolean isCondition() {
        return category.isCondition();
    }

    @Override
    public boolean isDisabled() {
        if (this == PLAYER_SET_RESOURCE_PACK || this == IF_PLAYER_HAS_RESOURCE_PACK) {
            if (!OpenCreative.getSettings().getLobbySettings().shouldResetResourcePack()) {
                return true;
            }
        }
        if (this == PLAYER_SET_VIEW_DISTANCE || this == PLAYER_SET_SIMULATION_DISTANCE) {
            if (!OpenCreative.getSettings().getLobbySettings().shouldResetViewDistance()) {
                return true;
            }
        }
        if (this.name().contains("DISGUISE")) {
            if (!OpenCreative.getDisguiseManager().isEnabled()) {
                return true;
            }
        }
        return getActionClass() == null
                || (requiredPlugin != null && !HookUtils.isPluginEnabled(requiredPlugin))
                || (isCondition() && OpenCreative.getSettings().getCodingSettings().isDisabledCondition(this))
                || OpenCreative.getSettings().getCodingSettings().isDisabledAction(this);
    }

    @Override
    public final @NotNull ItemStack getIcon() {
        ItemStack icon = createItem(this.material, 1, "items.developer." + (this.isCondition() ? "conditions" : "actions") + "." + this.name().toLowerCase().replace("_", "-"));
        if (isDisabled()) {
            icon.setType(Material.RED_STAINED_GLASS);
            addLoreAtEnd(icon, getLocaleMessage("disabled"));
        }
        setPersistentData(icon, getCodingValueKey(), name());
        return icon;
    }

    @Override
    public @NotNull ActionCategory getCategory() {
        return category;
    }

}
