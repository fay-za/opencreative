package ua.mcchickenstudio.opencreative.coding.blocks.events;

import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.planets.Planet;

/**
 * A basic concrete implementation of WorldEvent used for
 * internal code initialization (like Structures).
 */
public class InternalTriggerEvent extends WorldEvent {
    public InternalTriggerEvent(@NotNull Planet planet) {
        super(planet);
    }
}