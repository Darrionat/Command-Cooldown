package me.darrionat.commandcooldown.interfaces;

import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.cooldowns.PlayerCooldown;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface IPlayerCooldownsRepository extends Repository {
    /**
     * Gives a player a defined cooldown.
     *
     * @param p        The player to give the cooldown.
     * @param cooldown The cooldown the player will receive.
     */
    void giveCooldown(Player p, Cooldown cooldown);

    /**
     * Removes all cooldowns from a specified player.
     *
     * @param p The player
     */
    void removePlayerCooldowns(Player p);

    /**
     * Gets the player's remaining cooldown for a particular command in seconds.
     *
     * @param p        the player.
     * @param cooldown the command with a cooldown.
     * @return returns a player's remaining cooldown for the command; {@code -1} if the player has no cooldown.
     */
    double getRemainingCooldown(Player p, Cooldown cooldown);

    /**
     * Gets the player's remaining cooldown for a particular command in seconds.
     *
     * @param uuid     the UUID of the player.
     * @param cooldown the command with a cooldown.
     * @return returns a player's remaining cooldown for the command; {@code -1} if the player has no cooldown.
     */
    double getRemainingCooldown(UUID uuid, Cooldown cooldown);

    /**
     * Determines if the player has a cooldown for a command.
     *
     * @param p        the player.
     * @param cooldown the cooldown.
     * @return {@code true} if the player has a cooldown on the command; otherwise {@code false}.
     */
    boolean playerHasCooldown(Player p, Cooldown cooldown);

    /**
     * Gets all cooldowns associated with a player.
     *
     * @param p the player.
     * @return returns a list of all active cooldowns for the given player.
     */
    List<PlayerCooldown> getAllPlayerCooldownsForPlayer(Player p);

    /**
     * Gets all cooldowns associated with a player.
     *
     * @param uuid the UUID of the player.
     * @return returns a list of all active cooldowns for the given player.
     */
    List<PlayerCooldown> getAllPlayerCooldownsForPlayer(UUID uuid);
}

