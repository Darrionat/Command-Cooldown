package me.darrionat.commandcooldown.utils;

import java.util.HashMap;

public class Duration {
    private static final String SECOND = "s";
    private static final String MINUTE = "m";
    private static final String HOUR = "h";
    private static final String DAY = "d";
    private static final String WEEK = "w";
    private static final String YEAR = "y";
    private static final HashMap<String, Double> DURATION_TO_SECONDS = new HashMap<>();

    static {
        DURATION_TO_SECONDS.put(SECOND, 1.0);
        DURATION_TO_SECONDS.put(MINUTE, 60.0);
        DURATION_TO_SECONDS.put(HOUR, 3600.0);
        DURATION_TO_SECONDS.put(DAY, 86400.0);
        DURATION_TO_SECONDS.put(WEEK, 86400.0 * 7);
        DURATION_TO_SECONDS.put(YEAR, 86400.0 * 365);
    }

    /**
     * Parses the duration of a cooldown in seconds from a string.
     *
     * @param s The string to be parsed.
     * @return The cooldown, in seconds.
     * @throws NumberFormatException thrown when the duration cannot be parsed.
     */
    public static double parseDuration(String s) throws NumberFormatException {
        String unit = s.charAt(s.length() - 1) + "";
        if (!Duration.validUnit(unit))
            return Double.parseDouble(s);

        double multi = Duration.toSeconds(unit);
        // Removes the unit on the end of the string and multiplies
        double duration = Double.parseDouble(s.substring(0, s.length() - 1));
        return duration * multi;
    }

    public static boolean validUnit(String unit) {
        return DURATION_TO_SECONDS.containsKey(unit);
    }

    public static double toSeconds(String unit) {
        return DURATION_TO_SECONDS.get(unit);
    }

    public static String toDurationString(double seconds) {
        int d = (int) Math.floor(seconds / 86400);
        int h = (int) Math.floor(seconds % 86400 / 3600);
        int m = (int) Math.floor(seconds % 3600 / 60);
        int s = (int) Math.floor(seconds % 60);
        StringBuilder builder = new StringBuilder();
        if (d >= 1)
            builder.append(d).append("d").append(" ");
        if (h >= 1)
            builder.append(h).append("h").append(" ");
        if (m >= 1)
            builder.append(m).append("m").append(" ");
        if (s >= 1)
            builder.append(s).append("s");
        return builder.toString().trim();
    }
}