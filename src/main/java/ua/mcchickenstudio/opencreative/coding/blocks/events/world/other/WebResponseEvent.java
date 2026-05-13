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

package ua.mcchickenstudio.opencreative.coding.blocks.events.world.other;

import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldEvent;
import ua.mcchickenstudio.opencreative.planets.Planet;

public final class WebResponseEvent extends WorldEvent {

    private final String url;
    private final int code;
    private final String response;

    public WebResponseEvent(Planet planet, String url, int code, String response) {
        super(planet);
        this.url = url;
        this.code = code;
        this.response = response;
    }

    public String getUrl() {
        return url;
    }

    public int getCode() {
        return code;
    }

    public String getResponse() {
        return response;
    }
}
