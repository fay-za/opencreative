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

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * <h1>DisabledCodingPrompter</h1>
 * This class represents a coding prompter, that
 * does nothing, because it's disabled.
 */
public final class DisabledCodingPrompter implements CodingPrompter {

    @Override
    public @NotNull CompletableFuture<String> generateCode(@NotNull String nickname, @NotNull UUID uuid, @NotNull String text,
                                                           int actionsLimit) {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.complete("Disabled");
        return future;
    }


    @Override
    public void setToken(@NotNull String token) {
    }

    @Override
    public void init() {
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getName() {
        return "Disabled Coding Prompter";
    }

}
