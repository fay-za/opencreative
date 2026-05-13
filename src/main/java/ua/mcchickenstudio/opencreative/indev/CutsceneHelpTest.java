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

package ua.mcchickenstudio.opencreative.indev;

import ua.mcchickenstudio.opencreative.planets.DevPlanet;
import ua.mcchickenstudio.opencreative.utils.ItemUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

public class CutsceneHelpTest {

    private final DevPlanet devPlanet;

    public CutsceneHelpTest(DevPlanet devPlanet) {
        this.devPlanet = devPlanet;
    }

    private ItemStack getChestPlate() {
        ItemStack chestPlate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestPlateMeta = (LeatherArmorMeta) chestPlate.getItemMeta();
        chestPlateMeta.setColor(Color.fromRGB(199,199,199));
        chestPlate.setItemMeta(chestPlateMeta);
        return chestPlate;
    }

    private ItemStack getLeggings() {
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
        leggingsMeta.setColor(Color.fromRGB(92,92,92));
        leggings.setItemMeta(leggingsMeta);
        return leggings;
    }

    private ItemStack getBoots() {
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(Color.fromRGB(64,64,64));
        boots.setItemMeta(bootsMeta);
        return boots;
    }

    private void setupArmorStand(ArmorStand stand) {
        stand.getPersistentDataContainer().set(ItemUtils.getCodingValueKey(), PersistentDataType.STRING,"cutscene");
        stand.setArms(true);
        stand.setBasePlate(false);
        stand.getEquipment().setHelmet(new ItemStack(Material.PLAYER_HEAD));
        stand.getEquipment().setChestplate(getChestPlate());
        stand.getEquipment().setLeggings(getLeggings());
        stand.getEquipment().setBoots(getBoots());
    }

    public void clearEntities() {
        if (!devPlanet.isLoaded()) return;
        for (Entity entity : devPlanet.getWorld().getEntities()) {
            if (entity.getPersistentDataContainer().has(ItemUtils.getCodingValueKey())) {
                entity.remove();
            }
        }
    }

    public void start() {
        if (!devPlanet.isLoaded()) return;
        Location location = new Location(devPlanet.getWorld(),2,1,2);
        ArmorStand stand = (ArmorStand) devPlanet.getWorld().spawnEntity(location,EntityType.ARMOR_STAND);
        setupArmorStand(stand);
    }
}
