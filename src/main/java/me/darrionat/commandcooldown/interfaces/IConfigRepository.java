package me.darrionat.commandcooldown.interfaces;

public interface IConfigRepository extends Repository {
    /**
     * Determines if to check for available updates.
     *
     * @return {@code true} if the plugin should check for available updates; {@code false} otherwise.
     */
    boolean checkForUpdates();

    /**
     * Determines if to send a message to a player who bypasses cooldowns.
     *
     * @return {@code true} if a player should be informed when they bypass a cooldown;
     * {@code false} otherwise.
     */
    boolean sendBypassMessage();

    /**
     * Checks if the MySQL database connection is enabled.
     *
     * @return returns {@code true} if the plugin should use MySQL for cooldown storage.
     */
    boolean databaseEnabled();

    /**
     * Gets the host of the MySQL database connection.
     *
     * @return host name.
     */
    String getDatabaseHost();

    /**
     * Gets the database name associated to the host.
     *
     * @return name to access the correct database for plugin storage.
     */
    String getDatabase();

    /**
     * Gets the username to access the database.
     *
     * @return username for database access.
     */
    String getDatabaseUsername();

    /**
     * Gets the password to access the database.
     *
     * @return password for database access.
     */
    String getDatabasePassword();

    /**
     * Gets the port to access the database.
     *
     * @return port for database access.
     */
    int getDatabasePort();
}