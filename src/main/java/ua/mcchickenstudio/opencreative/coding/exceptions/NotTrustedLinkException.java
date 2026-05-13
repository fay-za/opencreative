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

package ua.mcchickenstudio.opencreative.coding.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * <h1>NotTrustedLinkException</h1>
 * This class represents an exception, that happens when
 * specified link is not marked as trusted in config.yml.
 */
public final class NotTrustedLinkException extends RuntimeException {

    private final String link;

    public NotTrustedLinkException(@NotNull String link) {
        super("Link " + link + " is not trusted by server.");
        this.link = link;
    }

    public @NotNull String getLink() {
        return link;
    }

}
