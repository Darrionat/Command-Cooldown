package me.darrionat.commandcooldown.gui;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.cooldowns.SavedCommand;
import me.darrionat.commandcooldown.prompts.ChatPrompt;
import me.darrionat.commandcooldown.prompts.CreateCommandTask;
import me.darrionat.commandcooldown.prompts.Prompt;
import me.darrionat.pluginlib.guis.AnimatedGui;
import me.darrionat.shaded.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The default menu for the plugin.
 */
public class CooldownsGui extends AnimatedGui {
    public final static XMaterial CREATE = XMaterial.GREEN_WOOL;
    public final static XMaterial FILLER = XMaterial.BLACK_STAINED_GLASS_PANE;
    public final static XMaterial PAGE_SWITCH = XMaterial.ARROW;
    public final static int AMT_PER_PAGE = 9 * 5;
    public final static int CREATE_SLOT = 49;
    public final static int PREV_PAGE_SLOT = 45;
    public final static int NEXT_PAGE_SLOT = 53;
    private final CommandCooldownPlugin plugin;
    private final int page;

    public CooldownsGui(CommandCooldownPlugin plugin, int page) {
        super(plugin, plugin.getName(), 6);
        this.plugin = plugin;
        this.page = page;
        fillerItem();
    }

    @Override
    protected void getContents(Player p) {
        List<SavedCommand> commands = plugin.getCommandCooldowns();
        int pageDiff = (page - 1) * AMT_PER_PAGE;
        for (int i = 0; i < AMT_PER_PAGE && i + pageDiff < commands.size(); i++) {
            SavedCommand command = commands.get(i + pageDiff);
            createItem(XMaterial.CHEST, 1, i, "&f&l/" + command.getLabel(),
                    "&7Left-Click to edit this command", "&7Right-Click to &cdelete &7cooldown on this command");
        }

        createItem(CREATE, 1, CREATE_SLOT, "&aCreate Command Cooldown",
                "&7Left-Click to enter the", "&7command cooldown editor");
        if (page > 1) // Not on first page
            createItem(PAGE_SWITCH, 1, PREV_PAGE_SLOT, "&fPrevious Page");
        if (page * AMT_PER_PAGE < commands.size()) // There are more afterwards
            createItem(PAGE_SWITCH, 1, NEXT_PAGE_SLOT, "&fNext Page");
    }

    @Override
    public void clicked(Player p, int slot, ClickType clickType) {
        ItemStack clickedItem = inv.getItem(slot);
        if (clickedItem == null) return;
        // Create new command cooldown
        if (slot == CREATE_SLOT) {
            Prompt prompt = new ChatPrompt(new CreateCommandTask(plugin, p));
            prompt.openPrompt();
            return;
        }
        if (clickedItem.getType() == PAGE_SWITCH.parseMaterial()) {
            switchPage(p, slot);
            return;
        }
        List<SavedCommand> commands = plugin.getCommandCooldowns();
        if (slot >= commands.size()) return;
        SavedCommand command = commands.get(slot);
        if (command != null) {
            if (clickType == ClickType.LEFT) {
                plugin.openCommandEditor(p, command, 1);
            } else if (clickType == ClickType.RIGHT) {
                plugin.removeCommandCooldown(command);
                plugin.openCooldownsEditor(p, 1);
            }
        }
    }

    private void fillerItem() {
        for (int i = 0; i < size; i++)
            createItem(FILLER, 1, i, " ");
    }

    /**
     * Opens a different page of the {@code CooldownsGui}.
     *
     * @param p    the player with the gui open.
     * @param slot the clicked slot of the inventory.
     */
    private void switchPage(Player p, int slot) {
        if (slot == PREV_PAGE_SLOT) {
            plugin.openCooldownsEditor(p, page - 1);
        } else {
            assert slot == NEXT_PAGE_SLOT;
            plugin.openCooldownsEditor(p, page + 1);
        }
    }
}