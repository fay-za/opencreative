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

package ua.mcchickenstudio.opencreative.settings;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import ua.mcchickenstudio.opencreative.OpenCreative;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendWarningMessage;

public enum Sounds {

    LOBBY("block.beacon.deactivate", 1.5f),
    LOBBY_MUSIC("music_disc.creator", 0.9f),
    OPENCREATIVE("block.beacon.activate", 2),
    RELOADING("block.beacon.ambient", 2),
    RELOADED("block.beacon.deactivate", 2),

    MENU_NEXT_PAGE("item.book.page_turn"),
    MENU_PREVIOUS_PAGE("item.book.page_turn"),
    MENU_NEXT_CHOICE("ui.button.click"),
    MENU_OPEN_GENERATION("block.portal.ambient"),
    MENU_OPEN_WORLD_ACCESS("item.armor.equip_netherite", 0.1f),
    MENU_OPEN_WORLD_MODERATION("entity.warden.listening", 0.1f),
    MENU_OPEN_ENVIRONMENT("block.amethyst_block.chime", 0.1f),
    MENU_OPEN_WORLD_SETTINGS("block.amethyst_block.resonate", 0.1f),
    MENU_OPEN_MODULE_SETTINGS("block.amethyst_block.resonate", 0.1f),
    MENU_OPEN_PROFILE_SETTINGS("block.note_block.chime", 1f),
    MENU_OPEN_RECOMMENDATIONS("block.ender_chest.open", 0.1f),
    MENU_OPEN_PROFILE("entity.cat.purreow", 1f),
    MENU_OPEN_OWN_WORLDS_BROWSER("block.enchantment_table.use", 1.4f),
    MENU_OPEN_OWN_MODULES_BROWSER("block.enchantment_table.use", 1.4f),
    MENU_OPEN_WORLDS_BROWSER("block.vault.activate"),
    MENU_OPEN_ENTITIES_BROWSER("entity.panda.worried_ambient", 0.1f),

    MENU_OPEN_PLAYERS_BROWSER("entity.panda.worried_ambient", 0.1f),
    MENU_OPEN_PLAYER_CONTROL("entity.panda.worried_ambient", 0.1f),
    MENU_PLAYERS_BROWSER_SORT("block.trial_spawner.spawn_item", 1.2f),

    MENU_OPEN_VALUES_BROWSER("ui.loom.select_pattern"),
    MENU_OPEN_CONFIRMATION("block.amethyst_block.resonate", 0.5f),
    MENU_OPEN_MODULES_BROWSER("block.shulker_box.open", 0.1f),
    MENU_OPEN_BLOCKS_MANIPULATOR("block.amethyst_block.resonate", 0.5f),
    MENU_GENERATION_CHANGE("block.amethyst_block.resonate"),
    MENU_ENVIRONMENT_CHANGE("block.amethyst_block.step", 0.1f),
    MENU_ENTITIES_BROWSER_SORT("block.trial_spawner.spawn_item", 1.2f),
    MENU_WORLDS_BROWSER_SORT("block.trial_spawner.spawn_item", 0.6f),
    MENU_WORLDS_BROWSER_CATEGORY("block.trial_spawner.detect_player", 1.2f),
    MENU_WORLD_SEARCH("block.respawn_anchor.ambient"),
    MENU_GENERATE_STRUCTURES_CHANGE("block.respawn_anchor.charge", 2),
    MENU_BIOME_CHANGE("block.scaffolding.break", 1),

    MENU_CLEAR_DATA("item.brush.brushing.generic", 0.1f),

    WORLD_GENERATION("block.respawn_anchor.set_spawn", 0.1f),
    WORLD_CONNECTION("block.trial_spawner.about_to_spawn_item"),
    WORLD_CONNECTED("block.beacon.activate", 2),
    WORLD_LIKED("item.bottle.fill_dragonbreath", 1.3f),
    WORLD_DISLIKED("item.bottle.fill_dragonbreath", 0.7f),
    WELCOME_TO_NEW_WORLD("ui.toast.challenge_complete", 0.1f),

    WORLD_MODE_BUILD("block.beacon.power_select", 1.7f),
    WORLD_MODE_DEV("entity.illusioner.prepare_mirror", 0.5f),

    WORLD_NOW_BUILDER("entity.cat.ambient"),
    WORLD_NOW_DEVELOPER("entity.cat.ambient"),
    WORLD_NOW_DEVELOPER_GUEST("entity.cat.ambient"),
    WORLD_WHITELIST_ADDED("entity.cat.ambient"),
    WORLD_WHITELIST_REMOVED("entity.cat.hurt"),
    WORLD_KICKED("entity.cat.hurt"),
    WORLD_BANNED("entity.cat.hurt"),

    WORLD_LOAD("entity.allay.ambient_without_item", 0.1f),
    WORLD_UNLOAD("entity.allay.ambient_without_item", 0.5f),
    WORLD_DELETION("entity.wither.spawn", 0.1f),

