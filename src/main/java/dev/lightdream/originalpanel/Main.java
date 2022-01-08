package dev.lightdream.originalpanel;

import dev.lightdream.databasehandler.DatabaseMain;
import dev.lightdream.databasehandler.database.IDatabaseManager;
import dev.lightdream.databasehandler.dto.SQLConfig;
import dev.lightdream.logger.Debugger;
import dev.lightdream.logger.LoggableMain;
import dev.lightdream.logger.Logger;
import dev.lightdream.originalpanel.managers.CacheManager;
import dev.lightdream.originalpanel.managers.DatabaseManager;
import dev.lightdream.originalpanel.managers.FileManager;
import dev.lightdream.originalpanel.managers.RateLimiter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.io.File;

public class Main implements DatabaseMain, LoggableMain {

    public static Main instance;
    public SQLConfig sqlConfig;
    public FileManager fileManager;
    public DatabaseManager databaseManager;
    public CacheManager cacheManager;
    public RestEndPoints restEndPoints;
    public RateLimiter rateLimiter;
    public JDA bot;
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
        this.restEndPoints = new RestEndPoints();
        this.bot = JDABuilder.createDefault("OTI4ODExNjUzNzA3OTk3MjM0.YdeNQg.my4IrZuUjMjWTaUygb1Qqz21dlg").build();

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
    }

    public boolean debug() {
        return false; //todo configure
    }

    public void log(String log) {
        System.out.println(log);
    }

    public File getDataFolder() {
        return new File(System.getProperty("user.dir"));
    }

    @Override
    public dev.lightdream.databasehandler.dto.SQLConfig getSqlConfig() {
        return sqlConfig;
    }

    @Override
    public IDatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
