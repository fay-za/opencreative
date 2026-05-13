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

package ua.mcchickenstudio.opencreative.utils.world.cache;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.Collections;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendDebugError;
import static ua.mcchickenstudio.opencreative.utils.world.WorldUtils.isDevPlanet;

public class ChunkPacketListener extends PacketAdapter {

    public ChunkPacketListener(Plugin plugin) {
        super(plugin, ListenerPriority.HIGHEST,
                Collections.singletonList(PacketType.Play.Server.MAP_CHUNK),
                ListenerOptions.ASYNC);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        final World world = event.getPlayer().getWorld();
        if (!isDevPlanet(world)) return;
        try {
            final int
                    chunkX = event.getPacket().getIntegers().read(0),
                    chunkZ = event.getPacket().getIntegers().read(1);
            ChunkCache.preLoad(world, chunkX, chunkZ);
        } catch (Exception error) {
            sendDebugError("Cannot preload chunks.", error);
        }
    }
}
