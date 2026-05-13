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


package ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.world.phys.data;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.utils.async.AsyncScheduler;
import ua.mcchickenstudio.opencreative.utils.millennium.types.EvictingList;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

// Made by pawsashatoy :)
public class PhysService {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(40,
            new ThreadFactoryBuilder().setNameFormat("opencreative-phys-thread-%d").build());
    private static final Map<Integer, List<PhysObject>> objects = new ConcurrentHashMap<>();

    private PhysService() {
    }

    public static void add(final PhysObject object, final int limit) {
        final World world = object.getWorld();
        final int hash = Objects.hashCode(world.getName());
        if (!objects.containsKey(hash)) objects.put(hash, new EvictingList<>(limit));
        objects.get(hash).add(object);
    }

    public static void run() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(OpenCreative.getPlugin(), () -> AsyncScheduler.run(() -> {
            for (final List<PhysObject> objects : objects.values()) {
                if (objects.isEmpty()) continue;
                final Set<PhysObject> toDelete = new HashSet<>();
                for (final PhysObject object : objects) {
                    object.tick();
                    if (!object.isLiving()) toDelete.add(object);
                }
                objects.removeAll(toDelete);
            }
        }, scheduler), 1L, 1L);
    }
}
