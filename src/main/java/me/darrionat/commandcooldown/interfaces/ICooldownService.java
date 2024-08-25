package me.darrionat.commandcooldown.interfaces;

import me.darrionat.commandcooldown.cooldowns.Cooldown;
import org.bukkit.entity.Player;

public interface ICooldownService extends Service {
    /**
     * Gets a cooldown from a command message.
     *
     * @param s The string of the command that does not contain the command special character.
     * @return the cooldown if it exists; otherwise {@code null}.
     */
    Cooldown findApplicableCooldown(String s);

    /**
     * Checks a player's permissions to see if they have a permission that affects the cooldown's duration.
     *
     * @param p        The player.
     * @param cooldown The cooldown to check for duration changes.
     * @return The cooldown with a changed duration if the player's permissions affect it; otherwise the original
     * cooldown.
     */
    Cooldown permissionCooldownChange(Player p, Cooldown cooldown);
}