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

package ua.mcchickenstudio.opencreative.coding.blocks.events.player.world;

import org.bukkit.entity.Player;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;

public final class PlayerPurchaseEvent extends WorldEvent {

    private final String id;
    private final String name;
    private final int price;
    private final boolean save;

    public PlayerPurchaseEvent(Player player, String id, String name, int price, boolean save) {
        super(player);
        this.id = id;
        this.name = name;
        this.price = price;
        this.save = save;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSave() {
        return save;
    }

    public int getPrice() {
        return price;
    }
}
