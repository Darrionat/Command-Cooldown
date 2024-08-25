package me.darrionat.commandcooldown.commands.subcommands;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.commands.CommandCooldownCommand;
import me.darrionat.commandcooldown.interfaces.IMessageService;
import me.darrionat.pluginlib.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand extends SubCommand {
    private final CommandCooldownPlugin plugin;
    private final IMessageService messageService;

    public ReloadCommand(CommandCooldownCommand parentCommand, CommandCooldownPlugin plugin, IMessageService messageService) {
        super(parentCommand, plugin);
        this.plugin = plugin;
        this.messageService = messageService;
    }

    @Override
    public String getSubCommand() {
        return "reload";
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
        messageService.sendReloadMessage(sender);
        plugin.reinitializeRepositories();
    }

    @Override
    public List<String> getTabComplete(String[] args) {
        return null;
    }
}