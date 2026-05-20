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

package ua.mcchickenstudio.opencreative;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ua.mcchickenstudio.opencreative.commands.*;
import ua.mcchickenstudio.opencreative.commands.minecraft.*;
import ua.mcchickenstudio.opencreative.commands.world.*;
import ua.mcchickenstudio.opencreative.commands.world.modes.*;
import ua.mcchickenstudio.opencreative.commands.world.reputation.*;
import ua.mcchickenstudio.opencreative.wanders.OfflineWander;
import ua.mcchickenstudio.opencreative.wanders.Wander;
import ua.mcchickenstudio.opencreative.coding.prompters.*;
import ua.mcchickenstudio.opencreative.listeners.CreativeListener;
import ua.mcchickenstudio.opencreative.listeners.creative.PlanetListener;
import ua.mcchickenstudio.opencreative.listeners.entity.*;
import ua.mcchickenstudio.opencreative.listeners.player.*;
import ua.mcchickenstudio.opencreative.listeners.world.*;
import ua.mcchickenstudio.opencreative.managers.blocks.BlocksManager;
import ua.mcchickenstudio.opencreative.managers.disguises.DisguiseManager;
import ua.mcchickenstudio.opencreative.managers.downloader.*;
import ua.mcchickenstudio.opencreative.managers.economy.*;
import ua.mcchickenstudio.opencreative.managers.hints.*;
import ua.mcchickenstudio.opencreative.managers.modules.*;
import ua.mcchickenstudio.opencreative.managers.packets.PacketManager;
import ua.mcchickenstudio.opencreative.settings.Sounds;
import ua.mcchickenstudio.opencreative.utils.world.WorldUtils;
import ua.mcchickenstudio.opencreative.utils.world.platforms.*;
import ua.mcchickenstudio.opencreative.managers.stability.*;
import ua.mcchickenstudio.opencreative.managers.updater.*;
import ua.mcchickenstudio.opencreative.menus.Menus;
import ua.mcchickenstudio.opencreative.managers.space.*;
import ua.mcchickenstudio.opencreative.settings.Settings;
import ua.mcchickenstudio.opencreative.utils.FileUtils;
import ua.mcchickenstudio.opencreative.utils.PlayerUtils;
import ua.mcchickenstudio.opencreative.utils.hooks.HookUtils;
import ua.mcchickenstudio.opencreative.utils.hooks.Metrics;
import ua.mcchickenstudio.opencreative.coding.blocks.events.WorldListener;
import ua.mcchickenstudio.opencreative.coding.blocks.actions.worldactions.world.phys.data.PhysService;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.parseException;
import static ua.mcchickenstudio.opencreative.utils.ErrorUtils.sendCriticalErrorMessage;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.isEntityInLobby;
import static ua.mcchickenstudio.opencreative.utils.PlayerUtils.teleportToLobby;

/**
 * This class represents OpenCreative+ java plugin for PaperMC.
 * Only for loading, enabling and disabling plugin. Contains
 * general information about plugin's version and codename.
 *
 * @author McChicken Studio
 */
public final class OpenCreative extends JavaPlugin {

    private static OpenCreative plugin;
    private final Map<UUID, Wander> wanders = new HashMap<>();

    private Settings settings;
    private Economy economy;
    private Updater updater;
    private PacketManager packet;
    private PlanetsManager space;
    private ModuleManager moduler;
    private StabilityManager watchdog;
    private DisguiseManager disguiser;
    private BlocksManager blocks;
    private HintManager hints;
    private DownloadManager downloader;
    private DevPlatformer devPlatformer;
    private CodingPrompter prompter;

    private static final String version = "6.0.0 Preview";
    private static final String codename = "Well, it's possible";

