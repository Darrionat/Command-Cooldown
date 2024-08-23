package me.darrionat.commandcooldown.utils;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.interfaces.IMessageService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.darrionat.pluginlib.ErrorHandler;
import me.darrionat.pluginlib.commands.SubCommand;

import java.util.UUID;

public class Errors implements ErrorHandler {
    private final CommandCooldownPlugin plugin;
    private final IMessageService messageService;

    public Errors(CommandCooldownPlugin plugin, IMessageService messageService) {
        this.plugin = plugin;
        this.messageService = messageService;
    }

    public void noPermissionError(Player p, String permission) {
        messageService.sendNoPermissionError(p, permission);
    }

    public void onlyPlayerCommandError(CommandSender sender) {
        messageService.sendOnlyPlayersError(sender);
    }

    public void notEnoughArguments(SubCommand subCommand, CommandSender sender) {
        messageService.sendNotEnoughArgsError(sender, subCommand);
    }

    public void loadingSavedCooldownsError() {
        plugin.log("&cError: Internal error when loading saved cooldowns, report this bug promptly.");
    }

    public void parsePlayerCooldownError(UUID uuid) {
        plugin.log("&cError: Internal error when parsing player cooldown for " + uuid + ", report this bug promptly.");
    }
}