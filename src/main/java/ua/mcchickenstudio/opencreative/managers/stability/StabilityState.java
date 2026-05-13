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

package ua.mcchickenstudio.opencreative.managers.stability;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

public enum StabilityState {

    /**
     * This state allows players to create, connect worlds and launch a code.
     */
    FINE,
    /**
     * This state allows players to connect loaded worlds, but disallows to create, compile a code.
     */
    NOT_OKAY,
    /**
     * This state disallows players everything: browsing, connecting, compiling.
     */
    NIGHTMARE;

    public String getLocalized() {
        return getLocaleMessage("creative.stability." + name().toLowerCase().replace("_", "-"), false);
    }

}
