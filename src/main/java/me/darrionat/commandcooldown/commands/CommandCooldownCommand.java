package me.darrionat.commandcooldown.commands;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.commands.subcommands.*;
import me.darrionat.commandcooldown.interfaces.*;
import me.darrionat.pluginlib.commands.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandCooldownCommand extends BaseCommand {
    private final IMessageService messageService;

    public CommandCooldownCommand(CommandCooldownPlugin plugin, IMessageService messageService, IBypassService bypassService, IPlayerCooldownsRepository playerCooldownsRepository) {
        super(plugin);
        this.messageService = messageService;
        addSubCommand(new EditorCommand(this, plugin));
        addSubCommand(new ReloadCommand(this, plugin, messageService));
        addSubCommand(new HelpCommand(this, plugin, messageService));
        addSubCommand(new BypassCommand(this, plugin, bypassService, messageService));
        addSubCommand(new RemoveCooldownsCommand(this, plugin, playerCooldownsRepository, messageService));
        addSubCommand(new ActiveCooldownsCommand(this, plugin, playerCooldownsRepository, messageService));
    }

    @Override
    public String getCommandLabel() {
        return "commandcooldown";
    }

    @Override
    protected void runNoArgs(CommandSender sender, Command command, String label, String[] args) {
        messageService.sendBaseMessage(sender);
    }
}