package me.darrionat.commandcooldown.repository;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.cooldowns.Cooldown;
import me.darrionat.commandcooldown.cooldowns.PlayerCooldown;
import me.darrionat.commandcooldown.interfaces.IConfigRepository;
import me.darrionat.commandcooldown.interfaces.IPlayerCooldownsRepository;
import me.darrionat.pluginlib.mysql.DatabaseConnection;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabasePlayerCooldownsRepository extends DatabaseConnection implements IPlayerCooldownsRepository {

    private final CommandCooldownPlugin plugin;

    private final static String PLAYER_COOLDOWNS_TABLE = "player_cooldowns";
    private final static String COMMAND_COLUMN = "CommandCooldown";

    // Basic Layout of DB is:
    // UUID PlayerCooldown
    // d8.... | PlayerCooldownString

    public DatabasePlayerCooldownsRepository(CommandCooldownPlugin plugin, IConfigRepository configRepository) {
        super(configRepository.getDatabaseHost(), configRepository.getDatabasePort(), configRepository.getDatabase(), configRepository.getDatabaseUsername(), configRepository.getDatabasePassword());
        this.plugin = plugin;
        connect(); // Initializes connection to database
        createTable(); // Creates the table if it does not exist
    }

    private void createTable() {
        try (PreparedStatement statement = prepareStatement("CREATE TABLE IF NOT EXISTS " + PLAYER_COOLDOWNS_TABLE + " (UUID char(36), " + COMMAND_COLUMN + " varchar(256))")) {
            statement.execute();
        } catch (Exception exe) {
            exe.printStackTrace();
        }
    }

    /**
     * Updates the player data to not contain expired cooldowns
     *
     * @param uuid The uuid associated with the player data.
     */
    private void removeExpiredCooldowns(UUID uuid) {
        // Create a list of all PlayerCooldowns that are expired
        List<String> toRemove = new ArrayList<>();
        try (PreparedStatement statement = prepareStatement("SELECT * FROM " + PLAYER_COOLDOWNS_TABLE + " WHERE UUID=?")) {
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                String playerCooldownAsString = results.getString(COMMAND_COLUMN);
                PlayerCooldown playerCooldown = PlayerCooldown.parsePlayerCooldown(playerCooldownAsString);
                if (playerCooldown == null) {
                    plugin.getErrorHandler().parsePlayerCooldownError(uuid);
                    continue;
                }
                if (playerCooldown.expired()) {
                    toRemove.add(playerCooldownAsString);
                }
            }
        } catch (Exception exe) {
            exe.printStackTrace();
        }

        // Delete expired cooldowns
        for (String playerCooldownAsString : toRemove) {
            try (PreparedStatement statement = prepareStatement("DELETE FROM " + PLAYER_COOLDOWNS_TABLE + " WHERE UUID=? AND " + COMMAND_COLUMN + "=?")) {
                statement.setString(1, uuid.toString());
                statement.setString(2, playerCooldownAsString);
                statement.execute();
            } catch (Exception exe) {
                exe.printStackTrace();
            }
        }
    }

    @Override
    public void init() {
        disconnect();
        connect();
    }

    @Override
    public void giveCooldown(Player p, Cooldown cooldown) {
        assert !playerHasCooldown(p, cooldown);
        if (cooldown == null) return;
        long current = System.currentTimeMillis();
        long cooldownMS = (long) (cooldown.getDuration() * 1000);
        long end = current + cooldownMS;
        PlayerCooldown playerCooldown = new PlayerCooldown(p.getUniqueId(), cooldown, end);

        try (PreparedStatement insert = prepareStatement("INSERT INTO " + PLAYER_COOLDOWNS_TABLE + "(UUID, " + COMMAND_COLUMN + ") VALUE (?,?)")) {
            insert.setString(1, p.getUniqueId().toString());
            insert.setString(2, playerCooldown.toString());
            insert.executeUpdate();
        } catch (Exception exe) {
            exe.printStackTrace();
        }
    }

    @Override
    public void removePlayerCooldowns(Player p) {
        try (PreparedStatement statement = prepareStatement("DELETE FROM " + PLAYER_COOLDOWNS_TABLE + " WHERE UUID=?")) {
            statement.setString(1, p.getUniqueId().toString());
            statement.execute();
        } catch (Exception exe) {
            exe.printStackTrace();
        }
    }

    @Override
    public double getRemainingCooldown(Player p, Cooldown cooldown) {
        return getRemainingCooldown(p.getUniqueId(), cooldown);
    }

    @Override
    public double getRemainingCooldown(UUID uuid, Cooldown cooldown) {
        PlayerCooldown cd = plugin.matchCooldownToPlayerCooldown(uuid, cooldown);
        if (cd == null) return 0;
        long end = cd.getEnd();
        long rem = end - System.currentTimeMillis();
        return Math.max(rem / 1000, 0);
    }

    @Override
    public boolean playerHasCooldown(Player p, Cooldown cooldown) {
        UUID uuid = p.getUniqueId();
        PlayerCooldown playerCooldown = plugin.matchCooldownToPlayerCooldown(uuid, cooldown);
        if (playerCooldown == null) return false;
        boolean expired = playerCooldown.expired();
        if (expired) {
            // Cooldown is expired, remove from list
            try (PreparedStatement statement = prepareStatement("DELETE FROM " + PLAYER_COOLDOWNS_TABLE + " WHERE UUID=? AND " + COMMAND_COLUMN + "=?")) {
                statement.setString(1, uuid.toString());
                statement.setString(2, playerCooldown.toString());
                statement.execute();
            } catch (Exception exe) {
                exe.printStackTrace();
            }
        }
        return !expired;
    }

    @Override
    public List<PlayerCooldown> getAllPlayerCooldownsForPlayer(Player p) {
        return getAllPlayerCooldownsForPlayer(p.getUniqueId());
    }

    @Override
    public List<PlayerCooldown> getAllPlayerCooldownsForPlayer(UUID uuid) {
        removeExpiredCooldowns(uuid);
        List<PlayerCooldown> toReturn = new ArrayList<>();

        try (PreparedStatement statement = prepareStatement("SELECT * FROM " + PLAYER_COOLDOWNS_TABLE + " WHERE UUID=?")) {
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                PlayerCooldown playerCooldown = PlayerCooldown.parsePlayerCooldown(results.getString(COMMAND_COLUMN));
                if (playerCooldown == null) {
                    plugin.getErrorHandler().parsePlayerCooldownError(uuid);
                    continue;
                }
                toReturn.add(playerCooldown);
            }
        } catch (Exception exe) {
            exe.printStackTrace();
        }
        return toReturn;
    }
}