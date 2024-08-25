package me.darrionat.commandcooldown.repository;

import me.darrionat.commandcooldown.CommandCooldownPlugin;
import me.darrionat.commandcooldown.interfaces.IMessageRepository;
import me.darrionat.pluginlib.files.Config;
import me.darrionat.pluginlib.files.ConfigBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Objects;

public class MessageRepository implements IMessageRepository {
    private final Config config;
    private FileConfiguration file;

    public MessageRepository(CommandCooldownPlugin plugin) {
        ConfigBuilder builder = new ConfigBuilder(plugin, MESSAGES);
        builder.useBuiltInFile().updateConfig();
        config = builder.build();
        init();
    }

    public void init() {
        file = config.getFileConfiguration();
    }

    @Override
    public String getMessage(String key) {
        Objects.requireNonNull(key);
        String msg = file.getString(key);
        return msg == null ? key : msg;
    }

    @Override
    public ConfigurationSection getConfigurationSection(String key) {
        return file.getConfigurationSection(key);
    }

    @Override
    public List<String> getList(String key) {
        Objects.requireNonNull(key);
        return file.getStringList(key);
    }
}