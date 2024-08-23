package me.darrionat.commandcooldown.commands.subcommands;


import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.commands.CommandCooldownCommand;
import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.cooldowns.PlayerCooldown;
import me.darrionat.commandcooldown.interfaces.IMessageService;
import me.darrionat.commandcooldown.interfaces.IPlayerCooldownsRepository;
import me.darrionat.commandcooldown.utils.Duration;
import me.darrionat.pluginlib.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ActiveCooldownsCommand extends SubCommand {
    private final CommandCooldownPlugin plugin;
    private final IMessageService messageService;
    private final IPlayerCooldownsRepository playerCooldownsRepository;
    public final static String PERMISSION = "commandcooldown.cooldowns";
    public final static String SEE_OTHERS_PERMISSION = PERMISSION + ".others";

    public ActiveCooldownsCommand(CommandCooldownCommand parentCommand, CommandCooldownPlugin plugin, IPlayerCooldownsRepository playerCooldownsRepository, IMessageService messageService) {
        super(parentCommand, plugin);
        this.plugin = plugin;
        this.playerCooldownsRepository = playerCooldownsRepository;
        this.messageService = messageService;
    }

    // /cc cooldowns
    // /cc cooldowns [player]
    // /cc cooldowns [UUID]
    @Override
    public String getSubCommand() {
        return "cooldowns";
    }

    @Override
    public int getRequiredArgs() {
        return 1;
    }

    @Override
    public boolean onlyPlayers() {
        return false;
    }

    @Override
    protected void runCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                plugin.getErrorHandler().notEnoughArguments(this, sender);
                return;
            }
            sendActiveCooldownsOfSecondaryPlayer(sender, args[1]);
            return;
        }

        Player p = (Player) sender;
        if (!p.hasPermission(PERMISSION)) {
            plugin.getErrorHandler().noPermissionError(p, PERMISSION);
            return;
        }
        if (args.length == 1) {
            // Send player their active cooldowns
            sendActiveCooldowns(p, p.getUniqueId(), p.getName());
            return;
        }
        // Argument length >=2
        if (!p.hasPermission(SEE_OTHERS_PERMISSION)) {
            plugin.getErrorHandler().noPermissionError(p, SEE_OTHERS_PERMISSION);
            return;
        }
        // Optional player/UUID
        sendActiveCooldownsOfSecondaryPlayer(sender, args[1]);
    }

    private void sendActiveCooldownsOfSecondaryPlayer(CommandSender sender, String arg) {
        try {
            UUID uuid = UUID.fromString(arg);
            sendActiveCooldowns(sender, uuid, uuid.toString());
        } catch (IllegalArgumentException e) {
            // Not a valid UUID
            Player secondaryPlayer = Bukkit.getPlayer(arg);
            if (secondaryPlayer != null) {
                sendActiveCooldowns(sender, secondaryPlayer.getUniqueId(), secondaryPlayer.getName());
            } else {
                messageService.sendUseOnlinePlayerOrUUIDMessage(sender);
            }
        }
    }

    private void sendActiveCooldowns(CommandSender sender, UUID uuid, String nameOrUUID) {
        List<PlayerCooldown> activeCooldowns = playerCooldownsRepository.getAllPlayerCooldownsForPlayer(uuid);
        if (activeCooldowns.isEmpty()) {
            messageService.sendNoActiveCooldownsMessage(sender, nameOrUUID);
            return;
        }
        messageService.sendActiveCooldownsHeader(sender, nameOrUUID);
        for (PlayerCooldown playerCooldown : activeCooldowns) {
            Cooldown cd = playerCooldown.getCooldown();
            double remainingSeconds = playerCooldownsRepository.getRemainingCooldown(uuid, cd);

            String commandString = cd.toCommandString();
            messageService.sendCommandWithActiveCooldownMessage(sender, commandString);

            String durationString = Duration.toDurationString(remainingSeconds);
            messageService.sendRemainingTimeOnActiveCooldownMessage(sender, durationString);
        }
    }
}