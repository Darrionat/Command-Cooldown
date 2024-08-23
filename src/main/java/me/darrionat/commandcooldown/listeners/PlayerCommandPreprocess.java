package me.darrionat.commandcooldown.listeners;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.interfaces.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandPreprocess implements Listener {
    private final ICooldownService cooldownService;
    private final IConfigRepository configRepo;
    private final IBypassService bypassService;
    private final IMessageService messageService;
    private final IPlayerCooldownsRepository playerCooldownsRepository;

    public PlayerCommandPreprocess(CommandCooldownPlugin plugin, IConfigRepository configRepo,
                                   IPlayerCooldownsRepository playerCooldownsRepository, ICooldownService cooldownService,
                                   IBypassService bypassService, IMessageService messageService) {
        this.cooldownService = cooldownService;
        this.configRepo = configRepo;
        this.bypassService = bypassService;
        this.messageService = messageService;
        this.playerCooldownsRepository = playerCooldownsRepository;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onSentCommand(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage().replaceFirst("/", "");
        Cooldown cooldown = cooldownService.findApplicableCooldown(message);
        if (cooldown == null) return; // No saved cooldown exists for the sent command.

        Player p = e.getPlayer();
        // Player is bypassing with command or permission
        if (bypassService.playerIsBypassing(cooldown, p)) {
            if (configRepo.sendBypassMessage())
                messageService.sendBypassMessage(p);
            return;
        }

        if (playerCooldownsRepository.playerHasCooldown(p, cooldown)) {
            messageService.sendCooldownMessage(p, cooldown, playerCooldownsRepository.getRemainingCooldown(p, cooldown));
            e.setCancelled(true);
        } else {
            // Allow execution and give cooldown.
            playerCooldownsRepository.giveCooldown(p, cooldownService.permissionCooldownChange(p, cooldown));
        }
    }
}