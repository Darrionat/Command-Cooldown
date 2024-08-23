package me.darrionat.commandcooldown.gui;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.cooldowns.SavedCommand;
import me.darrionat.commandcooldown.interfaces.ICommandService;
import me.darrionat.commandcooldown.prompts.ChatPrompt;
import me.darrionat.commandcooldown.prompts.CreateCooldownTask;
import me.darrionat.commandcooldown.prompts.Prompt;
import me.darrionat.commandcooldown.utils.Duration;
import me.darrionat.pluginlib.guis.Gui;
import me.darrionat.shaded.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandEditorGui extends Gui {
    // Subtract 1 for base cooldown
    public final static int AMT_PER_PAGE = CooldownsGui.AMT_PER_PAGE - 1;
    public final static int BASE_COOLDOWN_SLOT = 0;
    public final static int BACK_MENU_SLOT = CooldownsGui.CREATE_SLOT - 1;
    public final static int ALIASES_SLOT = BACK_MENU_SLOT - 1;
    public static final XMaterial ALIASES = XMaterial.OAK_SIGN;
    private static final XMaterial BASE_COOLDOWN_MATERIAL = XMaterial.GREEN_WOOL;
    private static final XMaterial NO_BASE_COOLDOWN_MATERIAL = XMaterial.RED_WOOL;
    private static final XMaterial COOLDOWN = XMaterial.LIME_WOOL;
    private final CommandCooldownPlugin plugin;
    private final SavedCommand command;
    private final int page;
    private final ICommandService commandService;

    public CommandEditorGui(CommandCooldownPlugin plugin, SavedCommand command, int page) {
        super(plugin, "Command Editor - /" + command.getLabel(), 6);
        this.plugin = plugin;
        this.command = command;
        this.page = page;
        this.commandService = plugin.getCommandService();
        fillerItem();
    }

    @Override
    protected void getContents(Player p) {
        Cooldown base = command.getBaseCooldown();
        if (command.hasBaseCooldown()) {
            String durationString = Duration.toDurationString(base.getDuration());
            createItem(BASE_COOLDOWN_MATERIAL, 1, BASE_COOLDOWN_SLOT, "&eBase Cooldown &7*",
                    "&7Duration: &a" + durationString,
                    "&7Left-Click to &aedit &7cooldown");
        } else {
            createItem(NO_BASE_COOLDOWN_MATERIAL, 1, 0,
                    "&cNo Base Cooldown", "&7Create a base cooldown");
        }
        // Show cooldowns
        List<Cooldown> cooldowns = command.getCooldowns();

        int pageDiff = (page - 1) * AMT_PER_PAGE;
        for (int i = 1; i < AMT_PER_PAGE && i + pageDiff < cooldowns.size(); i++) {
            Cooldown cooldown = cooldowns.get(i);
            if (cooldown.equals(base)) continue;
            String durationString = Duration.toDurationString(cooldown.getDuration());
            createItem(COOLDOWN, 1, i,
                    "&e" + String.join(" ", cooldown.getArgs()),
                    "&7Duration: &a" + durationString,
                    "&7Left-Click to &aedit &7cooldown",
                    "&7Right-Click to &cdelete &7cooldown");
        }
        // Sign, aliases menu
        createItem(ALIASES, 1, ALIASES_SLOT, "&eEdit Aliases",
                "&7Left-Click to enter the", "&7aliases editor");
        // Go back item
        createItem(CooldownsGui.PAGE_SWITCH, 1, BACK_MENU_SLOT, "&7Go Back");
        // Create cooldown slot
        createItem(CooldownsGui.CREATE, 1, CooldownsGui.CREATE_SLOT,
                "&aCreate Command Cooldown", "&7Left-Click to enter the", "&7cooldown editor");
        if (page > 1) // Not on first page
            createItem(CooldownsGui.PAGE_SWITCH, 1, CooldownsGui.PREV_PAGE_SLOT, "&fPrevious Page");
        if (page * AMT_PER_PAGE < cooldowns.size()) // There are more afterwards
            createItem(CooldownsGui.PAGE_SWITCH, 1, CooldownsGui.NEXT_PAGE_SLOT, "&fNext Page");
    }

    @Override
    public void clicked(Player p, int slot, ClickType clickType) {
        ItemStack clickedItem = inv.getItem(slot);
        if (clickedItem == null) return;
        // Create new cooldown
        if (slot == CooldownsGui.CREATE_SLOT) {
            Prompt prompt = new ChatPrompt(new CreateCooldownTask(plugin, command, p));
            prompt.openPrompt();
            return;
        }
        if (slot == BACK_MENU_SLOT) {
            plugin.openCooldownsEditor(p, 1);
            return;
        }
        if (slot == ALIASES_SLOT) {
            plugin.openAliasesEditor(p, command, 1);
            return;
        }
        if (clickedItem.getType() == CooldownsGui.PAGE_SWITCH.parseMaterial()) {
            switchPage(p, slot);
            return;
        }
        List<Cooldown> cooldowns = command.getCooldowns();
        if (slot >= cooldowns.size()) return;
        Cooldown cooldown = command.getCooldowns().get(slot);
        // Edit cooldown or delete it
        if (cooldown != null) {
            if (clickType == ClickType.LEFT) {
                plugin.openCooldownEditor(p, cooldown);
            } else if (clickType == ClickType.RIGHT && !cooldown.isBaseCooldown()) {
                commandService.removeCooldown(command, cooldown);
                plugin.openCommandEditor(p, command, page);
            }
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
            plugin.openCommandEditor(p, command, page - 1);
        } else {
            assert slot == CooldownsGui.NEXT_PAGE_SLOT;
            plugin.openCommandEditor(p, command, page + 1);
        }
    }
}