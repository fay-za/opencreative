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

package ua.mcchickenstudio.opencreative.wanders;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.planets.Planet;

/**
 * <h1>Wander</h1>
 * This class represents wander, that could be online.
 * Wander is a player, who plays on planets.
 * He has nickname, description, gender, favorite
 * worlds and last played world.
 */
public final class Wander extends OfflineWander implements Audience {

    private final Player player;
    private final GriefStats griefStats;
    private boolean connectingToPlanet;

    public Wander(@NotNull Player player) {
        super(player);
        this.player = player;
        this.griefStats = new GriefStats();
    }

    /**
     * Returns statistics of destructive changes to world.
     * <p>
     * It will be cleared, when player leaves the world.
     *
     * @return grief statistics.
     */
    public @NotNull GriefStats getGriefStats() {
        return griefStats;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Returns current planet, where wander is currently in.
     *
     * @return current planet, or null.
     */
    public @Nullable Planet getCurrentPlanet() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return null;
        return OpenCreative.getPlanetsManager().getPlanetByPlayer(player);
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        player.sendMessage(message);
    }

    @Override
    public void sendActionBar(@NotNull Component message) {
        player.sendActionBar(message);
    }

    @Override
    public void showTitle(@NotNull Title title) {
        player.showTitle(title);
    }

    /**
     * Sets whether wander is currently connecting to planet.
     *
     * @param connectingToPlanet whether wander is connecting to planet or not.
     */
    public void setConnectingToPlanet(boolean connectingToPlanet) {
        this.connectingToPlanet = connectingToPlanet;
    }

    /**
     * Checks whether wander is trying to connect to some planet.
     *
     * @return true - is connecting, false - not connecting.
     */
    public boolean isConnectingToPlanet() {
        return connectingToPlanet;
    }
}
