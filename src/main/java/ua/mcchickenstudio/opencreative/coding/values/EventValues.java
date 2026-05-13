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

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Action;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionsHandler;
import ua.mcchickenstudio.opencreative.coding.menus.MenusCategory;
import ua.mcchickenstudio.opencreative.coding.values.attributes.*;
import ua.mcchickenstudio.opencreative.coding.values.entity.*;
import ua.mcchickenstudio.opencreative.coding.values.events.*;
import ua.mcchickenstudio.opencreative.coding.values.human.*;
import ua.mcchickenstudio.opencreative.coding.values.living.*;
import ua.mcchickenstudio.opencreative.coding.values.player.*;
import ua.mcchickenstudio.opencreative.coding.values.world.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendDebug;

/**
 * <h1>EventValues</h1>
 * This class represents registry of event values,
 * that can register new event values for coding.
 * <p>
 * To get instance use {@link #getInstance()}.
 * To add custom event value create a class, that
 * extends one of prepared: {@link TextEventValue},
 * {@link NumberEventValue}, {@link VectorEventValue},
 * {@link LocationEventValue}, {@link ItemEventValue},
 * {@link BooleanEventValue}, and register it with
 * {@link #registerEventValue(EventValue)} method.
 */
public final class EventValues {

    private static EventValues instance;
    private final List<EventValue> eventValues = new LinkedList<>();

    /**
     * Returns instance of event values controller class.
     *
     * @return instance of event values.
     */
    public synchronized static @NotNull EventValues getInstance() {
        if (instance == null) {
            instance = new EventValues();
            instance.registerDefaults();
        }
        return instance;
    }

    /**
     * Registers event value, that will be replaced in coding.
     *
     * @param value event value to register.
     */
    public void registerEventValue(@NotNull EventValue value) {
        EventValue existing = getById(value.getID());
        if (existing != null) {
            sendDebug("[VALUES] Can't register event value " + value.getName() + " (from " + value.getExtensionId() + "), "
                    + "because there's already registered event value " + existing.getName() + " (from " + existing.getExtensionId() + ") "
                    + "with same ID: " + value.getID());
            return;
        }
        sendDebug("[VALUES] Registered event value: " + value.getName() + " (from " + value.getExtensionId() + ")");
        eventValues.add(value);
    }

    /**
     * Registers event values, that will be replaced in coding.
     *
     * @param values event values to register.
     */
    public void registerEventValue(@NotNull EventValue... values) {
        for (EventValue value : values) {
            registerEventValue(value);
        }
    }

    /**
     * Unregisters event value if list contains it.
     *
     * @param value event value to unregister.
     */
    @SuppressWarnings("unused")
    public void unregisterEventValue(@NotNull EventValue value) {
        eventValues.remove(value);
    }

    /**
     * Returns a copy of list that contains all registered event values.
     *
     * @return event values list.
     */
    public @NotNull List<EventValue> getEventValues() {
        return new ArrayList<>(eventValues);
    }

