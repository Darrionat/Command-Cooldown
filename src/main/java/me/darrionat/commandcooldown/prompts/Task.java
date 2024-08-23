package me.darrionat.commandcooldown.prompts;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Represents an action that can be done once a {@link Prompt} is completed.
 */
public abstract class Task {
    protected final Player p;

    public Task(Player p) {
        this.p = p;
    }

    /**
     * Gets the player this task belongs to.
     *
     * @return The active player of this task.
     */
    public Player getPlayer() {
        return p;
    }

    /**
     * Executed once the prompt is completed.
     *
     * @return The inventory that is opened when the task is finished.
     * @throws IllegalStateException thrown when the task is run when not completed.
     * @see #complete()
     */
    public abstract Inventory run() throws IllegalStateException;

    /**
     * The text that is prompted to the player when they first open the prompt.
     *
     * @return The task prompt text.
     */
    public abstract String promptText();

    /**
     * Executed once the prompt fails.
     *
     * @return The fail message.
     */
    public abstract String onFail();

    /**
     * Determines if an input is a valid input for the task currently.
     * <p>
     * If the required conditions of the task have been met.
     *
     * @param input The input text.
     * @return {@code true} if the task met its base conditions; otherwise {@code false}.
     */
    public abstract boolean valid(String input);

    /**
     * Determines if the task is complete.
     *
     * @return {@code true} if teh task is finished; otherwise {@code false}.
     */
    public abstract boolean complete();
}