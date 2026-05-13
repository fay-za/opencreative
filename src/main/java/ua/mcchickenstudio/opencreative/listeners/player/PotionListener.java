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

package ua.mcchickenstudio.opencreative.listeners.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public final class PotionListener implements Listener {

    private final static int POTION_AMPLIFIER_LIMIT = 100;

    public static boolean isCorrupted(List<PotionEffect> effects) {
        for (PotionEffect effect : effects) {
            if (effect.getAmplifier() > POTION_AMPLIFIER_LIMIT) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCorrupted(PotionMeta potionMeta) {
        return isCorrupted(potionMeta.getCustomEffects());
    }

    @EventHandler
    public void onEvent(AreaEffectCloudApplyEvent event) {
        if (isCorrupted(event.getEntity().getCustomEffects())) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onEvent(PotionSplashEvent event) {
        if (isCorrupted(event.getEntity().getPotionMeta())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEvent(LingeringPotionSplashEvent event) {
        if (isCorrupted(event.getEntity().getPotionMeta())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEvent(EntityPotionEffectEvent event) {
        if (event.getOldEffect() != null && event.getOldEffect().getAmplifier() > POTION_AMPLIFIER_LIMIT) {
            event.setCancelled(true);
        }
        if (event.getNewEffect() != null && event.getNewEffect().getAmplifier() > POTION_AMPLIFIER_LIMIT) {
            event.setCancelled(true);
        }
    }

}
