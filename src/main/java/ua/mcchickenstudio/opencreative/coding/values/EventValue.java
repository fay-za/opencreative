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

package ua.mcchickenstudio.opencreative.coding.values;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.ExtensionContent;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;

import static ua.mcchickenstudio.opencreative.utils.MessageUtils.getLocaleMessage;

/**
 * <h1>EventValue</h1>
 * This class represents a value, that can be got
 * from event, actions handler, action and target.
 * <p>
 * To create custom event value create a class,
 * that extends one of prepared: {@link TextEventValue},
 * {@link NumberEventValue}, {@link VectorEventValue},
 * {@link LocationEventValue}, {@link ItemEventValue},
 * {@link BooleanEventValue} and register with
 * {@link EventValues#registerEventValue(EventValue)}.
 */
public abstract class EventValue implements ExtensionContent {

    private final String id;
    private final ItemStack displayIcon;
    private final MenusCategory category;

    /**
     * Constructor of event value.
     *
     * @param id          short id of event value that will be used in signs and translations.
     *                    <p>
     *                    It must be lower-snake-cased, for example: "nickname", "entity_health".
     *                    If some of registered event values has same ID as new, it will be not added.
     * @param displayIcon icon of event value that will be displayed in event values list.
     * @param category    category of event value for event values list.
     */
    public EventValue(@NotNull String id, @NotNull ItemStack displayIcon, @NotNull MenusCategory category) {
        this.id = id.replace("-", "_").toLowerCase();
        this.displayIcon = displayIcon;
        this.category = category;
    }

    /**
     * Returns an icon that will be used
     * in event values menus.
     *
     * @return icon of event value to display.
     */
    public @NotNull ItemStack getDisplayIcon() {
        return displayIcon;
    }

    /**
     * Returns a category, that event
     * value belongs to.
     *
     * @return category where event value will be able.
     */
    public @NotNull MenusCategory getCategory() {
        return category;
    }

    /**
     * Returns a value that will be set instead of
     * event value name tag item.
     *
     * @param handler handler of action to get value.
     * @param action  action to get value.
     * @param entity  entity to get value, can be null.
     * @return string, number, boolean, item, location, vector, or null.
     */
    public abstract @Nullable Object getValue(@NotNull ActionsHandler handler, @NotNull Action action, @Nullable Entity entity);

    /**
     * Returns name of event value for displaying in
     * registry by converting id. Not used in menus.
     *
     * @return display name of event value.
     */
    public @NotNull String getName() {
        return StringUtils.capitalize(id.replace("_", " "));
    }

    /**
     * Returns id of event value, that will be used
     * to find it in registry.
     *
     * @return id of event value.
     */
    public final @NotNull String getID() {
        return id;
    }

    /**
     * Returns localized name of event value,
     * that will be used in menus.
     *
     * @return localized name.
     */
    public @NotNull String getLocaleName() {
        return getLocaleMessage("menus.developer.event-values.items." + this.getID().replace("_", "-") + ".name", false);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof EventValue value)) return false;
        return id.equals(value.id);
    }
}
