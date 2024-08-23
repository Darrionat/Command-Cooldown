package me.darrionat.commandcooldown.gui;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.prompts.ChatPrompt;
import me.darrionat.commandcooldown.prompts.DurationTask;
import me.darrionat.commandcooldown.prompts.Prompt;
import me.darrionat.commandcooldown.utils.Duration;
import me.darrionat.pluginlib.guis.Gui;
import me.darrionat.shaded.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class CooldownEditorGui extends Gui {
    private final static XMaterial CHANGE_COOLDOWN_MATERIAL = XMaterial.YELLOW_WOOL;
    private final static int CHANGE_COOLDOWN_SLOT = 31; // Center slot

    private final CommandCooldownPlugin plugin;
    private final Cooldown cooldown;

    public CooldownEditorGui(CommandCooldownPlugin plugin, Cooldown cooldown) {
        super(plugin, cooldown.toCommandString(), 6);
        this.plugin = plugin;
        this.cooldown = cooldown;
        fillerItem();
    }

    @Override
    protected void getContents(Player p) {
        double duration = cooldown.getDuration();
        String durationString = Duration.toDurationString(duration);
        createItem(CHANGE_COOLDOWN_MATERIAL,
                1,
                CHANGE_COOLDOWN_SLOT,
                "&eChange Cooldown",
                "&7Current Duration: &a" + durationString);
        // Go back item
        createItem(CooldownsGui.PAGE_SWITCH,
                1,
                CommandEditorGui.BACK_MENU_SLOT,
                "&7Go Back");
    }

    @Override
    public void clicked(Player p, int slot, ClickType clickType) {
        if (slot == CHANGE_COOLDOWN_SLOT) {
            Prompt prompt = new ChatPrompt(new DurationTask(plugin, cooldown, p));
            prompt.openPrompt();
        } else if (slot == CommandEditorGui.BACK_MENU_SLOT) {
            plugin.openCommandEditor(p, cooldown.getCommand(), 1);
        }
    }

    private void fillerItem() {
        for (int i = 0; i < size; i++)
            createItem(CooldownsGui.FILLER, 1, i, " ");
    }
}