    private void registerDefaults() {
        registerEventValue(new WorldNameValue(), new WorldDescriptionValue(), new WorldOnlineValue(),
                new WorldCustomIdValue(), new WorldSpawnValue(), new WorldIdValue(), new WorldIconValue(),
                new WorldRatingValue(), new UnixTimeValue(), new UnixTimeHoursValue(), new UnixTimeMinutesValue(),
                new UnixTimeSecondsValue(), new WorldEntitiesAmountValue(), new WorldEntitiesAmountLimitValue(),
                new WorldLastRedstoneOperationsValue(), new WorldRedstoneOperationsLimitValue(),
                new WorldVariablesAmountValue(), new WorldVariablesAmountLimitValue(), new WorldBlockEditsAmountValue(),
                new WorldBlockEditsLimitValue(), new ClearWeatherDurationValue(), new ThunderWeatherDurationValue(), new WorldTimeValue());
        registerEventValue(new BedEnterResultValue(), new BedEventValue(), new EventItemValue(),
                new EventNewItemValue(), new FishingStateValue(), new BlockInteractionTypeValue(), new CursorItemValue(),
                new ClickTypeValue(), new ClickedSlotValue(), new OldSlotValue(), new NewSlotValue(),
                new ChatMessageValue(), new BlockMaterialValue(), new BlockLocationValue(), new TransferKeyValue(),
                new TransferVariableValue(), new WebUrlValue(), new WebResponseCodeValue(), new WebResponseValue(),
                new PurchaseIdValue(), new PurchaseNameValue(), new PurchasePriceValue(), new PurchaseSaveValue(),
                new FoodLevelValue(), new DamageCauseValue(), new DamageValue());
        registerEventValue(new ClientBrandValue(), new HumanHungerValue(), new HumanLastDeathLocationValue(),
                new PlayerPingValue(), new PlayerExperienceValue(), new PlayerExperienceLevelValue(), new PlayerTotalExperienceValue(),
                new HumanGameModeValue(), new LocaleCountryValue(), new LocaleDisplayCountryValue(), new LocaleLanguageValue(),
                new LocaleDisplayLanguageValue(), new PlayerCompassTargetValue());
        registerEventValue(new EntityNameValue(), new EntityTypeValue(), new EntityUUIDValue(), new EntityLocationValue(),
                new EntityVelocityValue(), new HumanItemInMainHandValue(), new HumanItemInOffHandValue(), new HumanHelmetValue(),
                new HumanChestplateValue(), new HumanLeggingsValue(), new HumanBootsValue(), new LivingHealthValue(), new EyeLocationValue(),
                new TargetEntityValue(), new TargetBlockValue(), new PlayerWalkSpeedValue(), new PlayerFlightSpeedValue(),
                new LivingLastDamageValue(), new EntityLastDamageCauseValue(), new LivingMaxHealthValue(),
                new EntityFallDistanceValue(), new LivingNoDamageTicksValue(), new EntityFireTicksValue(),
                new EntityFreezeTicksValue(), new LivingMaxNoDamageTicksValue(), new LivingCanPickupItemValue(),
                new LivingArrowsInBodyValue(), new LivingShieldBlockingDelayValue(), new LivingBeeStingerCooldownValue(),
                new LivingRemainingAirValue(), new LivingMaximumAirValue());

        registerEventValue(new EntityGravityValue(), new BurningTimeValue(), new EntityStepHeightValue(),
                new KnockbackResistanceValue(), new ExplosionKnockbackResistanceValue(), new AttackKnockbackValue(),
                new AttackDamageValue(), new ArmorToughnessValue(), new EntityScaleValue(), new AttackSpeedValue(),
                new ArmorPointsValue());
        registerEventValue(new EntityTargetValue(), new PassengersValue(), new EntityItemValue(), new EntityFacingValue(),
                new EntityLocationDirectionValue(), new EntityLocationPitchValue(), new EntityLocationYawValue(),
                new EntityLocationXValue(), new EntityLocationYValue(), new EntityLocationZValue(),
                new BlockBeneathValue(), new BlockAboveValue(), new ProjectileOwnerValue(), new EntityVehicleValue(), new EntitySpawnReasonValue(),
                new EntityPoseValue(), new EntityWidthZValue(), new EntityWidthXValue(), new EntityFuseTicksValue(),
                new EntityHeightValue(), new EntityAgeValue(), new EntityTicksLivedValue(), new MerchantTradesCountValue(),
                new LivingPotionsValue(), new HolderInventoryValue(), new TargetFluidValue(), new LivingBodyYawValue(),
                new LivingLeashHolderValue(), new SaddleItemValue(), new AbsorptionAmountValue());
        registerEventValue(new MenuItemsValue(), new HotBarItemsValue(), new PlayerMainHandValue(),
                new PlayerChatVisibilityValue(), new PlayerClientViewDistanceValue());
        registerEventValue(new ServerTicksPerSecondValue(), new WorldSizeValue(), new WorldGameTimeValue(),
                new WorldMoonPhaseValue(), new WorldTargetsUUID(), new WorldTargetsCount(), new WorldTargetsName());
    }