    PLAYER_CANCEL("entity.villager.no"),
    PLAYER_FAIL("block.amethyst_block.break", 0.1f),
    PLAYER_ERROR("block.amethyst_block.break", 0.1f),
    PLAYER_TELEPORT("entity.illusioner.mirror_move", 0.1f),
    PLAYER_RESPAWN("entity.player.breath", 2f),

    WORLD_CODE_ERROR("block.beacon.deactivate", 1.7f),
    WORLD_CODE_WARNING("block.amethyst_block.break"),
    WORLD_CODE_COMPILE_ERROR("block.beacon.deactivate", 1.7f),
    WORLD_CODE_CRITICAL_ERROR("block.respawn_anchor.deplete", 0.5f),
    WORLD_PURCHASE("entity.player.levelup", 1.2f),

    WORLD_SETTINGS_FLAG_CHANGE("ui.loom.select_pattern"),
    WORLD_SETTINGS_CATEGORY_SET("entity.player.levelup", 1.6f),
    WORLD_SETTINGS_TIME_CHANGE("block.respawn_anchor.charge", 1.2f),
    WORLD_SETTINGS_AUTOSAVE_ON("block.beacon.activate", 0.7f),
    WORLD_SETTINGS_AUTOSAVE_OFF("block.respawn_anchor.deplete", 0.3f),
    WORLD_SETTINGS_SHARING_PUBLIC("block.iron_door.open"),
    WORLD_SETTINGS_SHARING_PRIVATE("entity.evoker_fangs.attack", 0.6f),
    WORLD_SETTINGS_SPAWN_TELEPORT("entity.illusioner.mirror_move", 0.8f),
    WORLD_SETTINGS_SPAWN_SET("entity.illusioner.cast_spell", 0.8f),
    WORLD_SETTINGS_OWNER_SET("ui.toast.challenge_complete", 1.5f),
    WORLD_SETTINGS_NAME_CHANGE("entity.villager.work_cartographer", 1f),
    WORLD_SETTINGS_DESCRIPTION_SET("entity.villager.work_cartographer", 1f),
    WORLD_SETTINGS_CUSTOM_ID_SET("entity.villager.work_cartographer", 1f),

    PROFILE_SETTINGS_GENDER_CHANGE("entity.armadillo.scute_drop", 0.1f),
    PROFILE_SETTINGS_SOCIAL_SET("entity.villager.yes", 1f),
    PROFILE_SETTINGS_DESCRIPTION_SET("entity.villager.work_cartographer", 1f),

    MODULE_SETTINGS_NAME_SET("entity.villager.work_cartographer", 1f),
    MODULE_SETTINGS_DESCRIPTION_SET("entity.villager.work_cartographer", 1f),

    WORLD_REMOVE_ENTITY("entity.illusioner.prepare_blindness", 1.6f),
    WORLD_TELEPORT_TO_ENTITY("entity.illusioner.mirror_move", 0.8f),
    WORLD_TELEPORT_ENTITY_TO_ME("entity.illusioner.cast_spell", 0.8f),

    WORLD_DOWNLOADER_UPLOADING("block.trial_spawner.about_to_spawn_item", 0.6f),
    WORLD_DOWNLOADER_UPLOADED("block.vault.open_shutter", 0.1f),

    DEV_CONNECTED("block.beacon.activate", 2),
    DEV_NOT_ALLOWED("block.amethyst_block.break", 1.2f),
    DEV_OPEN_CHEST("block.ender_chest.open", 0.6f),
    DEV_OPEN_BARREL("block.barrel.open", 0.6f),
    DEV_CLOSED_CHEST("block.ender_chest.close"),
    DEV_CLOSED_BARREL("block.barrel.close", 0.6f),
    DEV_SET_EVENT("entity.experience_orb.pickup", 1.7f),
    DEV_SET_ACTION("entity.experience_orb.pickup", 1.5f),
    DEV_SET_CONDITION("entity.experience_orb.pickup", 1.3f),
    DEV_SET_TARGET("block.end_portal_frame.fill", 0.2f),
    DEV_SET_METHOD("block.enchantment_table.use", 0.7f),
    DEV_SET_FUNCTION("block.enchantment_table.use", 1.2f),
    DEV_VAR_LIST("ui.loom.select_pattern", 0.7f),
    DEV_PLATFORM_COLOR("entity.illusioner.prepare_mirror"),
    DEV_PLATFORM_SIGN("entity.illusioner.prepare_mirror", 0.7f),
    DEV_SETTINGS_NIGHT_VISION("entity.illusioner.prepare_mirror", 0.7f),
    DEV_SETTINGS_SAVE_LOCATION("entity.illusioner.prepare_mirror", 0.7f),
    DEV_SETTINGS_DROP_ITEMS("entity.illusioner.prepare_mirror", 0.7f),
    DEV_PLATFORM_CLAIM("ui.toast.challenge_complete", 1.7f),
    DEV_SET_DEBUG_TORCH("entity.villager.work_mason", 1.7f),
    DEV_UNSET_DEBUG_TORCH("entity.villager.work_mason", 1.2f),
    DEV_MODULE_INSTALLED("entity.allay.item_given", 0.1f),
    DEV_MODULE_CREATED("ui.toast.challenge_complete", 1.4f),

