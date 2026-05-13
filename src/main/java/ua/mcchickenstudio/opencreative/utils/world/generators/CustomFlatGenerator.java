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

package ua.mcchickenstudio.opencreative.utils.world.generators;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendWarningErrorMessage;

/**
 * <h1>CustomFlatGenerator</h1>
 * This class represents a custom flat world generator,
 * that has generation pattern to set blocks.
 */
public final class CustomFlatGenerator extends AbstractFlatGenerator {

    /**
     * @param id            id of generator.
     * @param displayIcon   icon to display.
     * @param generation    generation pattern, that consists of amounts and material layers.
     *                      <pre>
     *                      {@code
     *                      "bedrock" // Generates only 1 layer with bedrock
     *                      "bedrock,2*sand" // Generates bedrock on Y = 0, and sand on Y = 1, 2.
     *                      "bedrock,2*air,stone" // Generates bedrock on Y = 0, and stone on Y = 3.
     *                      "bedrock,grass_block;ice_spikes" // Generates bedrock, grass_block with ice_spikes biome.
     *                      } </pre>
     * @param generateTrees generate trees or not.
     */
    public CustomFlatGenerator(@NotNull String id, @NotNull ItemStack displayIcon, @NotNull String generation, boolean generateTrees) {
        super(id, displayIcon);
        Map<Integer, Material> generationBlocks = getBlocksFromText(generation);
        for (int y : generationBlocks.keySet()) {
            blocks.put(y, generationBlocks.get(y));
        }
        this.generateTrees = generateTrees;
    }

    private @NotNull Map<Integer, Material> getBlocksFromText(@NotNull String text) {
        Map<Integer, Material> blocksMap = new LinkedHashMap<>();
        try {
            // minecraft:bedrock,2*minecraft:dirt,1*minecraft:grass_block;ice_spikes
            text = text.toUpperCase().replace("-", "_").replace("MINECRAFT:", "");
            // BEDROCK,2*DIRT,1*GRASS_BLOCK;ICE_SPIKES
            String[] blocksAndBiome = text.split(";");
            String[] blocks = blocksAndBiome[0].split(",");
            int y = 0;
            for (String blockData : blocks) {
                String[] amountAndMaterial = blockData.split("\\*");
                String amountString = "";
                String materialString = "";
                if (amountAndMaterial.length == 1) {
                    amountString = "1";
                    materialString = amountAndMaterial[0];
                } else if (amountAndMaterial.length == 2) {
                    amountString = amountAndMaterial[0];
                    materialString = amountAndMaterial[1];
                }
                int amount = 1;
                Material material = Material.AIR;
                try {
                    amount = Integer.parseInt(amountString);
                    if (amount < 0 || amount > 320) amount = 1;
                } catch (NumberFormatException ignored) {
                }
                material = Objects.requireNonNullElse(Material.getMaterial(materialString), material);
                if (!material.isBlock() || material == Material.AIR) {
                    y += amount;
                    continue;
                }
                for (int i = 0; i < amount; i++) {
                    blocksMap.put(y, material);
                    y++;
                }
            }
            if (blocksAndBiome.length == 1) return blocksMap;
            String biomeString = blocksAndBiome[1];
            Biome biome = getBiome(biomeString);
            if (biome != null) {
                this.biome = biome;
            } else {
                sendWarningErrorMessage("Can't recognize biome for " + getID() + ": " + biomeString);
            }
        } catch (Exception error) {
            sendWarningErrorMessage("Can't parse custom flat generation pattern for " + getID() + ": " + text);
        }
        return blocksMap;
    }

    private @Nullable Biome getBiome(@Nullable String text) {
        if (text == null) return null;
        try {
            return Biome.valueOf(text);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public @NotNull String getExtensionId() {
        return "default";
    }

    @Override
    public @NotNull String getDescription() {
        return "Generates flat world by settings";
    }
}
