package me.Darrionat.CommandCooldown.Listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.Darrionat.CommandCooldown.Main;
import me.Darrionat.CommandCooldown.Utils.Utils;

public class CommandProcess implements Listener {

	private Main plugin;

	public static HashMap<String, Long> cooldownMap = new HashMap<String, Long>();

	public CommandProcess(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	@EventHandler
	public void commandSent(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		String sentCommand = e.getMessage().replace("/", "");
		int i = sentCommand.indexOf(' ');
		String label;
		try {
			label = sentCommand.substring(0, i);
		} catch (StringIndexOutOfBoundsException exe) {
			label = sentCommand;
		}
		FileConfiguration config = plugin.getConfig();

		for (String key : config.getKeys(false)) {
			if (key.equalsIgnoreCase("checkUpdates") || key.equalsIgnoreCase("SendBypassMessage")
					|| key.equalsIgnoreCase("Messages")) {
				continue;
			}
			ConfigurationSection section = config.getConfigurationSection(key);
			int cooldown = section.getInt("cooldown");
			if (!key.equalsIgnoreCase(sentCommand)) {

				List<String> aliases = section.getStringList("aliases");

				for (String alias : aliases) {
					if (!label.equalsIgnoreCase(alias)) {
						continue;
					}
					if (key.contains(" ")) {
						String[] arr = key.split(" ", 2);
						if (!arr[1].equalsIgnoreCase(sentCommand.replace(label + " ", ""))) {
							if (!arr[0].equalsIgnoreCase(label)) {
								continue;
							}
							if (playerBypassing(p, key)) {
								return;
							}
							if (addCooldown(p, config, key, cooldown) == true) {
								e.setCancelled(true);
							}
							return;
						}
					}
					if (!label.equalsIgnoreCase(alias)) {
						continue;
					}
					if (playerBypassing(p, key)) {
						return;
					}
					if (addCooldown(p, config, key, cooldown) == true) {
						e.setCancelled(true);
					}
					return;

				}

			}

			if (!key.contains(" ")) {
				if (!label.equalsIgnoreCase(key)) {
					continue;
				}
				if (playerBypassing(p, key)) {
					return;
				}
				if (addCooldown(p, config, key, cooldown) == true) {
					e.setCancelled(true);
				}
				return;
			}
			if (!sentCommand.equalsIgnoreCase(key)) {
				continue;
			}
			if (playerBypassing(p, key)) {
				return;
			}
			if (addCooldown(p, config, key, cooldown) == true) {
				e.setCancelled(true);
			}
			return;

		}
	}

	public boolean playerBypassing(Player p, String key) {
		FileConfiguration config = plugin.getConfig();
		ArrayList<String> bypassList = Utils.getBypassList();
		if (bypassList.contains(p.getName())) {
			if (config.getBoolean("SendBypassMessage") == true) {
				p.sendMessage(Utils.chat(config.getString("Messages.BypassMessage")));
			}
			return true;
		}
		if (p.hasPermission("commandcooldown.bypass." + key.replace(" ", "_"))) {
			return true;
		}

		return false;
	}

	public boolean addCooldown(Player p, FileConfiguration config, String key, int cooldown) {

		// Has permission with different cooldown
		Iterator<PermissionAttachmentInfo> iterator = p.getEffectivePermissions().iterator();
		while (iterator.hasNext()) {
			String permission = iterator.next().getPermission();
			String key2 = key.replace(" ", "_");
			if (!permission.contains("commandcooldown." + key2)) {
				continue;
			}

			String timeString = permission.replace("commandcooldown." + key2 + ".", "");
			try {
				cooldown = Integer.parseInt(timeString);
			} catch (NumberFormatException exe) {
				break;
			}
		}
		String mapKey = p.getName() + " " + key;
		if (cooldownMap.containsKey(mapKey)) {
			long secondsLeft = ((cooldownMap.get(mapKey) / 1000) + cooldown) - (System.currentTimeMillis() / 1000);
			if (secondsLeft > 0) {
				String timeString = null;
				long days;
				long hours;
				long minutes;
				long seconds;
				if (secondsLeft >= 86400) {
					days = secondsLeft / 86400;
					hours = (secondsLeft % 86400) / 3600;
					minutes = (secondsLeft % 3600) / 60;
					seconds = secondsLeft % 60;
					timeString = String.format("%02dd %02dh %02dm %02ds", days, hours, minutes, seconds);
				}
				if (secondsLeft > 3600 && secondsLeft < 86400) {
					hours = secondsLeft / 3600;
					minutes = (secondsLeft % 3600) / 60;
					seconds = secondsLeft % 60;
					timeString = String.format("%02dh %02dm %02ds", hours, minutes, seconds);
				}
				if (secondsLeft <= 3600 && secondsLeft > 60) {
					minutes = (secondsLeft / 60);
					seconds = secondsLeft % 60;
					timeString = String.format("%02dm %02ds", minutes, seconds);
				}
				if (secondsLeft <= 60) {
					seconds = secondsLeft;
					timeString = String.format("%02ds", seconds);
				}

				p.sendMessage(Utils
						.chat(config.getString("Messages.CooldownMsg").replace("%time%", String.valueOf(timeString))));
				System.out.println(Utils.chat(config.getString("Messages.NotifyConsole")
						.replace("%time%", String.valueOf(timeString)).replace("%player%", p.getName())));
				return true;
			}
		}
		cooldownMap.put(mapKey, System.currentTimeMillis());
		return false;
	}
}
