package me.darrionat.commandcooldown.commands.subcommands;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.commands.CommandCooldownCommand;
import me.darrionat.pluginlib.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class EditorCommand extends SubCommand {
    private final CommandCooldownPlugin plugin;

    public EditorCommand(CommandCooldownCommand parentCommand, CommandCooldownPlugin plugin) {
        super(parentCommand, plugin);
        this.plugin = plugin;
    }

    @Override
    public String getSubCommand() {
        return "editor";
    }

    @Override
    public int getRequiredArgs() {
        return 1;
    }

    @Override
    public boolean onlyPlayers() {
        return true;
    }

    @Override
    protected void runCommand(CommandSender sender, String[] args) {
        plugin.openCooldownsEditor((Player) sender, 1);
    }

    @Override
    public List<String> getTabComplete(String[] args) {
        return null;
    }
}