    DEV_ACTION_TARGET("block.amethyst_block.resonate"),
    DEV_ACTION_WITH_CHEST("block.ender_chest.close", 1.2f),
    DEV_CONDITION_NOT("block.trial_spawner.close_shutter"),
    DEV_CONDITION_DEFAULT("block.trial_spawner.close_shutter", 0.1f),
    DEV_CYCLE_DELAY_DECREASE("block.chain.fall", 0.1f),
    DEV_CYCLE_DELAY_INCREASE("block.chain.fall"),
    DEV_CYCLE_DELAY_SET("block.chain.fall", 0.5f),
    DEV_CYCLE_NAMED("block.chain.fall", 0.7f),
    DEV_FUNCTION_NAMED("block.enchantment_table.use", 0.7f),
    DEV_METHOD_NAMED("block.enchantment_table.use", 0.7f),
    DEV_MARK_EXECUTOR("block.trial_spawner.close_shutter"),
    DEV_UNMARK_EXECUTOR("block.trial_spawner.close_shutter", 0.7f),
    DEV_BLOCKS_DUPLICATED("entity.allay.item_given", 0.3f),

    DEV_VARIABLE_PARAMETER("block.vault.activate", 0.7f),
    DEV_NEXT_PARAMETER("block.amethyst_block.resonate", 1.7f),
    DEV_CHANGE_CATEGORY("item.book.page_turn", 0.5f),
    DEV_TAKE_VALUE("entity.allay.item_thrown", 2f),
    DEV_VALUE_SET("entity.axolotl.attack"),
    DEV_TEXT_SET("entity.evoker.cast_spell", 0.5f),
    DEV_NUMBER_SET("entity.slime.attack", 0.1f),
    DEV_PARTICLE_SET("entity.firework_rocket.large_blast_far", 1.2f),
    DEV_VARIABLE_SET("item.bottle.fill_dragonbreath", 1.4f),
    DEV_VARIABLE_CHANGE("item.bottle.fill_dragonbreath", 1.7f),
    DEV_FLY_SPEED_CHANGE("entity.player.levelup", 1.9f),
    DEV_POTION_SET("block.brewing_stand.brew", 1.2f),
    DEV_LOCATION_SET("entity.experience_orb.pickup", 2),
    DEV_LOCATION_TELEPORT("entity.illusioner.mirror_move", 0.7f),
    DEV_LOCATION_TELEPORT_BACK("entity.illusioner.mirror_move", 0.9f),
    DEV_EVENT_VALUE_SET("entity.ender_eye.death", 0.1f),
    DEV_VECTOR_SET("entity.warden.listening", 0.1f),
    DEV_BOOLEAN_TRUE("item.bottle.fill_dragonbreath", 1.7f),
    DEV_BOOLEAN_FALSE("item.bottle.fill_dragonbreath", 1.3f),
    DEV_MOVE_BLOCKS_RIGHT("block.barrel.close", 1.3f),
    DEV_MOVE_BLOCKS_LEFT("block.barrel.close", 1.5f),
    DEV_DEBUG_ON("entity.allay.ambient_without_item"),
    DEV_DEBUG_OFF("entity.allay.ambient_with_item"),
    DEV_MODULE_LIKED("item.bottle.fill_dragonbreath", 1.3f),
    DEV_MODULE_DISLIKED("item.bottle.fill_dragonbreath", 0.7f),
    DEV_INSERTED_IN_CONTAINER("block.bamboo_wood_fence_gate.open", 0.1f),
    DEV_PROMPTER_THINKING("block.beacon.activate", 0.1f),
    DEV_PROMPTER_DONE("block.vault.reject_rewarded_player", 0.6f),

    MAINTENANCE_NOTIFY("block.bell.use", 0.1f),
    MAINTENANCE_COUNT("block.end_portal_frame.fill", 2),
    MAINTENANCE_START("block.beacon.power_select", 0.5f),
    MAINTENANCE_END("block.beacon.power_select", 0.7f);

    public static final String SOUND_REGEX = "^[a-z0-9/.:_-]+$";
    private final String name;
    private final float pitch;

    Sounds(String name, float pitch) {
        this.name = name;
        this.pitch = pitch;
    }

    Sounds(String name) {
        this.name = name;
        this.pitch = 1;
    }

    public void play(Audience audience) {
        String nameSpace = "minecraft";
        String soundName = this.name;
        float pitch = this.pitch;
        SettingsSound customSound = OpenCreative.getSettings().getSounds().get(this);
        if (customSound != null) {
            soundName = customSound.sound();
            pitch = customSound.pitch();
        }
        if (soundName.contains(":")) {
            nameSpace = soundName.split(":")[0];
            soundName = soundName.split(":")[1];
        }
        try {
            audience.playSound(Sound.sound(Key.key(nameSpace, soundName),
                    Sound.Source.RECORD, 100f, pitch));
        } catch (Exception error) {
            sendWarningMessage("Can't play sound: " + nameSpace + " " + soundName, error);
        }
    }

}
