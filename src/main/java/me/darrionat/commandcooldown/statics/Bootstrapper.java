package me.darrionat.commandcooldown.statics;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.interfaces.*;
import me.darrionat.commandcooldown.repository.*;
import me.darrionat.commandcooldown.services.BypassService;
import me.darrionat.commandcooldown.services.CommandService;
import me.darrionat.commandcooldown.services.CooldownService;
import me.darrionat.commandcooldown.services.MessageService;

import java.util.ArrayList;
import java.util.List;

public class Bootstrapper {
    private static Bootstrapper instance;
    private final List<Repository> repositories = new ArrayList<>();
    private IConfigRepository configRepo;
    private ICooldownsRepository cooldownsRepo;
    private IMessageRepository messageRepo;
    private IPlayerCooldownsRepository playerCooldownsRepo;

    private IBypassService bypassService;
    private ICommandService commandService;
    private ICooldownService cooldownService;
    private IMessageService messageService;

    private Bootstrapper() {
    }

    public static Bootstrapper getBootstrapper() {
        if (instance == null) instance = new Bootstrapper();
        return instance;
    }

    public void init(CommandCooldownPlugin plugin) {
        configRepo = new ConfigRepository(plugin);
        if (configRepo.databaseEnabled()) {
            playerCooldownsRepo = new DatabasePlayerCooldownsRepository(plugin, configRepo);
        } else {
            playerCooldownsRepo = new LocalPlayerCooldownsRepository(plugin);
        }
        cooldownsRepo = new CooldownsRepository(plugin);
        messageRepo = new MessageRepository(plugin);

        repositories.add(configRepo);
        repositories.add(cooldownsRepo);
        repositories.add(messageRepo);

        bypassService = new BypassService();
        commandService = new CommandService(cooldownsRepo);
        cooldownService = new CooldownService(cooldownsRepo);
        messageService = new MessageService(plugin, messageRepo);
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public IConfigRepository getConfigRepo() {
        return configRepo;
    }

    public IPlayerCooldownsRepository getPlayerCooldownsRepo() {
        return playerCooldownsRepo;
    }

    public ICooldownsRepository getCooldownsRepo() {
        return cooldownsRepo;
    }

    public IMessageRepository getMessageRepo() {
        return messageRepo;
    }

    public IBypassService getBypassService() {
        return bypassService;
    }

    public ICommandService getCommandService() {
        return commandService;
    }

    public ICooldownService getCooldownService() {
        return cooldownService;
    }

    public IMessageService getMessageService() {
        return messageService;
    }
}