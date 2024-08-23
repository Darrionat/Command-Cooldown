package me.darrionat.commandcooldown.prompts;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.pluginlib.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Set;

public class ChatPromptListener implements Listener {
    private static final Set<Task> ACTIVE_TASKS = new HashSet<>();
    private final CommandCooldownPlugin plugin;

    public ChatPromptListener(CommandCooldownPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Adds an incomplete task to the list of active tasks.
     *
     * @param task The task to add.
     * @throws IllegalStateException Thrown when the task argument is completed.
     */
    public static void add(Task task) {
        if (task.complete())
            throw new IllegalStateException("A completed task cannot be added");
        // Remove other tasks that the player is in, at most there can only be 1 other
        Player p = task.getPlayer();
        Task toRemove = null;
        for (Task t : ACTIVE_TASKS)
            if (t.getPlayer() == p) {
                toRemove = t;
                break;
            }
        if (toRemove != null) ACTIVE_TASKS.remove(toRemove);
        ACTIVE_TASKS.add(task);
    }

    /**
     * Gets the active of the player given.
     *
     * @param p The player to get the task of.
     * @return The active task the player is involved in. If the player does not have an active task {@code null} will
     *         be returned.
     */
    private Task getPlayerTask(Player p) {
        for (Task task : ACTIVE_TASKS)
            if (task.getPlayer() == p)
                return task;
        return null;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        Task task = getPlayerTask(p);
        if (task == null) return;

        String text = e.getMessage();
        if (!task.valid(text)) {
            p.sendMessage(Utils.toColor(task.onFail()));
            return;
        }
        e.setCancelled(true);
        if (task.complete()) {
            Bukkit.getScheduler().runTask(plugin, () -> p.openInventory(task.run()));
            ACTIVE_TASKS.remove(task);
        } else
            p.sendMessage(Utils.toColor(task.promptText()));
    }
}