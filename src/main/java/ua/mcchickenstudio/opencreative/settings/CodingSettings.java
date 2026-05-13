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

package ua.mcchickenstudio.opencreative.settings;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.ActionType;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executor;
import ua.mcchickenstudio.opencreative.coding.blocks.executors.Executors;
import ua.mcchickenstudio.opencreative.coding.prompters.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.*;

/**
 * <h1>CodingSettings</h1>
 * This class represents a settings of developer's mode (/dev).
 */
public final class CodingSettings {

    private final Set<String> disabledEvents = new HashSet<>();
    private final Set<String> disabledActions = new HashSet<>();
    private final Set<String> disabledConditions = new HashSet<>();
    private boolean enabled = true;
    private boolean cancelChatOnValueSet = false;
    private boolean legacySelectionMenu = false;
    private int verticalPlatformStep = 5;
    private int legacyPlatformStep = 10;
    private int horizontalPlatformStep = 102;
    private boolean verticalPlatformNotchEnabled = false;
    private int verticalPlatformNotchWidth = 3;
    private boolean shiftBreakChainEnabled = true;
    private boolean shiftBreakChainCompactFull = true;
    private boolean ignoreActionsIfEntityNotInWorld = false;
    private boolean saveScriptsHistory = false;
    private int prompterMaxExecutors = 10;
    private int prompterTimeout = 120;

    /**
     * Loads settings of coding from configuration.
     */
    public void load() {
        FileConfiguration config = OpenCreative.getPlugin().getConfig();
        ConfigurationSection section = config.getConfigurationSection("coding");

        if (section == null) {
            sendCriticalErrorMessage("Can't load coding settings, section `coding` in config.yml is empty.");
            return;
        }

        enabled = section.getBoolean("enabled", true);

        legacySelectionMenu = section.getBoolean("old-selection-menu", false);
        cancelChatOnValueSet = section.getBoolean("cancel-chat-on-value-set", false);
        ignoreActionsIfEntityNotInWorld = section.getBoolean("ignore-actions-if-entity-not-in-world", false);
        verticalPlatformStep = Math.max(1, section.getInt("platforms-spacing.vertical", 5));
        legacyPlatformStep = Math.max(1, section.getInt("platforms-spacing.legacy", 10));
        horizontalPlatformStep = Math.max(101, section.getInt("platforms-spacing.horizontal", 102));
        verticalPlatformNotchEnabled = section.getBoolean("platforms-vertical-notch.enabled", false);
        verticalPlatformNotchWidth = Math.max(1, section.getInt("platforms-vertical-notch.width", 3));
        shiftBreakChainEnabled = section.getBoolean("shift-break-chain-enabled", true);
        shiftBreakChainCompactFull = section.getBoolean("shift-break-chain-compact-full", true);
        saveScriptsHistory = section.getBoolean("save-scripts-history", false);

        loadDisabledBlocks(section);
        setupPromptHandler(section);
    }

    /**
     * Loads disabled coding blocks list.
     *
     * @param config section with settings.
     */
    private void loadDisabledBlocks(@NotNull ConfigurationSection config) {
        disabledEvents.clear();
        disabledConditions.clear();
        disabledActions.clear();
        int count = 0;
        List<String> unknownBlocks = new ArrayList<>();
        for (String disabled : config.getStringList("disabled-actions")) {
            try {
                disabled = disabled.toUpperCase().replace("-", "_");
                ActionType type = ActionType.valueOf(disabled);
                disabledActions.add(type.name());
                count++;
            } catch (Exception ignored) {
                unknownBlocks.add(disabled);
            }
        }
        for (String disabled : config.getStringList("disabled-conditions")) {
            try {
                disabled = disabled.toUpperCase().replace("-", "_");
                ActionType type = ActionType.valueOf(disabled);
                disabledConditions.add(type.name());
                count++;
            } catch (Exception ignored) {
                unknownBlocks.add(disabled);
            }
        }
        for (String disabled : config.getStringList("disabled-events")) {
            disabled = disabled.toLowerCase().replace("-", "_");
            Executor executor = Executors.getInstance().getById(disabled);
            if (executor == null) {
                unknownBlocks.add(disabled);
                continue;
            }
            disabledEvents.add(executor.getID());
            count++;
        }
        if (count >= 1) {
            OpenCreative.getPlugin().getLogger().info("Disabled " + count + " coding blocks!");
        }
        if (!unknownBlocks.isEmpty()) {
            sendWarningErrorMessage("Can't recognize these coding blocks in disabled list, do they exist? " + unknownBlocks);
        }
    }