    /**
     * Returns list of event values, that have same menu category.
     *
     * @param menusCategory menu category.
     * @return list of event values with specified menu category.
     */
    public @NotNull List<EventValue> getByCategories(@NotNull MenusCategory menusCategory) {
        List<EventValue> list = new LinkedList<>();
        for (EventValue name : eventValues) {
            if (name.getCategory() == menusCategory) {
                list.add(name);
            }
        }
        return list;
    }

    /**
     * Returns list of all menu categories of all event values.
     *
     * @return list of menu categories.
     */
    public @NotNull List<MenusCategory> getCategories() {
        List<MenusCategory> list = new LinkedList<>();
        for (EventValue value : eventValues) {
            if (list.contains(value.getCategory())) continue;
            list.add(value.getCategory());
        }
        return list;
    }

    /**
     * Checks if event value with specified ID exists in registry.
     *
     * @param id id of event value.
     * @return true - exists, false - not exists.
     */
    public boolean exists(@NotNull String id) {
        return getById(id) != null;
    }

    /**
     * Checks if event value with specified class exists in registry.
     *
     * @param clazz class of event value.
     * @return true - exists, false - not exists.
     */
    public boolean exists(@NotNull Class<? extends EventValue> clazz) {
        return getByClass(clazz) != null;
    }

    /**
     * Returns event value from registry by specified class
     * if it exists, otherwise will return null.
     *
     * @param clazz class to get event value.
     * @return event value - if exists, or null - not exists.
     */
    public @Nullable EventValue getByClass(@NotNull Class<? extends EventValue> clazz) {
        for (EventValue eventValue : eventValues) {
            if (eventValue.getClass().equals(clazz)) {
                return eventValue;
            }
        }
        return null;
    }

    /**
     * Returns event value from registry by specified id
     * if it exists, otherwise will return null.
     *
     * @param id id to get event value.
     * @return event value - if exists, or null - not exists.
     */
    public @Nullable EventValue getById(@NotNull String id) {
        for (EventValue eventValue : eventValues) {
            if (eventValue.getID().equals(id)) {
                return eventValue;
            }
        }
        return null;
    }

    /**
     * Returns replaced value of event value if it exists,
     * otherwise it will return null.
     * <p>
     * Replaced value can also be null by some conditions.
     *
     * @param id      id of event value.
     * @param handler handler of action.
     * @param action  action, where it executes.
     * @param entity  entity, or null.
     * @return value of event value, or null.
     */
    public @Nullable Object getValue(@NotNull String id, @NotNull ActionsHandler handler, @NotNull Action action, @Nullable Entity entity) {
        EventValue value = getById(id);
        if (value == null) return null;
        return value.getValue(handler, action, entity);
    }

    /**
     * Returns replaced value of event value if it exists,
     * otherwise it will return null.
     * <p>
     * Replaced value can also be null by some conditions.
     *
     * @param clazz   class of event value.
     * @param handler handler of action.
     * @param action  action, where it executes.
     * @param entity  entity, or null.
     * @return value of event value, or null.
     */
    public @Nullable Object getValue(@NotNull Class<? extends EventValue> clazz, @NotNull ActionsHandler handler, @NotNull Action action, @NotNull Entity entity) {
        EventValue value = getByClass(clazz);
        if (value == null) return null;
        if (value.getCategory() == MenusCategory.PLAYER || value.getCategory() == MenusCategory.ENTITY) {
            if (!entity.getWorld().equals(action.getExecutor().getPlanet())) return null;
        }
        return value.getValue(handler, action, entity);
    }

}
