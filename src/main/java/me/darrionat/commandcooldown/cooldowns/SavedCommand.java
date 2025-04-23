package me.darrionat.commandcooldown.cooldowns;

import me.darrionat.commandcooldown.exceptions.NoBaseCooldownException;
import me.darrionat.commandcooldown.interfaces.Cooldownable;

import java.util.ArrayList;
import java.util.List;

public class SavedCommand extends Command implements Cooldownable {
    private final List<Cooldown> cooldowns = new ArrayList<>();
    private Cooldown baseCooldown;

    /**
     * Constructs a new {@code SavedCommand} with no pre-defined cooldowns.
     *
     * @param label The label of the command.
     */
    public SavedCommand(String label) {
        super(label.toLowerCase());
    }

    /**
     * Gets the base cooldown of this command
     *
     * @return returns the base cooldown of the command.
     * @throws NoBaseCooldownException thrown when the command has no base cooldown.
     */
    public Cooldown getBaseCooldown() {
        if (baseCooldown != null)
            return baseCooldown;
        for (Cooldown cooldown : cooldowns)
            if (cooldown.isBaseCooldown()) {
                this.baseCooldown = cooldown;
                return baseCooldown;
            }
        throw new NoBaseCooldownException(getLabel());
    }

    /**
     * Determines if the saved command has a base cooldown.
     *
     * @return {@code true} if the command has a base cooldown; {@code false} otherwise.
     */
    public boolean hasBaseCooldown() {
        try {
            getBaseCooldown();
            return true;
        } catch (NoBaseCooldownException e) {
            return false;
        }
    }

    /**
     * Gets all cooldowns for this command.
     *
     * @return Returns a list of {@link Cooldown}.
     */
    public List<Cooldown> getCooldowns() {
        return cooldowns;
    }


    /**
     * Adds a new set of arguments and cooldown to the defined list of cooldowns.
     */
    public void addCooldown(Cooldown cooldown) {
        if (cooldown.isBaseCooldown())
            this.baseCooldown = cooldown;
        cooldowns.add(cooldown);
    }

    /**
     * Removes a cooldown from this command's cooldowns.
     *
     * @param cooldown The cooldown to remove.
     */
    public void removeCooldown(Cooldown cooldown) {
        cooldowns.remove(cooldown);
    }

}