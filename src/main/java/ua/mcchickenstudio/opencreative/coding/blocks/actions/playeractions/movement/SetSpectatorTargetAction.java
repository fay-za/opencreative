package ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.movement;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.coding.arguments.Arguments;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.Target;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.playeractions.PlayerAction;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;

import java.util.Set;

public class SetSpectatorTargetAction extends PlayerAction {
    public SetSpectatorTargetAction(Executor executor, Target target, int x, Arguments args) {
        super(executor, target, x, args);
    }

    @Override
    public void executePlayer(@NotNull Player player) {
        String text = getArguments().getText("entity", " ", this);
        Set<Entity> entities = getEntitiesByNameOrUUID(text);
        if (entities.isEmpty()) return;
        Entity first = entities.iterator().next();
        player.setSpectatorTarget(first);
    }

    @Override
    public @NotNull ActionType getActionType() {
        return ActionType.PLAYER_SET_SPECTATOR_TARGET;
    }
}
