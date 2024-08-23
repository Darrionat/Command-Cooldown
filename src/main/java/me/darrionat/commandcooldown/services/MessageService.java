package me.darrionat.commandcooldown.services;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.interfaces.IMessageRepository;
import me.darrionat.commandcooldown.interfaces.IMessageService;
import me.darrionat.commandcooldown.utils.Duration;
import me.darrionat.pluginlib.commands.SubCommand;
import me.darrionat.pluginlib.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageService implements IMessageService {
    private static final String ERRORS = "errors.";
    private static final String COMMANDS = "commands.";
    private static final String ACTIVE_COOLDOWNS = "activeCooldowns.";
    private final CommandCooldownPlugin plugin;
    private final IMessageRepository messageRepo;

    public MessageService(CommandCooldownPlugin plugin, IMessageRepository messageRepo) {
        this.plugin = plugin;
        this.messageRepo = messageRepo;
    }

    @Override
    public void sendMessage(CommandSender sender, String message) {
        Objects.requireNonNull(message);
        sender.sendMessage(Utils.toColor(message));
    }

    @Override
    public void sendBypassMessage(Player p) {
        sendMessage(p, messageRepo.getMessage("bypassCooldown"));
    }

    @Override
    public void sendStopBypassMessage(Player p) {
        sendMessage(p, messageRepo.getMessage("noLongerBypassing"));
    }

    @Override
    public void sendCooldownMessage(Player p, Cooldown cooldown, double remainingCooldown) {
        String msg = messageRepo.getMessage("onCooldown");
        String duration = Duration.toDurationString(remainingCooldown);
        msg = msg.replace("%time%", duration);
        sendMessage(p, msg);
    }

    @Override
    public void sendBaseMessage(CommandSender sender) {
        for (String msg : messageRepo.getList("baseMessage")) {
            msg = msg.replace("%version%", plugin.getDescription().getVersion());
            sendMessage(sender, msg);
        }
    }

    @Override
    public void sendNoPermissionError(Player p, String permission) {
        String msg = messageRepo.getMessage(ERRORS + "noPermission");
        msg = msg.replace("%perm%", permission);
        sendMessage(p, msg);
    }

    @Override
    public void sendOnlyPlayersError(CommandSender sender) {
        String msg = messageRepo.getMessage(ERRORS + "onlyPlayers");
        sendMessage(sender, msg);
    }

    @Override
    public void sendNotEnoughArgsError(CommandSender sender, SubCommand subCommand) {
        String msg = messageRepo.getMessage(COMMANDS + subCommand.getSubCommand());
        sendMessage(sender, msg);
    }

    @Override
    public void sendReloadMessage(CommandSender sender) {
        sendMessage(sender, messageRepo.getMessage("reload"));
    }

    @Override
    public List<String> getHelpMessages() {
        List<String> toReturn = new ArrayList<>();
        ConfigurationSection section = messageRepo.getConfigurationSection("commands");
        for (String key : section.getKeys(false))
            toReturn.add(section.getString(key));
        return toReturn;
    }

    @Override
    public void sendHelpHeader(CommandSender sender, int page, int pageAmount) {
        String msg = messageRepo.getMessage("helpHeader");
        msg = msg.replace("%page%", String.valueOf(page));
        msg = msg.replace("%pageAmount%", String.valueOf(pageAmount));
        sendMessage(sender, msg);
    }

    @Override
    public void sendResetMessage(CommandSender sender, Player target) {
        String msg = messageRepo.getMessage("resetCooldowns");
        msg = msg.replace("%player%", target.getName());
        sendMessage(sender, msg);
    }

    @Override
    public void sendUseOnlinePlayerOrUUIDMessage(CommandSender sender) {
        String msg = messageRepo.getMessage(ERRORS + "useOnlinePlayerOrUUID");
        sendMessage(sender, msg);
    }

    @Override
    public void sendNoActiveCooldownsMessage(CommandSender sender, String playerOrUUID) {
        String msg = messageRepo.getMessage(ACTIVE_COOLDOWNS + "noActiveCooldowns");
        msg = msg.replace("%player%", playerOrUUID);
        sendMessage(sender, msg);
    }

    @Override
    public void sendActiveCooldownsHeader(CommandSender sender, String playerOrUUID) {
        String msg = messageRepo.getMessage(ACTIVE_COOLDOWNS + "header");
        msg = msg.replace("%player%", playerOrUUID);
        sendMessage(sender, msg);
    }

    @Override
    public void sendCommandWithActiveCooldownMessage(CommandSender sender, String commandString) {
        String msg = messageRepo.getMessage(ACTIVE_COOLDOWNS + "commandWithActiveCooldown");
        msg = msg.replace("%command%", commandString);
        sendMessage(sender, msg);
    }

    @Override
    public void sendRemainingTimeOnActiveCooldownMessage(CommandSender sender, String durationString) {
        String msg = messageRepo.getMessage(ACTIVE_COOLDOWNS + "remainingTime");
        msg = msg.replace("%command%", durationString);
        sendMessage(sender, msg);
    }
}