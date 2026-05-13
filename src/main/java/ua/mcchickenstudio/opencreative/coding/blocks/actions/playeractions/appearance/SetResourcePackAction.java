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

package ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.appearance;

import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.packs.ResourcePack;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.exceptions.NotTrustedLinkException;
import ua.mcchickenstudio.opencreative.coding.exceptions.TooLongTextException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class SetResourcePackAction extends PlayerAction {

    public SetResourcePackAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(@NotNull Player player) {

        if (!OpenCreative.getSettings().getLobbySettings().shouldResetResourcePack()) {
            return;
        }

        String url = getArguments().getText("url", "", this);
        if (url.isEmpty() || !isAllowed(url)) return;

        List<ResourcePackInfo> packs = new ArrayList<>();

        // Checking server's resource pack
        ResourcePack serverPack = Bukkit.getServerResourcePack();
        if (serverPack != null) {
            ResourcePackInfo serverPackInfo = ResourcePackInfo.resourcePackInfo()
                    .uri(URI.create(serverPack.getUrl()))
                    .hash(serverPack.getHash() == null ? "" : serverPack.getHash())
                    .build();
            packs.add(serverPackInfo);
        }

        Component prompt = getArguments().getComponent("prompt", Component.empty(), this);
        String plainText = PlainTextComponentSerializer.plainText().serialize(prompt);
        if (plainText.length() > 256) {
            throw new TooLongTextException(256);
        }

        CompletableFuture<ResourcePackInfo> info = ResourcePackInfo.resourcePackInfo()
                .uri(URI.create(url))
                .computeHashAndBuild();

        info.thenAccept(pack -> {
            List<ResourcePackInfo> finalPacks = new ArrayList<>(packs);
            finalPacks.add(pack);
            ResourcePackRequest request = ResourcePackRequest.resourcePackRequest()
                    .packs(finalPacks)
                    .prompt(prompt)
                    .required(false)
                    .build();
            Bukkit.getScheduler().runTask(OpenCreative.getPlugin(),
                    () -> {
                        if (player.isOnline() && player.getWorld().equals(getWorld())) {
                            player.sendResourcePacks(request);
                        }
                    }
            );
        });
    }

    private boolean isAllowed(@NotNull String url) {
        /*
         * We check url, because some world owners
         * can use IP logger when player downloads
         * a resource pack from owner's site.
         */
        String checkUrl = url.toLowerCase().replaceAll("^https?://(www.)?", "");
        Set<String> allowedLinks = OpenCreative.getSettings().getAllowedResourcePackLinks();
        for (String allowed : allowedLinks) {
            if (checkUrl.startsWith(allowed)) {
                return true;
            }
        }
        throw new NotTrustedLinkException(url);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_SET_RESOURCE_PACK;
    }
}
