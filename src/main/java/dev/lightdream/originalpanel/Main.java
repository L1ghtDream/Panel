package dev.lightdream.originalpanel;

import dev.lightdream.databasemanager.DatabaseMain;
import dev.lightdream.databasemanager.database.IDatabaseManager;
import dev.lightdream.filemanager.FileManager;
import dev.lightdream.filemanager.FileManagerMain;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.LoggableMain;
import dev.lightdream.logger.Logger;
import dev.lightdream.originalpanel.dto.Config;
import dev.lightdream.originalpanel.dto.JDAConfig;
import dev.lightdream.originalpanel.dto.SQLConfig;
import dev.lightdream.originalpanel.managers.*;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.io.File;

public class Main implements DatabaseMain, LoggableMain, FileManagerMain {

    public static Main instance;
    public SQLConfig sqlConfig;
    public FileManager fileManager;
    public DatabaseManager databaseManager;
    public CacheManager cacheManager;
    public RestEndPoints restEndPoints;
    public RateLimiter rateLimiter;
    public JDA bot;
    public DiscordManager discordManager;
    public NotificationManager notificationManager;
    public Config config;
    public JDAConfig jdaConfig;
    @SuppressWarnings("FieldMayBeFinal")
    private boolean enabled;

    @SneakyThrows
    public Main() {
        Debugger.init(this);
        Logger.init(this);
        Main.instance = this;

        Logger.good("Starting Panel version 1.7");

        this.fileManager = new FileManager(this, FileManager.PersistType.YAML);
        loadConfigs();
        this.databaseManager = new DatabaseManager();
        this.cacheManager = new CacheManager(this);
        this.discordManager = new DiscordManager();
        this.notificationManager = new NotificationManager();

        this.restEndPoints = new RestEndPoints();
        this.bot = JDABuilder.createDefault(config.botToken).build();

        this.rateLimiter = new RateLimiter();
        Logger.good("Application started");

        enabled = true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isEnabled() {
        return enabled;
    }

    public void loadConfigs() {
        sqlConfig = fileManager.load(SQLConfig.class);
        config = fileManager.load(Config.class);
        jdaConfig = fileManager.load(JDAConfig.class);
    }

    public boolean debug() {
        return config.debug;
    }

    public void log(String log) {
        System.out.println(log);
    }

    public File getDataFolder() {
        return new File(System.getProperty("user.dir") + "/config");
    }

    @Override
    public SQLConfig getSqlConfig() {
        return sqlConfig;
    }

    @Override
    public IDatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
