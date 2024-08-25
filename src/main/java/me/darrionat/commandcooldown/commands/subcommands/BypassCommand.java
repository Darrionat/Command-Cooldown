package me.darrionat.commandcooldown.commands.subcommands;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.interfaces.IBypassService;
import me.darrionat.commandcooldown.interfaces.IMessageService;
import me.darrionat.pluginlib.commands.BaseCommand;
import me.darrionat.pluginlib.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BypassCommand extends SubCommand {
    private final IBypassService bypassService;
    private final IMessageService messageService;

    public BypassCommand(BaseCommand parentCommand, CommandCooldownPlugin plugin, IBypassService bypassService, IMessageService messageService) {
        super(parentCommand, plugin);
        this.bypassService = bypassService;
        this.messageService = messageService;
    }

    @Override
    public String getSubCommand() {
        return "bypass";
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
        Player p = (Player) sender;
        if (bypassService.playerIsBypassing(null, p)) {
            bypassService.stopBypassing(p);
            messageService.sendStopBypassMessage(p);
        } else {
            bypassService.startBypassing(p);
            messageService.sendBypassMessage(p);
        }
    }

    @Override
    public List<String> getTabComplete(String[] args) {
        return null;
    }
}
