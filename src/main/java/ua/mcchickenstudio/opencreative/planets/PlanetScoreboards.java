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

package ua.mcchickenstudio.opencreative.planets;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>PlanetScoreboards</h1>
 * This class represents a scoreboards manager, that contains
 * methods to register, unregister and modify scoreboards.
 * <p>
 * Every scoreboard has dummy objective called "score"
 * with sidebar display type.
 */
public class PlanetScoreboards {

    private final Planet planet;
    private final Map<String, Scoreboard> scoreboards = new HashMap<>();

    public PlanetScoreboards(@NotNull Planet planet) {
        this.planet = planet;
    }

    /**
     * Returns map of names and scoreboards.
     *
     * @return map of names and scoreboards.
     */
    public Map<String, Scoreboard> getMap() {
        Map<String, Scoreboard> scoreboardMap = new HashMap<>();
        for (String name : scoreboards.keySet()) {
            scoreboardMap.put(name, scoreboards.get(name));
        }
        return scoreboardMap;
    }

    /**
     * Registers new scoreboard, or replaces old with new one.
     *
     * @param name       name of scoreboard.
     * @param scoreboard scoreboard to register.
     */
    public void registerScoreboard(@NotNull String name, @NotNull Scoreboard scoreboard) {
        scoreboards.put(name, scoreboard);
    }

    /**
     * Unregisters scoreboard from world by name.
     *
     * @param name scoreboard to remove.
     */
    public void unregisterScoreboard(@NotNull String name) {
        Scoreboard scoreboard = getScoreboard(name);
        scoreboards.remove(name);
        if (scoreboard == null) return;
        destroyScoreboard(scoreboard);
    }

    /**
     * Unregisters scoreboard from world.
     *
     * @param scoreboard scoreboard to remove.
     */
    @SuppressWarnings("unused")
    public void unregisterScoreboard(@NotNull Scoreboard scoreboard) {
        String board = null;
        for (String name : scoreboards.keySet()) {
            if (scoreboard.equals(scoreboards.get(name))) {
                board = name;
            }
        }
        if (board != null) scoreboards.remove(board);
    }

    /**
     * Removes objective from scoreboard and hides it from all players.
     * <p>
     * To unregister scoreboard, use {@link #unregisterScoreboard(Scoreboard)}.
     *
     * @param scoreboard scoreboard to destroy.
     */
    public void destroyScoreboard(@NotNull Scoreboard scoreboard) {
        Objective objective = scoreboard.getObjective("score");
        if (objective != null) {
            objective.unregister();
        }
        for (Player player : planet.getPlayers()) {
            if (player.getScoreboard().equals(scoreboard)) {
                player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
        }
    }

    /**
     * Clears all scores from scoreboard.
     *
     * @param scoreboard scoreboard to clear scores.
     */
    public void clearScores(@NotNull Scoreboard scoreboard) {
        Objective objective = scoreboard.getObjective("score");
        if (objective == null) return;
        NumberFormat format = objective.numberFormat();
        Component displayName = objective.displayName();
        objective.unregister();

        objective = scoreboard.registerNewObjective("score", Criteria.DUMMY, displayName);
        objective.numberFormat(format);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }


    /**
     * Returns scoreboard by name, or null - if not exists.
     *
     * @param scoreboard name of scoreboard.
     * @return scoreboard, or null - if not found.
     */
    public @Nullable Scoreboard getScoreboard(@NotNull String scoreboard) {
        return scoreboards.get(scoreboard);
    }

    /**
     * Returns amount of all scoreboards.
     *
     * @return amount of scoreboards.
     */
    public int getAmount() {
        return scoreboards.size();
    }

    /**
     * Checks whether player sees any active scoreboard from this planet.
     *
     * @param player player to check.
     * @return true - player sees world's scoreboard, false - doesn't see.
     */
    public boolean hasActiveScoreboard(@NotNull Player player) {
        if (player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) return false;
        for (Scoreboard scoreboard : scoreboards.values()) {
            if (player.getScoreboard().equals(scoreboard)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes and unregisters all scoreboards.
     */
    public void clear() {
        if (scoreboards.isEmpty()) return;
        for (Scoreboard scoreboard : scoreboards.values()) {
            destroyScoreboard(scoreboard);
        }
        scoreboards.clear();
    }

}
