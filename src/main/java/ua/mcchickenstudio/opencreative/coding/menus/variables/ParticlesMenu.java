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

package ua.mcchickenstudio.opencreative.coding.menus.variables;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.menus.ListBrowserMenu;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.mcchickenstudio.opencreative.utils.ItemUtils.*;
import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public final class ParticlesMenu extends ListBrowserMenu<Particle> {

    private static final Map<Particle, Material> particles = new HashMap<>();

    static {
        particles.put(Particle.POOF, Material.BONE_MEAL);
        particles.put(Particle.EXPLOSION, Material.TNT_MINECART);
        particles.put(Particle.EXPLOSION_EMITTER, Material.TNT);
        particles.put(Particle.FIREWORK, Material.FIREWORK_ROCKET);
        particles.put(Particle.BUBBLE, Material.HEART_OF_THE_SEA);
        particles.put(Particle.SPLASH, Material.LAPIS_LAZULI);
        particles.put(Particle.FISHING, Material.FISHING_ROD);
        particles.put(Particle.UNDERWATER, Material.WET_SPONGE);
        particles.put(Particle.CRIT, Material.IRON_SWORD);
        particles.put(Particle.ENCHANTED_HIT, Material.NETHERITE_SWORD);
        particles.put(Particle.SMOKE, Material.CAMPFIRE);
        particles.put(Particle.LARGE_SMOKE, Material.SOUL_CAMPFIRE);
        particles.put(Particle.EFFECT, Material.POTION);
        particles.put(Particle.INSTANT_EFFECT, Material.WHITE_DYE);
        particles.put(Particle.ENTITY_EFFECT, Material.VILLAGER_SPAWN_EGG);
        particles.put(Particle.WITCH, Material.WITCH_SPAWN_EGG);
        particles.put(Particle.MYCELIUM, Material.MYCELIUM);
        particles.put(Particle.NOTE, Material.NOTE_BLOCK);
        particles.put(Particle.PORTAL, Material.PURPLE_STAINED_GLASS);
        particles.put(Particle.ENCHANT, Material.ENCHANTING_TABLE);
        particles.put(Particle.FLAME, Material.CAMPFIRE);
        particles.put(Particle.LAVA, Material.LAVA_BUCKET);
        particles.put(Particle.CLOUD, Material.QUARTZ);
        particles.put(Particle.ITEM_SLIME, Material.SLIME_BALL);
        particles.put(Particle.HEART, Material.RED_BED);
        particles.put(Particle.ITEM, Material.ITEM_FRAME);
        particles.put(Particle.BLOCK, Material.GRASS_BLOCK);
        particles.put(Particle.RAIN, Material.WATER_BUCKET);
        particles.put(Particle.ELDER_GUARDIAN, Material.ELDER_GUARDIAN_SPAWN_EGG);
        particles.put(Particle.DRAGON_BREATH, Material.DRAGON_BREATH);
        particles.put(Particle.END_ROD, Material.END_ROD);
        particles.put(Particle.SWEEP_ATTACK, Material.BRUSH);
        particles.put(Particle.FALLING_DUST, Material.SAND);
        particles.put(Particle.SPIT, Material.WHITE_DYE);
        particles.put(Particle.SQUID_INK, Material.INK_SAC);
        particles.put(Particle.BUBBLE_POP, Material.HEART_OF_THE_SEA);
        particles.put(Particle.CURRENT_DOWN, Material.BUCKET);
        particles.put(Particle.BUBBLE_COLUMN_UP, Material.WATER_BUCKET);
        particles.put(Particle.NAUTILUS, Material.NAUTILUS_SHELL);
        particles.put(Particle.DOLPHIN, Material.DOLPHIN_SPAWN_EGG);
        particles.put(Particle.SNEEZE, Material.PANDA_SPAWN_EGG);
        particles.put(Particle.CAMPFIRE_COSY_SMOKE, Material.CAMPFIRE);
        particles.put(Particle.CAMPFIRE_SIGNAL_SMOKE, Material.SOUL_CAMPFIRE);
        particles.put(Particle.FLASH, Material.WIND_CHARGE);
        particles.put(Particle.FALLING_LAVA, Material.LAVA_BUCKET);
        particles.put(Particle.LANDING_LAVA, Material.LAVA_BUCKET);
        particles.put(Particle.FALLING_WATER, Material.WATER_BUCKET);
        particles.put(Particle.DRIPPING_HONEY, Material.HONEYCOMB_BLOCK);
        particles.put(Particle.LANDING_HONEY, Material.HONEY_BLOCK);
        particles.put(Particle.FALLING_NECTAR, Material.HONEYCOMB);
        particles.put(Particle.SOUL_FIRE_FLAME, Material.SOUL_CAMPFIRE);
        particles.put(Particle.ASH, Material.BLACK_DYE);
        particles.put(Particle.CRIMSON_SPORE, Material.CRIMSON_PLANKS);
        particles.put(Particle.WARPED_SPORE, Material.WARPED_PLANKS);
        particles.put(Particle.SOUL, Material.SOUL_SAND);
        particles.put(Particle.DRIPPING_OBSIDIAN_TEAR, Material.CRYING_OBSIDIAN);
        particles.put(Particle.FALLING_OBSIDIAN_TEAR, Material.OBSIDIAN);
        particles.put(Particle.LANDING_OBSIDIAN_TEAR, Material.OBSIDIAN);
        particles.put(Particle.REVERSE_PORTAL, Material.PURPLE_SHULKER_BOX);
        particles.put(Particle.WHITE_ASH, Material.WHITE_DYE);
        particles.put(Particle.DUST_COLOR_TRANSITION, Material.ORANGE_DYE);
        particles.put(Particle.FALLING_SPORE_BLOSSOM, Material.SPORE_BLOSSOM);
        particles.put(Particle.SPORE_BLOSSOM_AIR, Material.SPORE_BLOSSOM);
        particles.put(Particle.SMALL_FLAME, Material.CAMPFIRE);
        particles.put(Particle.SNOWFLAKE, Material.SNOW_BLOCK);
        particles.put(Particle.DRIPPING_DRIPSTONE_LAVA, Material.LAVA_BUCKET);
        particles.put(Particle.FALLING_DRIPSTONE_LAVA, Material.LAVA_BUCKET);
        particles.put(Particle.DRIPPING_DRIPSTONE_WATER, Material.WATER_BUCKET);
        particles.put(Particle.FALLING_DRIPSTONE_WATER, Material.WATER_BUCKET);
        particles.put(Particle.GLOW_SQUID_INK, Material.GLOW_INK_SAC);
        particles.put(Particle.GLOW, Material.WHITE_STAINED_GLASS);
        particles.put(Particle.WAX_ON, Material.HONEYCOMB);
        particles.put(Particle.WAX_OFF, Material.HONEYCOMB);
        particles.put(Particle.ELECTRIC_SPARK, Material.QUARTZ);
        particles.put(Particle.SCRAPE, Material.TURTLE_HELMET);
        particles.put(Particle.SONIC_BOOM, Material.WARDEN_SPAWN_EGG);
        particles.put(Particle.SCULK_SOUL, Material.SCULK_CATALYST);
        particles.put(Particle.SCULK_CHARGE, Material.SCULK_SENSOR);
        particles.put(Particle.SCULK_CHARGE_POP, Material.SCULK);
        particles.put(Particle.CHERRY_LEAVES, Material.CHERRY_LEAVES);
        particles.put(Particle.EGG_CRACK, Material.TURTLE_EGG);
        particles.put(Particle.DUST_PLUME, Material.GUNPOWDER);
        particles.put(Particle.WHITE_SMOKE, Material.WHITE_STAINED_GLASS);
        particles.put(Particle.GUST, Material.BOW);
        particles.put(Particle.GUST_EMITTER_SMALL, Material.ARROW);
        particles.put(Particle.GUST_EMITTER_LARGE, Material.SPECTRAL_ARROW);
        particles.put(Particle.SMALL_GUST, Material.TIPPED_ARROW);
        particles.put(Particle.TRIAL_SPAWNER_DETECTION, Material.TRIAL_SPAWNER);
        particles.put(Particle.TRIAL_SPAWNER_DETECTION_OMINOUS, Material.TRIAL_SPAWNER);
        particles.put(Particle.VAULT_CONNECTION, Material.VAULT);
        particles.put(Particle.DUST_PILLAR, Material.WHITE_CANDLE);
        particles.put(Particle.OMINOUS_SPAWNING, Material.SPAWNER);
        particles.put(Particle.RAID_OMEN, Material.PILLAGER_SPAWN_EGG);
        particles.put(Particle.TRIAL_OMEN, Material.TRIAL_KEY);
        particles.put(Particle.BLOCK_MARKER, Material.WHITE_STAINED_GLASS);


        particles.put(Particle.DUST, Material.GLOWSTONE_DUST);
        particles.put(Particle.ANGRY_VILLAGER, Material.BEETROOT);
        particles.put(Particle.HAPPY_VILLAGER, Material.EMERALD);
        particles.put(Particle.TOTEM_OF_UNDYING, Material.TOTEM_OF_UNDYING);
        particles.put(Particle.DRIPPING_LAVA, Material.LAVA_BUCKET);
        particles.put(Particle.DRIPPING_WATER, Material.WATER_BUCKET);
        particles.put(Particle.ITEM_COBWEB, Material.COBWEB);
        particles.put(Particle.DAMAGE_INDICATOR, Material.NETHERITE_SWORD);
        particles.put(Particle.ITEM_SNOWBALL, Material.SNOWBALL);
        particles.put(Particle.VIBRATION, Material.SCULK_SENSOR);
    }

    public ParticlesMenu(Player player) {
        super(player, getLocaleMessage("menus.developer.particles-list.title"), PlacementLayout.VALUE_CHOOSER);
    }

    private Material getMaterial(Particle type) {
        if (particles.containsKey(type)) {
            return particles.get(type);
        }
        return Material.NETHER_STAR;
    }

    @Override
    protected ItemStack getElementIcon(Particle particle) {
        ItemStack itemStack = createItem(getMaterial(particle), 1);
        setDisplayName(itemStack, particle.name());
        setPersistentData(itemStack, getCodingValueKey(), "PARTICLE");
        setPersistentData(itemStack, getCodingParticleTypeKey(), particle.name());
        return itemStack;
    }

    @Override
    protected void fillDecorationItems() {
    }

    @Override
    protected void fillOtherItems() {
    }

    @Override
    protected void onCharmsBarClick(InventoryClickEvent event) {
    }

    @Override
    protected void onElementClick(InventoryClickEvent event) {
        if (isPlayerClicked(event) && isClickedInMenuSlots(event)) {
            if (event.getCurrentItem() == null) return;
            ItemStack item = event.getCurrentItem().clone();
            item.setType(Material.NETHER_STAR);
            event.getWhoClicked().getInventory().setItemInMainHand(item);
            Sounds.DEV_PARTICLE_SET.play(event.getWhoClicked());
        }
        event.setCancelled(true);
    }

    @Override
    public List<Particle> getElements() {
        return Arrays.asList(Particle.values());
    }

    @Override
    protected ItemStack getNextPageButton() {
        return createItem(Material.SPECTRAL_ARROW, 1, "menus.developer.particles-list.items.next-page");
    }

    @Override
    protected ItemStack getPreviousPageButton() {
        return createItem(Material.ARROW, 1, "menus.developer.particles-list.items.previous-page");
    }

    @Override
    protected ItemStack getNoElementsButton() {
        return createItem(Material.BARRIER, 1, "menus.developer.particles-list.items.no-elements");
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {

    }
}
