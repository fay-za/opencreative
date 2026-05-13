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

package ua.mcchickenstudio.opencreative.coding.prompters;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.managers.Manager;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * <h1>CodingPrompter</h1>
 * This interface represents a coding prompter, that
 * will generate a code by players prompts.
 */
public interface CodingPrompter extends Manager {

    /**
     * Generates a code by player's prompt. Returns
     *
     * @param nickname     name of player.
     * @param uuid         uuid of player.
     * @param prompt       prompt to generate code.
     * @param actionsLimit limit of actions per executor.
     * @return code script YAML, or reason why code refused to generate.
     */
    @NotNull CompletableFuture<String> generateCode(@NotNull String nickname,
                                                    @NotNull UUID uuid,
                                                    @NotNull String prompt,
                                                    int actionsLimit);

    /**
     * Sets the token for coding prompter.
     *
     * @param token new token.
     */
    void setToken(@NotNull String token);

}
