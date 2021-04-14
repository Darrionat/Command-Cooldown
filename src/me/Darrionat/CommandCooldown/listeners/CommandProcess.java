package me.Darrionat.CommandCooldown.listeners;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.Darrionat.CommandCooldown.Command;
import me.Darrionat.CommandCooldown.CommandCooldown;
import me.Darrionat.CommandCooldown.Cooldown;
import me.Darrionat.CommandCooldown.handlers.BypassHandler;
import me.Darrionat.CommandCooldown.handlers.MessageService;
import me.Darrionat.CommandCooldown.utils.Utils;

public class CommandProcess implements Listener {

	private CommandCooldown plugin;
	private FileConfiguration config;
	private MessageService messages;

	public CommandProcess(CommandCooldown plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
		config = plugin.getConfig();
		messages = new MessageService(plugin);
	}

	@EventHandler
	public void commandSent(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		String message = e.getMessage();
		Command command = new Command(message, plugin);

		// The command doesn't have a cooldown or the cooldown is <= 0
		if (!command.hasCooldown) {
			return;
		}
		// Player is bypassing with command or permission
		if (BypassHandler.playerIsBypassing(command, p)) {
			if (config.getBoolean("SendBypassMessage") == true)
				p.sendMessage(messages.getMessage(messages.bypassingCooldown));

			return;
		}

		Cooldown cooldown = playerIsOnCooldown(command, p);
		// Null if they don't have a cooldown for that command
		if (cooldown != null) {
			long endOfCooldown = cooldown.getEndOfCooldown();
			String cooldownMessage = getCooldownMessage(cooldown.getCommand(), endOfCooldown);

			p.sendMessage(Utils.chat(cooldownMessage));
			e.setCancelled(true);
			return;
		}
		giveCooldown(command, p);
	}

	/**
	 * @return: Cooldown, null if the player is not on a cooldown for the command
	 */
	private Cooldown playerIsOnCooldown(Command command, Player p) {
		for (Cooldown cooldown : plugin.cooldownList) {
			if (!cooldown.getCommand().equalsIgnoreCase(command.message))
				continue;
			if (!cooldown.getPlayerUUID().equals(p.getUniqueId()))
				continue;

			if (cooldown.getTimeRemainingMillis() <= 0) {
				plugin.cooldownList.remove(cooldown);
				return null;
			}
			return cooldown;
		}
		return null;
	}

	private void giveCooldown(Command command, Player p) {

		// Duration seconds
		double duration = command.cooldown;
		duration = changeCooldownByPermission(command, p, duration);
		long endOfCooldown = System.currentTimeMillis() + (long) (duration * 1000);

		Cooldown cooldown = new Cooldown(p.getUniqueId(), command.message, endOfCooldown);
		plugin.cooldownList.add(cooldown);
	}

	/**
	 * 
	 * @param command
	 * @param p
	 * @param duration - Original Duration
	 * @return - Unchanged duration if no permission with new cooldown
	 */
	private double changeCooldownByPermission(Command command, Player p, double duration) {
		// Has permission with different cooldown
		for (PermissionAttachmentInfo permissionAttachmentInfo : p.getEffectivePermissions()) {
			if (!permissionAttachmentInfo.getValue()) continue;

			String permission = permissionAttachmentInfo.getPermission();
			String key = command.message.replace(" ", "_");
			if (!permission.contains("commandcooldown." + key)) {
				continue;
			}
			String timeString = permission.replace("commandcooldown." + key + ".", "");
			try {
				duration = Integer.parseInt(timeString);
			} catch (NumberFormatException exe) {
				break;
			}
		}
		return duration;
	}

	private String getCooldownMessage(String command, long endOfCooldown) {
		long timeRemaining = (endOfCooldown - System.currentTimeMillis()) / 1000;
		String timeRemainingStr = Utils.getDurationString(timeRemaining);

		String cooldownMessage = messages.getMessage(messages.cooldownMessage);
		cooldownMessage = cooldownMessage.replace("%time%", timeRemainingStr);
		cooldownMessage = cooldownMessage.replace("%command%", command);

		return cooldownMessage;
	}
}