    /**
     * Plugin load operations.
     *
     * @see #onEnable
     */
    @Override
    public void onLoad() {
        getLogger().info(String.join("\n",
                "", "",
                "This software was made by Ukrainians, suffering from never-ending air alerts, explosions, and deaths.",
                "We're AGAINST THE WAR. This software IS NOT DESIGNED for those who support killing and robbing another country.",
                "",
                "Let us have fun, like players who create their worlds...",
                "McChicken Studio 2017–2026",
                ""
        ));
    }

    /**
     * Plugin startup operations.
     * <p>
     * Loads settings, registers commands and events,
     * starts managers and notifies players about startup.
     *
     * @see #onDisable
     **/
    @Override
    public void onEnable() {
        plugin = this;
        long startTime = System.currentTimeMillis();
        logStartup();

        loadCore();
        loadManagers();

        finalizeStartup(startTime);
        new Metrics(this, 22001);
    }

    /**
     * Plugin shutdown operations.
     * <p>
     * Unloads worlds when plugin is being disabled.
     *
     * @see #onEnable
     */
    @Override
    public void onDisable() {
        getLogger().info("Shutting down OpenCreative+, please wait...");
        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(
                        MiniMessage.miniMessage().deserialize(
                                " \n<white> Shutting down Open<gradient:#dbdbdb:#A3E2FF>Creative</gradient><color:#74D3FF>+ <gray>" + version + "<white>, please wait...\n "
                        ));
                if (WorldUtils.isPlanet(player.getWorld())) {
                    teleportToLobby(player);
                }
            }
            FileUtils.unloadPlanets();
            downloader.shutdown();
        } catch (Exception error) {
            OpenCreative.getPlugin().getLogger().severe("Failed to unload OpenCreative+ :(" + parseException(error, false));
        }
        getLogger().info(String.join("\n",
                "",
                "Goodbye from OpenCreative+",
                "",
                " " + codename,
                "  Made by McChicken Studio 2017–2026",
                ""
        ));
    }


    /**
     * Get a plugin instance for operations with it.
     * <p>
     * Useful for accessing planets manager, or settings.
     *
     * @return plugin instance.
     **/
    public static @NotNull OpenCreative getPlugin() {
        return plugin;
    }

    /**
     * Notifies console and players about OpenCreative+ startup.
     */
    private void logStartup() {
        getLogger().info("Starting OpenCreative+ " + version + ": " + codename + ", please wait...");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
            player.showTitle(Title.title(
                    MiniMessage.miniMessage().deserialize("<white>Open<gradient:#dbdbdb:#A3E2FF>Creative</gradient><color:#74D3FF>+ <gray>" + version),
                    Component.text("§f" + codename + "..."),
                    Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(5), Duration.ofSeconds(0))
            ));
        }
    }

    /**
     * Loads OpenCreative+ settings, localization files,
     * registers commands and events.
     */
    private void loadCore() {
        settings = new Settings();
        HookUtils.loadHooks();
        settings.load(false);
        registerCommands();
        registerEvents();
        //Ticker.runTicker();
        FileUtils.loadLocales();
        PlayerUtils.loadPermissions();
    }

    /**
     * Loads and assigns OpenCreative+ managers.
     */
    private void loadManagers() {
        space = new Space();
        space.init();
        moduler = new Moduler();
        moduler.init();
        if (devPlatformer == null) devPlatformer = new HorizontalPlatformer();
        if (prompter == null) prompter = new DisabledCodingPrompter();
        if (watchdog == null) watchdog = new DisabledWatchdog();
        if (downloader == null) downloader = new DisabledDownloader();
        if (economy == null) economy = new DisabledEconomy();

        FileUtils.loadPlanets();
        PhysService.run();
        FileUtils.loadModules();
        watchdog.init();
        downloader.init();

        updater = new HangarUpdater();
        updater.init();
        packet = HookUtils.getPacketManager();
        packet.init();
        blocks = HookUtils.getBlocks();
        blocks.init();
        disguiser = HookUtils.getDisguises();
        disguiser.init();
        hints = new Hints();
        hints.init();
    }

    /**
     * Notifies console and players about successful
     * startup of OpenCreative+.
     *
     * @param startTime timestamp of the beginning of startup.
     */
    private void finalizeStartup(long startTime) {
        long loadedTime = System.currentTimeMillis() - startTime;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (WorldUtils.isPlanet(player.getWorld())) {
                teleportToLobby(player);
            } else if (isEntityInLobby(player)) {
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                Sounds.LOBBY.play(player);
                player.clearTitle();
                player.sendMessage(
                        MiniMessage.miniMessage().deserialize("\n <white>Open<gradient:#dbdbdb:#A3E2FF>Creative</gradient><color:#74D3FF>+ <gray>" + version + " <white>is loaded <green>:) \n ")
                );
            }
        }
        getServer().sendActionBar(
                MiniMessage.miniMessage().deserialize(
                        "<white>Open<gradient:#dbdbdb:#A3E2FF>Creative</gradient><color:#74D3FF>+ <gray>" + version + "<white> is loaded for " + loadedTime + " ms."
                )
        );
        getLogger().info(String.join("\n",
                "OpenCreative+ " + version + ": " + codename + " is loaded for " + loadedTime + " ms.",
                "",
                " Welcome to OpenCreative+ " + version + "!",
                "",
                "  Running on " + Bukkit.getMinecraftVersion() + " server",
                "  Current time " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()),
                isChristmas() ? "  Ho-ho-ho! Merry Christmas, server owners! :-) ❆" :
                        isHalloween() ? "  Spo-o-o-oky Halloween, server owners! O_o 🎃" : "",
                "  " + codename,
                "  Made by McChicken Studio 2017–2026",
                ""
        ));
        if (settings.isFirstLaunch()) {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                Bukkit.getConsoleSender().sendMessage(String.join("\n",
                        "[" + getLogger().getName() + "] Thank you for installing §fOpen§7Creative§b+§f!",
                        "",
                        " You can always get help on our Discord server:",
                        " §bhttps://discord.gg/sSFCXUeq63",
                        "",
                        " §fCheck wiki pages about setting up:",
                        " §6https://gitlab.com/eagles-creative/opencreative/-/wikis/home",
                        ""
                ));
            }, 30L);
        }
    }

    /**
     * Registers plugin's commands and their tab completer. If some
     * commands failed to register, it notifies, but doesn't disable
     * entire plugin.
     */
    private void registerCommands() {
        this.getLogger().info("Registering OpenCreative+ commands...");
        int registeredCommands = 0;
        Map<String, Class<? extends CommandExecutor>> commands = new HashMap<>();
        commands.put("creative", CreativeCommand.class);
        commands.put("spawn", SpawnCommand.class);
        commands.put("menu", MenuCommand.class);
        commands.put("world", WorldCommand.class);
        commands.put("chat", ChatCommand.class);
        commands.put("join", JoinCommand.class);
        commands.put("ad", AdvertisementCommand.class);
        commands.put("play", PlayCommand.class);
        commands.put("build", BuildCommand.class);
        commands.put("dev", DevCommand.class);
        commands.put("environment", EnvironmentCommand.class);
        commands.put("like", LikeCommand.class);
        commands.put("dislike", DislikeCommand.class);
        commands.put("locate", LocateCommand.class);
        commands.put("gamemode", GamemodeCommand.class);
        commands.put("give", GiveCommand.class);
        commands.put("teleport", TeleportCommand.class);
        commands.put("edit", EditCommand.class);
        commands.put("playsound", PlaySoundCommand.class);
        commands.put("stopsound", StopSoundCommand.class);
        commands.put("time", TimeCommand.class);
        commands.put("weather", WeatherCommand.class);
        commands.put("value", ValueCommand.class);
        commands.put("module", ModuleCommand.class);
        commands.put("jointo", JoinToCommand.class);
        commands.put("ownworlds", OwnMenuCommand.class);
        commands.put("showoriginal", ShowOriginalCommand.class);
        for (String commandName : commands.keySet()) {
            PluginCommand command =  getCommand(commandName);
            if (command != null) {
                try {
                    command.setExecutor(commands.get(commandName).getDeclaredConstructor().newInstance());
                    registeredCommands++;
                } catch (Exception error) {
                    sendCriticalErrorMessage("Couldn't register command " + commandName, error);
                }
            } else {
                sendCriticalErrorMessage("Couldn't get command with name " + commandName + ", it is null. Maybe it doesn't exist in plugins.yml?");
            }
        }
        getLogger().info("OpenCreative+ registered " + (registeredCommands == commands.size() ? "all" : registeredCommands + "/" + commands.size()) + " commands.");
    }

    /**
     * Registers plugin's event listeners. If some listener is failed
     * to register, then it notifies, but doesn't disable entire plugin.
     */
    private void registerEvents() {
        getLogger().info("Registering OpenCreative+ event listeners...");
        int registeredListeners = 0;
        Class<?>[] listeners = new Class[]{
                ChangedWorld.class, EntitySpawnListener.class, EntityDamageListener.class,
                JoinListener.class, QuitListener.class, RespawnListener.class,
                DeathListener.class, TeleportListener.class, MoveListener.class,
                ChatListener.class, InteractListener.class, DropItemListener.class,
                PlaceBlockListener.class, DestroyBlockListener.class, BucketListener.class,
                ClickListener.class, RedstoneListener.class, BlockChangeListener.class,
                Menus.class, GameModeListener.class, EntityStateListener.class,
                CreativeListener.class, PotionListener.class, PlanetListener.class,
                CraftListener.class
        };
        for (Class<?> listenerClass : listeners) {
            try {
                getServer().getPluginManager().registerEvents(
                        (Listener) listenerClass.getDeclaredConstructor().newInstance(), this
                );
                registeredListeners++;
            } catch (Exception exception) {
                sendCriticalErrorMessage("Couldn't register event listener: " + listenerClass.getSimpleName(), exception);
            }
        }
        try {
            new WorldListener().registerExecutors();
        } catch (Exception exception) {
            sendCriticalErrorMessage("Couldn't register executors", exception);
        }
        getLogger().info("OpenCreative+ registered " + (registeredListeners == listeners.length ? "all" : registeredListeners + "/" + listeners.length) + " event listeners.");
    }

    /**
     * Returns OpenCreative+ settings.
     *
     * @return settings of plugin.
     */
    public static @NotNull Settings getSettings() {
        return getPlugin().settings;
    }

    /**
     * Sets custom economy manager.
     *
     * @param economy economy manager.
     */
    @SuppressWarnings("unused")
    public static void setEconomy(@NotNull Economy economy) {
        if (!(economy instanceof VaultEconomy || economy instanceof DisabledEconomy)) {
            getPlugin().getLogger().info("Now using economy manager: " + economy.getName());
        }
        getPlugin().economy = economy;
    }

    /**
     * Gets economy manager, that has money operations for players.
     *
     * @return economy manager.
     */
    public static Economy getEconomy() {
        return getPlugin().economy;
    }

    /**
     * Sets custom packet manager.
     *
     * @param packetManager packet manager.
     */
    @SuppressWarnings("unused")
    public static void setPacketManager(@NotNull PacketManager packetManager) {
        getPlugin().getLogger().info("Now using packet manager: " + packetManager.getName());
        getPlugin().packet = packetManager;
    }

    /**
     * Gets packet manager, that has packets modifiers methods.
     *
     * @return packet manager.
     */
    public static PacketManager getPacketManager() {
        return getPlugin().packet;
    }

    /**
     * Sets custom hint manager.
     *
     * @param hintManager hint manager.
     */
    @SuppressWarnings("unused")
    public static void setHintManager(@NotNull HintManager hintManager) {
        getPlugin().getLogger().info("Now using hint manager: " + hintManager.getName());
        getPlugin().hints = hintManager;
    }

    /**
     * Gets hint manager, that sends suggestions to players in action bar.
     *
     * @return hint manager.
     */
    @SuppressWarnings("unused")
    public static HintManager getHintManager() {
        return getPlugin().hints;
    }

    /**
     * Sets custom blocks manager.
     *
     * @param blocksManager blocks manager.
     */
    @SuppressWarnings("unused")
    public static void setBlocksManager(@NotNull BlocksManager blocksManager) {
        getPlugin().getLogger().info("Now using blocks manager: " + blocksManager.getName());
        getPlugin().blocks = blocksManager;
    }

    /**
     * Gets blocks manager, that changes a lot
     * of blocks in world.
     *
     * @return blocks manager.
     */
    @SuppressWarnings("unused")
    public static BlocksManager getBlocksManager() {
        return getPlugin().blocks;
    }

    /**
     * Sets custom module manager.
     *
     * @param moduleManager module manager.
     */
    @SuppressWarnings("unused")
    public static void setBlocksManager(@NotNull ModuleManager moduleManager) {
        getPlugin().getLogger().info("Now using module manager: " + moduleManager.getName());
        getPlugin().moduler = moduleManager;
    }

    /**
     * Gets disguise manager, that disguises
     * entities as players, other entities, blocks.
     *
     * @return disguise manager.
     */
    @SuppressWarnings("unused")
    public static DisguiseManager getDisguiseManager() {
        return getPlugin().disguiser;
    }

    /**
     * Sets custom disguise manager.
     *
     * @param disguiseManager disguise manager.
     */
    @SuppressWarnings("unused")
    public static void setDisguiseManager(@NotNull DisguiseManager disguiseManager) {
        getPlugin().getLogger().info("Now using disguise manager: " + disguiseManager.getName());
        getPlugin().disguiser = disguiseManager;
    }

    /**
     * Gets module manager, that creates
     * or deletes modules.
     *
     * @return modules manager.
     */
    public static ModuleManager getModuleManager() {
        return getPlugin().moduler;
    }

    /**
     * Sets custom coding platforms manager.
     *
     * @param platformsManager developer platforms manager.
     */
    @SuppressWarnings("unused")
    public static void setDevPlatformer(@NotNull DevPlatformer platformsManager) {
        if (!(platformsManager instanceof VerticalPlatformer || platformsManager instanceof HorizontalPlatformer)) {
            getPlugin().getLogger().info("Now using dev platforms manager: " + platformsManager.getName());
        }
        getPlugin().devPlatformer = platformsManager;
    }

    /**
     * Gets coding platforms manager, that
     * creates and manipulates with dev platforms
     * in developer worlds.
     *
     * @return coding platforms manager.
     */
    @SuppressWarnings("unused")
    public static DevPlatformer getDevPlatformer() {
        return getPlugin().devPlatformer;
    }

    /**
     * Sets custom download manager.
     *
     * @param downloadManager download manager.
     */
    @SuppressWarnings("unused")
    public static void setDownloadManager(@NotNull DownloadManager downloadManager) {
        if (!(downloadManager instanceof DisabledDownloader || downloadManager instanceof Downloader)) {
            getPlugin().getLogger().info("Now using download manager: " + downloadManager.getName());
        }
        getPlugin().downloader = downloadManager;
    }

    /**
     * Gets download manager, that
     * uploads world acrhive and allows
     * players to download it.
     *
     * @return download manager.
     */
    @SuppressWarnings("unused")
    public static DownloadManager getDownloadManager() {
        return getPlugin().downloader;
    }

    /**
     * Sets custom coding prompt manager.
     *
     * @param codingPrompter coding prompter.
     */
    @SuppressWarnings("unused")
    public static void setCodingPrompter(@NotNull CodingPrompter codingPrompter) {
        if (!(codingPrompter instanceof DisabledCodingPrompter
                || codingPrompter instanceof OpenAIPrompter || codingPrompter instanceof GeminiPrompter
                || codingPrompter instanceof OpenRouterPrompter)) {
            getPlugin().getLogger().info("Now using coding prompter: " + codingPrompter.getName());
        }
        getPlugin().prompter = codingPrompter;
    }

    /**
     * Gets coding prompt manager, that
     * generates code by players prompts.
     *
     * @return coding prompter.
     */
    @SuppressWarnings("unused")
    public static CodingPrompter getCodingPrompter() {
        return getPlugin().prompter;
    }

    /**
     * Sets custom planets manager.
     *
     * @param planetsManager planets manager.
     */
    @SuppressWarnings("unused")
    public static void setPlanetsManager(@NotNull PlanetsManager planetsManager) {
        getPlugin().getLogger().info("Now using planets manager: " + planetsManager.getName());
        getPlugin().space = planetsManager;
    }

    /**
     * Gets planets manager, that stores planets in base
     * and has methods to create, find and delete them.
     *
     * @return planets manager.
     */
    public static PlanetsManager getPlanetsManager() {
        return getPlugin().space;
    }

    /**
     * Sets custom stability manager.
     *
     * @param stabilityManager stability manager.
     */
    @SuppressWarnings("unused")
    public static void setStability(@NotNull StabilityManager stabilityManager) {
        if (!(stabilityManager instanceof DisabledWatchdog || stabilityManager instanceof Watchdog)) {
            getPlugin().getLogger().info("Now using stability manager: " + stabilityManager.getName());
        }
        getPlugin().watchdog = stabilityManager;
    }

    /**
     * Gets stability manager, that checks server's
     * performance and makes sure everything is fine.
     *
     * @return stability manager.
     */
    public static StabilityManager getStability() {
        return getPlugin().watchdog;
    }

    /**
     * Gets version of OpenCreative+.
     *
     * @return version of plugin.
     */
    public static @NotNull String getVersion() {
        return version;
    }

    /**
     * Gets update manager, that has methods to
     * check available updates for plugin.
     */
    public static Updater getUpdater() {
        return getPlugin().updater;
    }

    /**
     * Gets codename of current OpenCreative+ version.
     *
     * @return codename of version.
     */
    public static @NotNull String getCodename() {
        return codename;
    }

    /**
     * Checks if it's Christmas on plugin launch.
     *
     * @return true - it's Christmas, false - not.
     */
    private static boolean isChristmas() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            return calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DAY_OF_MONTH) == 25;
        } catch (Exception error) {
            return false;
        }
    }

    /**
     * Checks if it's Halloween on plugin launch.
     *
     * @return true - it's Halloween, false - not.
     */
    private static boolean isHalloween() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            return calendar.get(Calendar.MONTH) == Calendar.OCTOBER && calendar.get(Calendar.DAY_OF_MONTH) == 31;
        } catch (Exception error) {
            return false;
        }
    }

    public @NotNull Wander registerWander(@NotNull Player player) {
        Wander wander = new Wander(player);
        wanders.put(player.getUniqueId(), wander);
        return wander;
    }

    public void unregisterWander(@NotNull Player player) {
        wanders.remove(player.getUniqueId());
    }

    /**
     * Returns online wander, that plays on server.
     *
     * @return wander - if online, otherwise - null
     */
    public static @Nullable Wander getWander(@NotNull UUID uuid) {
        return getPlugin().wanders.get(uuid);
    }

    /**
     * Returns online wander casted by player.
     *
     * @return wander of player.
     */
    public static @NotNull Wander getWander(@NotNull Player player) {
        Wander wander = getWander(player.getUniqueId());
        if (wander == null) {
            wander = getPlugin().registerWander(player);
        }
        return wander;
    }

    /**
     * Returns offline wander, that can be online or offline.
     *
     * @return offline wander.
     */
    public static @NotNull OfflineWander getOfflineWander(@NotNull UUID uuid) {
        return new OfflineWander(uuid);
    }

}
