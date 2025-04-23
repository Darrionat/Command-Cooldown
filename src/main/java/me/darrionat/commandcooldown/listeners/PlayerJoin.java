package me.darrionat.commandcooldown.listeners;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.interfaces.IConfigRepository;
import me.darrionat.pluginlib.utils.SpigotMCUpdateHandler;
import me.darrionat.pluginlib.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;

public class PlayerJoin implements Listener {
    private final CommandCooldownPlugin plugin;
    private final SpigotMCUpdateHandler updater;

    public PlayerJoin(CommandCooldownPlugin plugin, IConfigRepository configRepo) {
        this.plugin = plugin;
        this.updater = plugin.getUpdater();
        if (!configRepo.checkForUpdates()) return;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("commandcooldown.use")) return;

        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
            if (!updater.updateAvailable()) return;
            try {
                p.sendMessage(Utils.toColor("&e" + plugin.getName() + "update available! Current version: " + plugin.getDescription().getVersion() + ", New version: " + updater.getLatestVersion()));
            } catch (IOException ex) {
                p.sendMessage(Utils.toColor("&e" + plugin.getName() + "update available! Current version: " + plugin.getDescription().getVersion()));
            }
            p.sendMessage(Utils.toColor("&bDownload the newest version here: " + updater.getResourceURL()));
            p.sendMessage(Utils.toColor("&b&oIf you just updated, you can ignore this message (SpigotMC takes time to update)."));
        }, 30L);// 30 ticks delay

    }
}