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

package ua.mcchickenstudio.opencreative.indev.messenger;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VelocityMessenger implements Messenger, PluginMessageListener {

    private final Map<UUID, Integer> connectPlayerToPlanetsOnJoinRequests = new HashMap<>();

    @Override
    public @Nullable Integer getPlanetToConnectPlayerOnJoin(@NotNull UUID uuid) {
        return 0;
    }

    @Override
    public void clearRequestsOfPlayer(@NotNull UUID uuid) {

    }

    @Override
    public void init() {
        //Bukkit.getMessenger().registerOutgoingPluginChannel(OpenCreative.getPlugin(), "OpenCreative");
        //Bukkit.getMessenger().registerIncomingPluginChannel(OpenCreative.getPlugin(), "OpenCreative", this);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "";
    }

    public @NotNull String fromBytesToString(byte[] message) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        return in.readUTF();
    }


    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        if (!channel.equals("OpenCreative")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String action = in.readUTF();

        String worldName;
        try {
            worldName = in.readUTF();
        } catch (Exception e) {
            worldName = null;
        }
    }

    public void send(@NotNull Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF("worldname");
        player.sendPluginMessage(OpenCreative.getPlugin(), "OpenCreative", out.toByteArray());
    }
}
