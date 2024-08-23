package me.darrionat.commandcooldown.interfaces;

import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.pluginlib.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public interface IMessageService extends Service {
    /**
     * Informs a player that they are bypassing a command. This will only send if enabled in the config.
     *
     * @param p the player to send the message to.
     */
    void sendBypassMessage(Player p);

    void sendStopBypassMessage(Player p);

    /**
     * Informs a player that they still have a cooldown on a command.
     *
     * @param p                 The player on a cooldown.
     * @param cooldown          The cooldown the player has.
     * @param remainingCooldown The remaining amount of time in seconds.
     */
    void sendCooldownMessage(Player p, Cooldown cooldown, double remainingCooldown);

    void sendBaseMessage(CommandSender sender);

    void sendNoPermissionError(Player p, String permission);

    void sendOnlyPlayersError(CommandSender sender);

    void sendNotEnoughArgsError(CommandSender sender, SubCommand subCommand);

    void sendReloadMessage(CommandSender sender);

    List<String> getHelpMessages();

    void sendHelpHeader(CommandSender sender, int page, int pagesAmount);

    void sendResetMessage(CommandSender sender, Player target);

    void sendMessage(CommandSender sender, String message);

    void sendUseOnlinePlayerOrUUIDMessage(CommandSender sender);

    void sendNoActiveCooldownsMessage(CommandSender sender, String playerOrUUID);

    void sendActiveCooldownsHeader(CommandSender sender, String playerOrUUID);

    void sendCommandWithActiveCooldownMessage(CommandSender sender, String commandString);

    void sendRemainingTimeOnActiveCooldownMessage(CommandSender sender, String durationString);
}