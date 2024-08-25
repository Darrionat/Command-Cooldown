package me.darrionat.commandcooldown.commands.subcommands;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.commands.CommandCooldownCommand;
import me.darrionat.commandcooldown.interfaces.IMessageService;
import me.darrionat.pluginlib.commands.SubCommand;
import me.darrionat.pluginlib.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
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

    @Override
    public List<String> getTabComplete(String[] args) {
        if (args.length != 2) {
            return null;
        }
        List<String> toReturn = new ArrayList<>();
        List<String> helpMessages = messageService.getHelpMessages();

        for (int i = 1; i <= pageAmount(helpMessages); i++) {
            toReturn.add("" + i);
        }
        return toReturn;
    }

    private void sendHelpMessage(CommandSender sender, String pageInput) {
        int page;
        List<String> helpMessages = messageService.getHelpMessages();
        int pagesAmount = pageAmount(helpMessages);
        try {
            page = Integer.parseInt(pageInput);
        } catch (NumberFormatException e) {
            page = 1;
        }
        if (page > pagesAmount || page < 1) page = 1;
        messageService.sendHelpHeader(sender, page, pagesAmount);
        for (int i = page * 5 - 5; i <= (page * 5 - 1) && i < helpMessages.size(); i++)
            sender.sendMessage(Utils.toColor(" " + helpMessages.get(i)));
    }

    private int pageAmount(List<String> helpMessages) {
        return (int) Math.ceil((double) helpMessages.size() / 5.0);
    }
}