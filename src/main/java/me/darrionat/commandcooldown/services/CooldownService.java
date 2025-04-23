package me.darrionat.commandcooldown.services;

import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.cooldowns.SavedCommand;
import me.darrionat.commandcooldown.interfaces.ICooldownService;
import me.darrionat.commandcooldown.interfaces.ICooldownsRepository;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CooldownService implements ICooldownService {
    private final ICooldownsRepository cooldownsRepo;

    /**
     * The active cooldowns for players.
     * <p>
     * The key is the player's UUID and the value is the time that the cooldown expires.
     */

    public CooldownService(ICooldownsRepository cooldownsRepo) {
        this.cooldownsRepo = cooldownsRepo;
    }

    @Override
    public Cooldown findApplicableCooldown(String s) {
        // take into account "warp shop a aa a"
        String[] message = s.toLowerCase().split(" ");
        String label = message[0];
        List<String> args = new ArrayList<>(Arrays.asList(message));
        // Remove label
        args.remove(0);
        for (SavedCommand savedCommand : cooldownsRepo.getCommandCooldowns()) {
            // If the label and aliases don't contain the sent label, continue
            if (!label.equalsIgnoreCase(savedCommand.getLabel())
                    && !savedCommand.getAliases().contains(label)) continue;

            Cooldown cooldown = getClosestCooldown(savedCommand, args);
            if (cooldown == null)
                return savedCommand.getBaseCooldown();
            return cooldown;
        }
        // No saved command exists
        return null;
    }

    /**
     * Gets the closest cooldown to the passed arguments.
     *
     * @param savedCommand The command with cooldowns to compare against.
     * @param args         The arguments that were sent.
     * @return The closest matching arguments and cooldown to the sent arguments; the base cooldown if no matches.
     */
    private Cooldown getClosestCooldown(SavedCommand savedCommand, List<String> args) {
        Cooldown mostMatching = null;
        int mostMatchingAmt = 0;

        if (args.isEmpty()) {
            return savedCommand.getBaseCooldown();
        }

        for (Cooldown cooldown : savedCommand.getCooldowns()) {
            if (cooldown.isBaseCooldown()) continue;
            // Track if matched and the amount of matched arguments
            boolean matched = true;
            int amtMatched = 0;
            List<String> cooldownArgs = cooldown.getArgs();
            // Loop over arguments. All need to match to be valid
            for (int i = 0; i < cooldownArgs.size() && i < args.size(); i++) {
                String arg = cooldownArgs.get(i);
                // Sent arguments don't match defined cooldown arguments
                // Incompatible cooldown arguments

                if (!arg.equalsIgnoreCase(args.get(i)) && !arg.equalsIgnoreCase("*")) {
                    matched = false;
                    break;
                }
                amtMatched++;
            }
            // If all arguments of the cooldown are contained and
            // the amount matched is greater, than it's the best match.
            if (matched && amtMatched > mostMatchingAmt) {
                mostMatching = cooldown;
                mostMatchingAmt = amtMatched;
            }
        }
        return mostMatching;
    }

    @Override
    public Cooldown permissionCooldownChange(Player p, Cooldown cooldown) {
        // Clone to avoid changing the original cooldown
        Cooldown clone = cooldown.clone();
        // Build permission
        // commandcooldown.commandPerm.duration
        String label = cooldown.getCommand().getLabel();
        // If it's a base cooldown, command perm is just the label.
        String commandPerm = cooldown.isBaseCooldown() ? label : label + "_" + String.join("_", cooldown.getArgs());
        double lowestDuration = getLowestDuration(p, commandPerm);
        // No new cooldown
        if (lowestDuration == -1)
            return cooldown;

        clone.setDuration(lowestDuration);
        return clone;
    }

    private static double getLowestDuration(Player p, String commandPerm) {
        double lowestDuration = -1;
        for (PermissionAttachmentInfo pai : p.getEffectivePermissions()) {
            if (!pai.getValue()) continue; // Ignores permissions with value false
            String permission = pai.getPermission();
            if (!permission.contains("commandcooldown." + commandPerm)) continue;
            // Only the duration
            String timeString = permission.replace("commandcooldown." + commandPerm + ".", "");
            try {
                double duration = Double.parseDouble(timeString);
                if (lowestDuration == -1)
                    lowestDuration = duration;
                else if (duration < lowestDuration)
                    lowestDuration = duration;
            } catch (NumberFormatException ignored) {
            }
        }
        return lowestDuration;
    }
}