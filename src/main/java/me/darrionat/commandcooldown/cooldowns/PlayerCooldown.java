package me.darrionat.commandcooldown.cooldowns;

import java.util.UUID;

public class PlayerCooldown {
    /**
     * The special character that is used to separate values when the {@code PlayerCooldown} is converted to a string.
     */
    public static final String SEP = "/";
    private final UUID uuid;
    private final Cooldown cooldown;
    private final long end;

    public PlayerCooldown(UUID uuid, Cooldown cooldown, long end) {
        this.uuid = uuid;
        this.cooldown = cooldown;
        this.end = end;
    }

    /**
     * Constructs a {@code PlayerCooldown} from a string.
     * <p>
     * The expected format is:
     * <pre>{@code
     * (UUID)uuid PlayerCooldown.SEP (String)commandLabel PlayerCooldown.SEP (String)arguments PlayerCooldown.SEP
     * (double)duration PlayerCooldown.SEP (long)endOfCooldown
     * }</pre>
     * <p>
     * Note that {@code arguments} should be a single string, with different arguments separated by a single space.
     * <p>
     * Example usage:
     * <pre>{@code
     * String input = "123e4567-e89b-12d3-a456-426614174000/command/arg1 arg2/300.0/1693377400000";
     * PlayerCooldown cooldown = PlayerCooldown.fromString(input);
     * }</pre>
     *
     * @param s The string to parse.
     * @return A {@code PlayerCooldown} constructed from the provided string, or {@code null} if not enough arguments
     * are provided.
     */
    public static PlayerCooldown parsePlayerCooldown(String s) {
        String[] arr = s.split(SEP);
        if (arr.length != 5) {
            return null;
        }
        UUID uuid = UUID.fromString(arr[0]);
        String label = arr[1];
        String argsStr = arr[2];
        double duration = Double.parseDouble(arr[3]);
        long end = Long.parseLong(arr[4]);

        SavedCommand command = new SavedCommand(label);
        Cooldown cooldown = new Cooldown(command, argsStr, duration);
        return new PlayerCooldown(uuid, cooldown, end);
    }

    public UUID getPlayer() {
        return uuid;
    }

    public Cooldown getCooldown() {
        return cooldown;
    }

    public long getEnd() {
        return end;
    }

    public boolean expired() {
        return System.currentTimeMillis() > end;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String args = String.join(" ", cooldown.getArgs());

        builder.append(uuid).append(SEP).append(cooldown.getCommand().getLabel()).append(SEP).append(args).append(SEP).append(cooldown.getDuration()).append(SEP).append(end);
        return builder.toString();
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlayerCooldown)) return false;
        PlayerCooldown b = (PlayerCooldown) obj;
        return uuid.equals(b.uuid) && cooldown.equals(b.cooldown);
    }
}