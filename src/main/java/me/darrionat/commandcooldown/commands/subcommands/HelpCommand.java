package me.darrionat.commandcooldown.commands.subcommands;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.commands.CommandCooldownCommand;
import me.darrionat.commandcooldown.interfaces.IMessageService;
import me.darrionat.pluginlib.commands.SubCommand;
import me.darrionat.pluginlib.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpCommand extends SubCommand {
    private final IMessageService messageService;

    public HelpCommand(CommandCooldownCommand command, CommandCooldownPlugin plugin, IMessageService messageService) {
        super(command, plugin);
        this.messageService = messageService;
    }

    @Override
    public String getSubCommand() {
        return "help";
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
        if (args.length == 1)
            sendHelpMessage(sender, "1");
        else
            sendHelpMessage(sender, args[1]);
    }

    private void sendHelpMessage(CommandSender sender, String pageInput) {
        int page;
        List<String> list = messageService.getHelpMessages();
        int pagesAmount = (int) Math.ceil((double) list.size() / 5.0);
        try {
            page = Integer.parseInt(pageInput);
        } catch (NumberFormatException e) {
            page = 1;
        }
        if (page > pagesAmount || page < 1) page = 1;
        messageService.sendHelpHeader(sender, page, pagesAmount);
        for (int i = page * 5 - 5; i <= (page * 5 - 1) && i < list.size(); i++)
            sender.sendMessage(Utils.toColor(" " + list.get(i)));
    }
}