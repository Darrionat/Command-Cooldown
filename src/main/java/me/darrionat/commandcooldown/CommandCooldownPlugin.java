package me.darrionat.commandcooldown;

import me.darrionat.commandcooldown.commands.CommandCooldownCommand;
import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.cooldowns.PlayerCooldown;
import me.darrionat.commandcooldown.cooldowns.SavedCommand;
import me.darrionat.commandcooldown.gui.AliasesEditorGui;
import me.darrionat.commandcooldown.gui.CommandEditorGui;
import me.darrionat.commandcooldown.gui.CooldownEditorGui;
import me.darrionat.commandcooldown.gui.CooldownsGui;
import me.darrionat.commandcooldown.interfaces.*;
import me.darrionat.commandcooldown.listeners.PlayerCommandPreprocess;
import me.darrionat.commandcooldown.listeners.PlayerJoin;
import me.darrionat.commandcooldown.prompts.ChatPromptListener;
import me.darrionat.commandcooldown.statics.Bootstrapper;
import me.darrionat.commandcooldown.utils.Errors;
import me.darrionat.pluginlib.Plugin;
import me.darrionat.pluginlib.guis.Gui;
import me.darrionat.pluginlib.utils.SpigotMCUpdateHandler;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class CommandCooldownPlugin extends Plugin {
    private static final int RESOURCE_ID = 73696;
    private SpigotMCUpdateHandler updater;
    private boolean updateAvailable;

    private Errors errors;
    private ICooldownsRepository cooldownsRepo;
    private IConfigRepository configRepo;
    private IPlayerCooldownsRepository playerCooldownsRepo;

    private IBypassService bypassService;
    private ICooldownService cooldownService;
    private IMessageService messageService;

    public void initPlugin() {
        Bootstrapper bootstrapper = Bootstrapper.getBootstrapper();
        bootstrapper.init(this);
        initFields();
        if (configRepo.checkForUpdates())
            checkUpdates();

        new CommandCooldownCommand(this, messageService, bypassService, playerCooldownsRepo);
        new PlayerCommandPreprocess(this, configRepo, playerCooldownsRepo, cooldownService, bypassService, messageService);
        new PlayerJoin(this, configRepo);
        new ChatPromptListener(this);
    }

    private void initFields() {
        Bootstrapper bootstrapper = Bootstrapper.getBootstrapper();
        cooldownsRepo = bootstrapper.getCooldownsRepo();
        configRepo = bootstrapper.getConfigRepo();
        playerCooldownsRepo = bootstrapper.getPlayerCooldownsRepo();

        bypassService = bootstrapper.getBypassService();
        cooldownService = bootstrapper.getCooldownService();
        messageService = bootstrapper.getMessageService();
        errors = new Errors(this, messageService);
    }

    /**
     * Reloads all repositories asynchronously.
     */
    public void reinitializeRepositories() {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            for (Repository repo : Bootstrapper.getBootstrapper().getRepositories())
                repo.init();
        });
    }

    private void checkUpdates() {
        this.updater = buildUpdateChecker();
        if (updater.updateAvailable()) {
            this.updateAvailable = true;
            log("Update available! Download at " + updater.getResourceURL());
        } else {
            log("Plugin up to date");
        }
    }

    public Errors getErrorHandler() {
        return errors;
    }

    @Override
    public void onDisable() {
    }

    public boolean updateAvailable() {
        return updateAvailable;
    }

    public SpigotMCUpdateHandler getUpdater() {
        return updater;
    }

    /**
     * Opens the menu that displays all {@code SavedCommand}s.
     *
     * @param p    The player opening the menu.
     * @param page The page of the menu to open.
     */
    public void openCooldownsEditor(Player p, int page) {
        Gui gui = new CooldownsGui(this, page);
        openMenu(p, gui);
    }

    /**
     * Opens an editor for a {@code SavedCommand}.
     *
     * @param p       The player opening the menu.
     * @param command The command to be edited.
     * @param page    The page of the menu to open.
     */
    public void openCommandEditor(Player p, SavedCommand command, int page) {
        openMenu(p, new CommandEditorGui(this, command, page));
    }

    /**
     * Opens an editor for a {@code Cooldown}.
     *
     * @param p        The player opening the menu.
     * @param cooldown The cooldown to be edited.
     */
    public void openCooldownEditor(Player p, Cooldown cooldown) {
        openMenu(p, new CooldownEditorGui(this, cooldown));
    }

    public void openAliasesEditor(Player p, SavedCommand command, int page) {
        openMenu(p, new AliasesEditorGui(this, command, page));
    }

    private void openMenu(Player p, Gui gui) {
        getGuiHandler().openGui(p, gui);
    }

    /**
     * Get all commands with a cooldown.
     *
     * @return A list of all {@code SavedCommand}s.
     */
    public List<SavedCommand> getCommandCooldowns() {
        return cooldownsRepo.getCommandCooldowns();
    }

    /**
     * Adds a new command with a cooldown.
     *
     * @param command The command.
     */
    public void createCommandCooldown(SavedCommand command) {
        cooldownsRepo.addCommandCooldown(command);
    }

    public ICommandService getCommandService() {
        return Bootstrapper.getBootstrapper().getCommandService();
    }

    public PlayerCooldown matchCooldownToPlayerCooldown(UUID uuid, Cooldown cooldown) {
        for (PlayerCooldown playerCooldown : playerCooldownsRepo.getAllPlayerCooldownsForPlayer(uuid)) {
            if (playerCooldown == null) {
                getErrorHandler().parsePlayerCooldownError(uuid);
                continue;
            }
            Cooldown cd = playerCooldown.getCooldown();
            if (playerCooldown.getPlayer().equals(uuid) && cd.equals(cooldown))
                return playerCooldown;
        }
        return null;
    }

    @Override
    public int getSpigotResourceId() {
        return RESOURCE_ID;
    }
}