    /**
     * Loads prompt handler.
     *
     * @param config section with settings.
     */
    private void setupPromptHandler(@NotNull ConfigurationSection config) {
        prompterTimeout = config.getInt("prompt-handler.timeout", 120);
        prompterMaxExecutors = config.getInt("prompt-handler.executors-limit", 10);

        String type = config.getString("prompt-handler.type", "none");
        if (type.equalsIgnoreCase("none")) {
            OpenCreative.setCodingPrompter(new DisabledCodingPrompter());
            return;
        }

        String token = config.getString("prompt-handler.token", "");
        if (token.length() <= 10) {
            sendWarningErrorMessage("[CODING PROMPT] The token is not valid, disabling prompt handler.");
            OpenCreative.setCodingPrompter(new DisabledCodingPrompter());
        } else {
            switch (type.toLowerCase()) {
                case "chatgpt", "openai" -> OpenCreative.setCodingPrompter(new OpenAIPrompter());
                case "openrouter", "openrouterai" -> OpenCreative.setCodingPrompter(new OpenRouterPrompter());
                case "gemini", "google" -> OpenCreative.setCodingPrompter(new GeminiPrompter());
                default -> {
                    sendWarningErrorMessage("[CODING PROMPT] Unknown prompter: " + type + ", using disabled prompt handler.");
                    OpenCreative.setCodingPrompter(new DisabledCodingPrompter());
                    return;
                }
            }
            OpenCreative.getCodingPrompter().setToken(token);
        }

        if (OpenCreative.getCodingPrompter() instanceof PrompterModelCapable modelable) {
            String model = config.getString("prompt-handler.model", "");
            if (!model.isEmpty()) modelable.setModel(model);
        }

        CodingPrompter prompter = OpenCreative.getCodingPrompter();
        if (OpenCreative.getCodingPrompter().isEnabled()) {
            sendDebug("[CODING PROMPT] Using prompter (" + prompter.getName() + ")" +
                    (prompter instanceof PrompterModelCapable model ? " with model: " + model.getModel() : "")
                    + " for /env make");
        }
    }

    /**
     * Checks whether executor is disabled.
     *
     * @param event executor to check.
     * @return true - disabled, false - not.
     */
    public boolean isDisabledEvent(@NotNull Executor event) {
        return disabledEvents.contains(event.getID());
    }

    /**
     * Checks whether action is disabled.
     *
     * @param action action to check.
     * @return true - disabled, false - not.
     */
    public boolean isDisabledAction(@NotNull ActionType action) {
        return disabledActions.contains(action.name());
    }

    /**
     * Checks whether condition is disabled.
     *
     * @param condition condition to check.
     * @return true - disabled, false - not.
     */
    public boolean isDisabledCondition(@NotNull ActionType condition) {
        return disabledConditions.contains(condition.name());
    }

    /**
     * Checks whether coding should use legacy selection menu.
     *
     * @return true - will use old menu, false - not.
     */
    public boolean isLegacySelectionMenu() {
        return legacySelectionMenu;
    }

    /**
     * Checks whether coding mode is enabled and players
     * can enter it to develop worlds.
     *
     * @return true - enabled, false - disabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Checks whether messages, that changed values, should be
     * hidden for players in coding world.
     *
     * @return true - hide these messages, false - let them show.
     */
    public boolean isCancelChatOnValueSet() {
        return cancelChatOnValueSet;
    }

    /**
     * Returns maximum amount of event blocks for prompt handler.
     *
     * @return limit of events.
     */
    public int getPrompterMaxExecutors() {
        return prompterMaxExecutors;
    }

    /**
     * Returns how much time prompt handler can think
     * before it will cancel and fail the request.
     *
     * @return limit of thinking time.
     */
    public int getPrompterTimeout() {
        return prompterTimeout;
    }

    /**
     * Checks whether any action will be not executed, if
     * target entity is not in planet's world.
     *
     * @return true - all actions require entity in same world<p>
     * false - only entity and player actions.
     */
    public boolean isIgnoreActionsIfEntityNotInWorld() {
        return ignoreActionsIfEntityNotInWorld;
    }

    /**
     * Returns Y-distance between generated vertical platforms.
     *
     * @return spacing in blocks.
     */
    public int getVerticalPlatformStep() {
        return verticalPlatformStep;
    }

    /**
     * Returns Y-distance between generated legacy platforms.
     *
     * @return spacing in blocks.
     */
    public int getLegacyPlatformStep() {
        return legacyPlatformStep;
    }

    /**
     * Returns X/Z-distance between generated horizontal platforms.
     *
     * @return spacing in blocks.
     */
    public int getHorizontalPlatformStep() {
        return horizontalPlatformStep;
    }

    /**
     * Checks whether vertical platform generator should cut a side notch.
     *
     * @return true - carve notch, false - keep full floor.
     */
    public boolean isVerticalPlatformNotchEnabled() {
        return verticalPlatformNotchEnabled;
    }

    /**
     * Returns notch width for vertical platform generator.
     *
     * @return width in blocks from platform edge.
     */
    public int getVerticalPlatformNotchWidth() {
        return verticalPlatformNotchWidth;
    }

    /**
     * Checks whether sneaking block-break should remove full multi-action chain.
     *
     * @return true - enabled, false - disabled.
     */
    public boolean isShiftBreakChainEnabled() {
        return shiftBreakChainEnabled;
    }

    /**
     * Checks whether shift chain removal should compact entire remaining line.
     *
     * @return true - compact fully, false - single vanilla move only.
     */
    public boolean isShiftBreakChainCompactFull() {
        return shiftBreakChainCompactFull;
    }

    /**
     * Checks whether codeScript.yml will be copied to
     * /plugins/OpenCreative/history/ folder.
     *
     * @return true - will be copied, false - not.
     */
    public boolean shouldSaveScriptsHistory() {
        return saveScriptsHistory;
    }
}
