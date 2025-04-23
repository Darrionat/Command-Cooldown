package me.darrionat.commandcooldown.gui;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.cooldowns.SavedCommand;
import me.darrionat.commandcooldown.interfaces.ICommandService;
import me.darrionat.commandcooldown.prompts.ChatPrompt;
import me.darrionat.commandcooldown.prompts.CreateAliasTask;
import me.darrionat.commandcooldown.prompts.Prompt;
import me.darrionat.pluginlib.guis.Gui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AliasesEditorGui extends Gui {
    private final CommandCooldownPlugin plugin;
    private final SavedCommand command;
    private final int page;
    private final ICommandService commandService;

    public AliasesEditorGui(CommandCooldownPlugin plugin, SavedCommand command, int page) {
        super(plugin, "Alternate labels for /" + command.getLabel(), 6);
        this.plugin = plugin;
        this.command = command;
        this.page = page;
        this.commandService = plugin.getCommandService();
        fillerItem();
    }

    @Override
    protected void getContents(Player p) {
        List<String> aliases = command.getAliases();
        int pageDiff = (page - 1) * CooldownsGui.AMT_PER_PAGE;
        for (int i = 0; i < CooldownsGui.AMT_PER_PAGE && i + pageDiff < aliases.size(); i++) {
            String alias = aliases.get(i + pageDiff);
            createItem(CommandEditorGui.ALIASES, 1, i, "&f&l/" + alias,
                    "&7Right-Click to delete this alias");
        }
        // Create Alias Button
        createItem(CooldownsGui.CREATE, 1, CooldownsGui.CREATE_SLOT, "&aCreate Alias",
                "&7Left-Click to create an alias");
        if (page > 1) // Not on first page
            createItem(CooldownsGui.PAGE_SWITCH, 1, CooldownsGui.PREV_PAGE_SLOT, "&fPrevious Page");
        if (page * CooldownsGui.AMT_PER_PAGE < aliases.size()) // There are more afterwards
            createItem(CooldownsGui.PAGE_SWITCH, 1, CooldownsGui.NEXT_PAGE_SLOT, "&fNext Page");
        // Go back
        createItem(CooldownsGui.PAGE_SWITCH, 1, CommandEditorGui.BACK_MENU_SLOT, "&7Go Back");
    }

    @Override
    public void clicked(Player p, int slot, ClickType clickType) {
        ItemStack clickedItem = inv.getItem(slot);
        if (clickedItem == null) return;
        // Create new alias
        if (slot == CooldownsGui.CREATE_SLOT) {
            Prompt prompt = new ChatPrompt(new CreateAliasTask(plugin, command, p));
            prompt.openPrompt();
            return;
        }
        if (slot == CommandEditorGui.BACK_MENU_SLOT) {
            plugin.openCommandEditor(p, command, 1);
            return;
        }
        if (clickedItem.getType() == CooldownsGui.PAGE_SWITCH.parseMaterial()) {
            switchPage(p, slot);
            return;
        }
        List<String> aliases = command.getAliases();
        if (slot >= aliases.size()) return;
        String alias = command.getAliases().get(slot);
        // Right-Click to delete alias
        if (alias != null && clickType == ClickType.RIGHT) {
            commandService.removeAlias(command, alias);
            plugin.openAliasesEditor(p, command, page);
        }
    }

    private void fillerItem() {
        for (int i = 0; i < size; i++)
            createItem(CooldownsGui.FILLER, 1, i, " ");
    }

    /**
     * Opens a different page of the {@code CommandEditorGui}.
     *
     * @param p    the player with the gui open.
     * @param slot the clicked slot of the inventory.
     */
    private void switchPage(Player p, int slot) {
        if (slot == CooldownsGui.PREV_PAGE_SLOT) {
            plugin.openAliasesEditor(p, command, page - 1);
        } else {
            assert slot == CooldownsGui.NEXT_PAGE_SLOT;
            plugin.openAliasesEditor(p, command, page + 1);
        }
    }
}