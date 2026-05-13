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

package ua.mcchickenstudio.opencreative.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.util.UUID;

/**
 * <h1>Advancement</h1>
 * This class represents a custom advancement, used in
 * {@link ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.communication.ShowAdvancementAction Show Advancement Action}.
 */
public final class Advancement {

    private final Material material;
    private final AdvancementStyle style;
    private final String title;
    private final String message;
    private final NamespacedKey nameSpacedKey = new NamespacedKey(OpenCreative.getPlugin(), "oc-advancement-" + UUID.randomUUID().toString());

    private Advancement(@NotNull ItemStack itemStack, @NotNull AdvancementStyle style,
                        @NotNull String title, @NotNull String message) {
        this.material = itemStack.getType();
        this.style = style;
        this.title = title;
        this.message = message;
    }

    public static Advancement make(@NotNull ItemStack itemStack, @NotNull AdvancementStyle style,
                                   @NotNull String title, @NotNull String message) {
        return new Advancement(itemStack, style, title, message);
    }

    public void show(@NotNull Player player) {
        org.bukkit.advancement.Advancement advancement = load();
        if (advancement == null) {
            return;
        }
        player.getAdvancementProgress(advancement).awardCriteria("trigger");
        Bukkit.getScheduler().runTaskLater(OpenCreative.getPlugin(), () -> {
            player.getAdvancementProgress(advancement).revokeCriteria("trigger");
            Bukkit.getUnsafe().removeAdvancement(nameSpacedKey);
        }, 10);
    }

    @SuppressWarnings("deprecation")
    private @Nullable org.bukkit.advancement.Advancement load() {
        return Bukkit.getUnsafe().loadAdvancement(nameSpacedKey, "{\n" +
                "    \"criteria\": {\n" +
                "        \"trigger\": {\n" +
                "            \"trigger\": \"minecraft:impossible\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"display\": {\n" +
                "        \"icon\": {\n" +
                "            \"id\": \"minecraft:" + material.toString().toLowerCase() + "\"\n" +
                "        },\n" +
                "        \"title\": {\n" +
                "            \"text\": \"" + title + "\n" + message + "\"\n" +
                "        },\n" +
                "        \"description\": {\n" +
                "            \"text\": \"\"\n" +
                "        },\n" +
                "        \"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\",\n" +
                "        \"frame\": \"" + style.toString().toLowerCase() + "\",\n" +
                "        \"announce_to_chat\": false,\n" +
                "        \"show_toast\": true,\n" +
                "        \"hidden\": true\n" +
                "    },\n" +
                "    \"requirements\": [\n" +
                "        [\n" +
                "            \"trigger\"\n" +
                "        ]\n" +
                "    ]\n" +
                "}");
    }

    public enum AdvancementStyle {

        GOAL,
        TASK,
        CHALLENGE

    }

}
